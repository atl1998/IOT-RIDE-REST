package com.example.hotelreservaapp.taxista.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.TaxistaFragmentPerfilBinding;
import com.example.hotelreservaapp.loginAndRegister.LoginActivity;
import com.example.hotelreservaapp.taxista.VerDatosVehiculoTaxistaActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class TaxiPerfilFragment extends Fragment {

    private TaxistaFragmentPerfilBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser usuarioActual;
    private boolean enModoEdicion = false;
    private Uri cameraImageUri;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    guardarImagenEnArchivosInternos(imageUri);
                }
            });

    private final ActivityResultLauncher<Intent> takePhotoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    File file = new File(requireContext().getFilesDir(), "foto_perfil.jpg");
                    binding.ivProfileImage.setImageURI(Uri.fromFile(file));
                    uploadImageToStorage(file);
                }
            });

    public TaxiPerfilFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = TaxistaFragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioActual != null) {
            db.collection("usuarios").document(usuarioActual.getUid()).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            // Cargar datos de texto
                            binding.etNombre.setText(document.getString("nombre"));
                            binding.etApellido.setText(document.getString("apellido"));
                            binding.etCorreo.setText(usuarioActual.getEmail());
                            binding.etDni.setText(document.getString("numeroDocumento"));
                            binding.etTelefono.setText(document.getString("telefono"));
                            binding.etDireccion.setText(document.getString("direccion"));
                            binding.tvUserName.setText(
                                    document.getString("nombre") + " " + document.getString("apellido")
                            );
                            binding.tvUserHandle.setText("Taxista Verificado");

                            // Cargar foto desde Firebase (urlFotoPerfil)
                            String fotoUrl = document.getString("urlFotoPerfil");
                            if (fotoUrl != null && !fotoUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(fotoUrl)
                                        .placeholder(R.drawable.default_profile)
                                        .into(binding.ivProfileImage);
                            } else {
                                // Si no hay URL, buscar foto en almacenamiento interno
                                File local = new File(requireContext().getFilesDir(), "foto_perfil.jpg");
                                if (local.exists()) {
                                    binding.ivProfileImage.setImageURI(Uri.fromFile(local));
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show()
                    );
        }

        // Resto de lógica de edición, cierre de sesión y vista de vehículo...
        binding.ivEditProfile.setOnClickListener(v -> {
            if (!enModoEdicion) {
                enModoEdicion = true;
                binding.ivEditProfile.setImageResource(R.drawable.save_icon);
                habilitarCampos(true);
            } else {
                guardarDatosPerfil();
            }
        });

        binding.ivChangePhoto.setOnClickListener(v -> mostrarDialogoFoto());
        binding.llDatosVehiculo.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), VerDatosVehiculoTaxistaActivity.class));
        });

        binding.btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void habilitarCampos(boolean habilitar) {
        binding.etNombre.setEnabled(habilitar);
        binding.etApellido.setEnabled(habilitar);
        binding.etDni.setEnabled(habilitar);
        binding.etTelefono.setEnabled(habilitar);
        binding.etDireccion.setEnabled(habilitar);
    }

    private void guardarDatosPerfil() {
        String nombre = binding.etNombre.getText().toString().trim();
        String apellido = binding.etApellido.getText().toString().trim();
        String dni = binding.etDni.getText().toString().trim();
        String telefono = binding.etTelefono.getText().toString().trim();
        String direccion = binding.etDireccion.getText().toString().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() ||
                telefono.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(getContext(), "Completa todos los campos antes de guardar", Toast.LENGTH_SHORT).show();
            return;
        }

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
                    enModoEdicion = false;
                    binding.ivEditProfile.setImageResource(R.drawable.edit_icon);
                    habilitarCampos(false);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                );
    }

    private void mostrarDialogoFoto() {
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.superadmin_bottomsheet_foto, null);
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(view);

        view.findViewById(R.id.opcionGaleria).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
            dialog.dismiss();
        });

        view.findViewById(R.id.opcionCamara).setOnClickListener(v -> {
            File imageFile = new File(requireContext().getFilesDir(), "foto_perfil.jpg");
            cameraImageUri = FileProvider.getUriForFile(
                    requireContext(), requireContext().getPackageName() + ".provider", imageFile
            );
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            takePhotoLauncher.launch(cameraIntent);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void guardarImagenEnArchivosInternos(Uri uri) {
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(
                     new File(requireContext().getFilesDir(), "foto_perfil.jpg"))) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            File file = new File(requireContext().getFilesDir(), "foto_perfil.jpg");
            binding.ivProfileImage.setImageURI(Uri.fromFile(file));
            uploadImageToStorage(file);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error al guardar imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToStorage(File file) {
        StorageReference ref = FirebaseStorage.getInstance()
                .getReference("fotosPerfil/" + usuarioActual.getUid() + ".jpg");
        Uri fileUri = Uri.fromFile(file);
        ref.putFile(fileUri)
                .addOnSuccessListener(task ->
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            db.collection("usuarios").document(usuarioActual.getUid())
                                    .update("urlFotoPerfil", downloadUrl)
                                    .addOnSuccessListener(a ->
                                            Toast.makeText(getContext(), "Foto de perfil actualizada", Toast.LENGTH_SHORT).show()
                                    )
                                    .addOnFailureListener(e ->
                                            Toast.makeText(getContext(), "Error actualizando URL en base de datos", Toast.LENGTH_SHORT).show()
                                    );
                        })
                )
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error subiendo la imagen", Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public void onResume() {
        super.onResume();
        File file = new File(requireContext().getFilesDir(), "foto_perfil.jpg");
        if (file.exists()) {
            binding.ivProfileImage.setImageURI(Uri.fromFile(file));
        }
    }
}
