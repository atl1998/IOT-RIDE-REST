package com.example.hotelreservaapp.AdminHotel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotelreservaapp.AdminHotel.ViewModel.RegistroViewModel;
import com.example.hotelreservaapp.MainActivity;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.cliente.HomeCliente;
import com.example.hotelreservaapp.databinding.AdminhotelFragmentInicioBinding;
import com.example.hotelreservaapp.databinding.AdminhotelFragmentPerfilBinding;
import com.example.hotelreservaapp.databinding.SuperadminPerfilFragmentBinding;
import com.example.hotelreservaapp.loginAndRegister.LoginActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class PerfilFragment extends Fragment {


    private AdminhotelFragmentPerfilBinding binding;

    private FirebaseFirestore db;
    private boolean enModoEdicion = false;
    private FirebaseUser usuarioActual;

    public PerfilFragment() {
        // Constructor vacío requerido
    }

    private static final int REQUEST_CAMERA = 1;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri cameraImageUri;
    private final ActivityResultLauncher<Intent> takePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    File file = new File(requireContext().getFilesDir(), "foto_perfil.jpg");
                    binding.ivProfileImage.setImageURI(null);
                    binding.ivProfileImage.setImageURI(Uri.fromFile(file));
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = AdminhotelFragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        ImageView btnEditar = binding.ivEditProfile;
        TextInputEditText etNombre = binding.etNombre;
        TextInputEditText etApellido = binding.etApellido;
        TextInputEditText etCorreo = binding.etCorreo;
        TextInputEditText etDni = binding.etDni;
        TextInputEditText etTelefono = binding.etTelefono;
        TextInputEditText etDireccion = binding.etDireccion;
        TextView user_name = binding.tvUserName;
        TextView correu = binding.tvUserHandle;

        // Cargar datos del usuario desde Firestore
        if (usuarioActual != null) {
            db.collection("usuarios").document(usuarioActual.getUid()).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            etNombre.setText(document.getString("nombre"));
                            user_name.setText(document.getString("nombre")+ " " + document.getString("apellido"));
                            etApellido.setText(document.getString("apellido"));
                            etCorreo.setText(usuarioActual.getEmail());
                            correu.setText(usuarioActual.getEmail());
                            etDni.setText(document.getString("numeroDocumento"));
                            etTelefono.setText(document.getString("telefono"));
                            etDireccion.setText(document.getString("direccion"));
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show()
                    );
        }

        btnEditar.setOnClickListener(v -> {
            if (!enModoEdicion) {
                // Activar modo edición
                enModoEdicion = true;
                btnEditar.setImageResource(R.drawable.save_icon);

                etNombre.setEnabled(true);
                etApellido.setEnabled(true);
                etDni.setEnabled(true);
                etTelefono.setEnabled(true);
                etDireccion.setEnabled(true);
            } else {
                // Obtener valores actualizados
                String nombre = etNombre.getText().toString().trim();
                String apellido = etApellido.getText().toString().trim();
                String dni = etDni.getText().toString().trim();
                String telefono = etTelefono.getText().toString().trim();
                String direccion = etDireccion.getText().toString().trim();

                // Validaciones
                if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() ||
                        telefono.isEmpty() || direccion.isEmpty()) {
                    Toast.makeText(getContext(), "Completa todos los campos antes de guardar", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Guardar en Firestore
                if (usuarioActual != null) {
                    db.collection("usuarios").document(usuarioActual.getUid())
                            .update(
                                    "nombre", nombre,
                                    "apellido", apellido,
                                    "numeroDocumento", dni,
                                    "telefono", telefono,
                                    "direccion", direccion
                            )
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();

                                // Desactivar modo edición
                                enModoEdicion = false;
                                btnEditar.setImageResource(R.drawable.edit_icon);

                                etNombre.setEnabled(false);
                                etApellido.setEnabled(false);
                                etDni.setEnabled(false);
                                etTelefono.setEnabled(false);
                                etDireccion.setEnabled(false);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });


        // Mostrar imagen si ya está guardada
        File file = new File(requireContext().getFilesDir(), "foto_perfil.jpg");
        if (file.exists()) {
            binding.ivProfileImage.setImageURI(Uri.fromFile(file));
        }

        // Inicializar pickImageLauncher
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                guardarImagenEnArchivosInternos(imageUri);
            }
        });

        // Inicializar botón para cambiar foto
        binding.ivChangePhoto.setOnClickListener(v -> mostrarDialogoFoto());

        // Inicializar botón para cerrar sesión
        binding.btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // 🔐 Cierra sesión
            Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();

            //  Redirige a LoginActivity
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });


    }

    private void guardarImagenEnArchivosInternos(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            File file = new File(requireContext().getFilesDir(), "foto_perfil.jpg");
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            binding.ivProfileImage.setImageURI(null); // Forzar reinicialización
            binding.ivProfileImage.setImageURI(Uri.fromFile(file));        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error al guardar imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoFoto() {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.superadmin_bottomsheet_foto, null);

        final BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(view);

        view.findViewById(R.id.opcionGaleria).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
            dialog.dismiss();
        });

        view.findViewById(R.id.opcionCamara).setOnClickListener(v -> {
            File imageFile = new File(requireContext().getFilesDir(), "foto_perfil.jpg");
            cameraImageUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", imageFile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            takePhotoLauncher.launch(cameraIntent);
            dialog.dismiss();
        });

        dialog.show();
    }
}