package com.example.hotelreservaapp.superadmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.hotelreservaapp.LogManager;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.adapter.LogsAdapter;
import com.example.hotelreservaapp.databinding.SuperadminPerfilFragmentBinding;
import com.example.hotelreservaapp.loginAndRegister.BottomSheetCambiarContrasena;
import com.example.hotelreservaapp.loginAndRegister.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PerfilFragment extends Fragment {

    private SuperadminPerfilFragmentBinding binding;
    private FirebaseFirestore db;
    private boolean enModoEdicion = false;
    private FirebaseUser usuarioActual;

    public PerfilFragment() {
        // Constructor vacío requerido
    }
    private ProgressBar progressBar;
    private static final int REQUEST_CAMERA = 1;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private static final int RC_GOOGLE_LINK = 2000;
    private GoogleSignInClient googleSignInClient;
    private MaterialButton btnVincularGoogle;
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
        binding = SuperadminPerfilFragmentBinding.inflate(inflater, container, false);
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
        btnVincularGoogle = view.findViewById(R.id.btnVincularGoogle);
        progressBar = binding.progressBarPhoto;


        // Configurar el cliente Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Este ID está en tu strings.xml
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        binding.llSeguridad.setOnClickListener(v -> {
            BottomSheetCambiarContrasena bottomSheet = new BottomSheetCambiarContrasena();
            bottomSheet.show(getParentFragmentManager(), "CambiarContrasena");
        });

        // Detectar si ya está vinculado con Google
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                boolean yaVinculado = false;
                for (UserInfo info : user.getProviderData()) {
                    if (info.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID)) {
                        yaVinculado = true;
                        break;
                    }
                }
                btnVincularGoogle.setVisibility(yaVinculado ? View.GONE : View.VISIBLE);

        // Botón de vincular
                btnVincularGoogle.setOnClickListener(v -> {
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_GOOGLE_LINK);
                });
        // Cargar datos del usuario desde Firestore
        if (usuarioActual != null) {
            db.collection("usuarios").document(usuarioActual.getUid()).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String nombre = document.getString("nombre");
                            Log.d("a",nombre);
                            etNombre.setText(document.getString("nombre"));
                            user_name.setText(document.getString("nombre")+ " " + document.getString("apellido"));
                            etApellido.setText(document.getString("apellido"));
                            etCorreo.setText(usuarioActual.getEmail());
                            correu.setText(usuarioActual.getEmail());
                            etDni.setText(document.getString("numeroDocumento"));
                            etTelefono.setText(document.getString("telefono"));
                            etDireccion.setText(document.getString("direccion"));
                            String urlFoto = document.getString("urlFotoPerfil");
                            if (!isAdded() || getContext() == null) return;
                            File file = new File(requireContext().getFilesDir(), "foto_perfil.jpg");
                            if (file.exists()) {
                                // ✅ Prioriza la imagen local si existe
                                binding.ivProfileImage.setImageURI(Uri.fromFile(file));
                            } else if (urlFoto != null && !urlFoto.isEmpty()) {
                                // 🌐 Si no hay imagen local, carga desde Storage
                                progressBar.setVisibility(View.VISIBLE);
                                Glide.with(this)
                                        .load(urlFoto)
                                        .placeholder(R.drawable.default_user_icon)
                                        .error(R.drawable.default_user_icon)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .into(new CustomTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                if (!isAdded() || getContext() == null) return;
                                                binding.ivProfileImage.setImageDrawable(resource);
                                                progressBar.setVisibility(View.GONE);

                                                // 💡 Guardar imagen descargada como `foto_perfil.jpg` para próxima carga offline:
                                                try {
                                                    // Convertir Drawable a Bitmap:
                                                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                                                    FileOutputStream fos = new FileOutputStream(file);
                                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                                    fos.close();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                                // Nada especial aquí
                                            }

                                            @Override
                                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                super.onLoadFailed(errorDrawable);
                                                binding.ivProfileImage.setImageDrawable(errorDrawable);
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                            } else {
                                // Fallback
                                binding.ivProfileImage.setImageResource(R.drawable.default_user_icon);
                            }
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

                etTelefono.setEnabled(true);
                etDireccion.setEnabled(true);
                etCorreo.setEnabled(true);
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
                                LogManager.registrarLogRegistro(
                                        nombre + " " + apellido,
                                        "Actualización de perfil",
                                        "El usuario actualizó sus datos personales."
                                );
                                // Desactivar modo edición
                                enModoEdicion = false;
                                btnEditar.setImageResource(R.drawable.edit_icon);

                                etCorreo.setEnabled(false);
                                etTelefono.setEnabled(false);
                                etDireccion.setEnabled(false);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });


        // Inicializar pickImageLauncher
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                guardarImagenEnArchivosInternos(imageUri);
            }
        });

        // Inicializar botón para cambiar foto
        binding.ivChangePhoto.setOnClickListener(v -> mostrarDialogoFoto());

        binding.btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // 🔐 Cierra sesión
            Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();

            //  Redirige a LoginActivity
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });


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

    private void guardarImagenEnArchivosInternos(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (!isAdded() || getContext() == null) return;
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
            binding.ivProfileImage.setImageURI(Uri.fromFile(file));
            subirImagenAFirebaseStorage(uri);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error al guardar imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void subirImagenAFirebaseStorage(Uri uri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //Ponemos nombres únicos a los jpg
        StorageReference storageRef = storage.getReference().child("fotos_perfil/" + usuarioActual.getUid() + ".jpg");

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Subiendo imagen");
        builder.setMessage("Por favor espera…");
        builder.setCancelable(false); // Evita que lo puedan cerrar

        final androidx.appcompat.app.AlertDialog progressDialog = builder.create();
        progressDialog.show();

        storageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Guardar la URL en Firestore
                        db.collection("usuarios").document(usuarioActual.getUid())
                                .update("urlFotoPerfil", downloadUri.toString())
                                .addOnSuccessListener(aVoid -> {
                                    if (isAdded() && getContext() != null) {
                                        Toast.makeText(getContext(), "Foto actualizada", Toast.LENGTH_SHORT).show();
                                    }
                                    if (!isAdded() || getContext() == null) return;
                                    File file = new File(requireContext().getFilesDir(), "foto_perfil.jpg");
                                    // 🔥 Consulta a Firestore para obtener nombre + apellido antes de registrar el log
                                    db.collection("usuarios").document(usuarioActual.getUid()).get()
                                            .addOnSuccessListener(document -> {
                                                if (document.exists()) {
                                                    String nombre = document.getString("nombre");
                                                    String apellido = document.getString("apellido");
                                                    String nombreCompleto = (nombre != null ? nombre : "") + " " + (apellido != null ? apellido : "");

                                                    LogManager.registrarLogRegistro(
                                                            nombreCompleto,
                                                            "Actualización de foto",
                                                            "El usuario " + nombreCompleto + " actualizó su foto de perfil."
                                                    );
                                                }
                                            });
                                    progressDialog.dismiss();
                                })
                                .addOnFailureListener(e -> {
                                    if (isAdded() && getContext() != null) {
                                        Toast.makeText(getContext(), "Error al guardar la foto", Toast.LENGTH_SHORT).show();
                                    }
                                    progressDialog.dismiss();
                                });
                    });
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            // Ya está guardada en cameraImageUri, solo mostrarla
            File file = new File(requireContext().getFilesDir(), "foto_perfil.jpg");
            binding.ivProfileImage.setImageURI(null);
            binding.ivProfileImage.setImageURI(Uri.fromFile(file));        }
        if (requestCode == RC_GOOGLE_LINK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.linkWithCredential(credential)
                        .addOnCompleteListener(linkTask -> {
                            if (linkTask.isSuccessful()) {
                                Toast.makeText(getContext(), "Cuenta vinculada con Google exitosamente", Toast.LENGTH_SHORT).show();
                                btnVincularGoogle.setVisibility(View.GONE); // Oculta el botón
                            } else {
                                String mensaje = linkTask.getException().getMessage();
                                if (mensaje != null && mensaje.contains("already linked")) {
                                    Toast.makeText(getContext(), "Esta cuenta de Google ya está vinculada a otro usuario", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getContext(), "Error al vincular: " + mensaje, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } catch (ApiException e) {
                Toast.makeText(getContext(), "Error al procesar cuenta de Google", Toast.LENGTH_SHORT).show();
            }
        }
    }


}