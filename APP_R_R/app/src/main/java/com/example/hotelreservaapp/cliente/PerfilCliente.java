package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.loginAndRegister.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PerfilCliente extends AppCompatActivity {
    // UI
    private ImageView ivProfileImage, ivChangePhoto, btnEditar;
    private TextInputEditText etNombre, etApellido, etCorreo, etDni, etTelefono, etDireccion;
    private MaterialTextView tvUserName, tvUserHandle;
    private MaterialButton btnNotificaciones, btnCerrarSesion;
    private BottomNavigationView bottomNav;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseUser usuarioActual;

    // State
    private boolean enModoEdicion = false;
    private Uri cameraImageUri;

    // Pick from gallery
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK
                        && result.getData() != null
                        && result.getData().getData() != null) {
                    guardarImagenEnArchivosInternos(result.getData().getData());
                }
            });

    // Take photo
    private final ActivityResultLauncher<Intent> takePhotoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    File file = new File(getFilesDir(), "foto_perfil.jpg");
                    ivProfileImage.setImageURI(Uri.fromFile(file));
                    uploadImageToStorage(file);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_perfil);

        // Init Firebase
        db = FirebaseFirestore.getInstance();
        usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        // Find views
        ivProfileImage  = findViewById(R.id.iv_profile_image);
        ivChangePhoto   = findViewById(R.id.iv_change_photo);
        btnEditar       = findViewById(R.id.iv_edit_profile);
        etNombre        = findViewById(R.id.etNombre);
        etApellido      = findViewById(R.id.etApellido);
        etCorreo        = findViewById(R.id.etCorreo);
        etDni           = findViewById(R.id.etDni);
        etTelefono      = findViewById(R.id.etTelefono);
        etDireccion     = findViewById(R.id.etDireccion);
        tvUserName      = findViewById(R.id.tv_user_name);
        tvUserHandle    = findViewById(R.id.tv_user_handle);
        btnNotificaciones = findViewById(R.id.notificaciones_cliente);
        btnCerrarSesion   = findViewById(R.id.btn_cerrar_sesion);
        bottomNav         = findViewById(R.id.bottonNavigationView);

        // Load Firebase data
        cargarDatosDesdeFirebase();

        // Toggle edit / save
        btnEditar.setOnClickListener(v -> {
            enModoEdicion = !enModoEdicion;
            habilitarCampos(enModoEdicion);
            btnEditar.setImageResource(
                    enModoEdicion
                            ? R.drawable.save_icon
                            : R.drawable.edit_square_24dp_black
            );
            if (!enModoEdicion) guardarDatosEnFirebase();
        });

        // Change photo
        ivChangePhoto.setOnClickListener(v -> mostrarDialogoFoto());

        // Notifications
        btnNotificaciones.setOnClickListener(v ->
                startActivity(new Intent(this, ClienteNotificaciones.class))
        );

        // Logout
        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
        });

        // Bottom nav
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.perfilCliente) return true;
            if (id == R.id.inicioCliente)    startActivity(new Intent(this, HomeCliente.class));
            if (id == R.id.historialCliente) startActivity(new Intent(this, HistorialEventos.class));
            if (id == R.id.chat_cliente)     startActivity(new Intent(this, ClienteChat.class));
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.perfilCliente);
        // Show local image if exists
        File f = new File(getFilesDir(), "foto_perfil.jpg");
        if (f.exists()) ivProfileImage.setImageURI(Uri.fromFile(f));
    }

    private void habilitarCampos(boolean habilitar) {
        etNombre.setEnabled(habilitar);
        etApellido.setEnabled(habilitar);
        etDni.setEnabled(habilitar);
        etTelefono.setEnabled(habilitar);
        etDireccion.setEnabled(habilitar);
        etCorreo.setEnabled(false);
    }

    private void cargarDatosDesdeFirebase() {
        if (usuarioActual == null) return;
        db.collection("usuarios").document(usuarioActual.getUid())
                .get().addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;
                    etNombre.setText(doc.getString("nombre"));
                    etApellido.setText(doc.getString("apellido"));
                    etCorreo.setText(usuarioActual.getEmail());
                    etDni.setText(doc.getString("numeroDocumento"));
                    etTelefono.setText(doc.getString("telefono"));
                    etDireccion.setText(doc.getString("direccion"));
                    tvUserName.setText(doc.getString("nombre") + " " + doc.getString("apellido"));
                    tvUserHandle.setText(usuarioActual.getEmail());
                    String fotoUrl = doc.getString("urlFotoPerfil");
                    if (fotoUrl != null && !fotoUrl.isEmpty()) {
                        Glide.with(this)
                                .load(fotoUrl)
                                .placeholder(R.drawable.default_profile)
                                .into(ivProfileImage);
                    }
                }).addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                );
    }

    private void guardarDatosEnFirebase() {
        if (usuarioActual == null) return;
        String n = etNombre.getText().toString().trim();
        String a = etApellido.getText().toString().trim();
        String d = etDni.getText().toString().trim();
        String t = etTelefono.getText().toString().trim();
        String dir = etDireccion.getText().toString().trim();
        if (n.isEmpty()||a.isEmpty()||d.isEmpty()||t.isEmpty()||dir.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos antes de guardar", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("usuarios").document(usuarioActual.getUid())
                .update(
                        "nombre", n,
                        "apellido", a,
                        "numeroDocumento", d,
                        "telefono", t,
                        "direccion", dir
                )
                .addOnSuccessListener(u ->
                        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                );
    }

    private void mostrarDialogoFoto() {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.superadmin_bottomsheet_foto, null);
        BottomSheetDialog dlg = new BottomSheetDialog(this);
        dlg.setContentView(view);
        view.findViewById(R.id.opcionGaleria).setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pick.setType("image/*");
            pickImageLauncher.launch(pick);
            dlg.dismiss();
        });
        view.findViewById(R.id.opcionCamara).setOnClickListener(v -> {
            File img = new File(getFilesDir(), "foto_perfil.jpg");
            cameraImageUri = FileProvider.getUriForFile(
                    this, getPackageName()+".provider", img);
            Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cam.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            takePhotoLauncher.launch(cam);
            dlg.dismiss();
        });
        dlg.show();
    }

    private void guardarImagenEnArchivosInternos(Uri uri) {
        try (InputStream in = getContentResolver().openInputStream(uri);
             FileOutputStream out = new FileOutputStream(
                     new File(getFilesDir(), "foto_perfil.jpg"))
        ) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            File f = new File(getFilesDir(), "foto_perfil.jpg");
            ivProfileImage.setImageURI(Uri.fromFile(f));
            uploadImageToStorage(f);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToStorage(File file) {
        StorageReference ref = FirebaseStorage.getInstance()
                .getReference("fotosPerfil/" + usuarioActual.getUid() + ".jpg");
        Uri uri = Uri.fromFile(file);
        ref.putFile(uri)
                .addOnSuccessListener(task ->
                        ref.getDownloadUrl().addOnSuccessListener(url ->
                                db.collection("usuarios").document(usuarioActual.getUid())
                                        .update("urlFotoPerfil", url.toString())
                                        .addOnSuccessListener(a ->
                                                Toast.makeText(this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show()
                                        )
                        )
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error subiendo la imagen", Toast.LENGTH_SHORT).show()
                );
    }
}
