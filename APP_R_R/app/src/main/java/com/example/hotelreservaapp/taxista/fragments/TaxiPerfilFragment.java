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

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.TaxistaFragmentPerfilBinding;
import com.example.hotelreservaapp.loginAndRegister.LoginActivity;
import com.example.hotelreservaapp.taxista.VerDatosVehiculoTaxistaActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class TaxiPerfilFragment extends Fragment {

    private TaxistaFragmentPerfilBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser usuarioActual;
    private boolean enModoEdicion = false;
    private Uri cameraImageUri;

    private static final int REQUEST_CAMERA = 100;

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
                            binding.etNombre.setText(document.getString("nombre"));
                            binding.etApellido.setText(document.getString("apellido"));
                            binding.etCorreo.setText(usuarioActual.getEmail());
                            binding.etDni.setText(document.getString("numeroDocumento"));
                            binding.etTelefono.setText(document.getString("telefono"));
                            binding.etDireccion.setText(document.getString("direccion"));
                            binding.tvUserName.setText(document.getString("nombre") + " " + document.getString("apellido"));
                            binding.tvUserHandle.setText("Taxista Verificado");
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show()
                    );
        }

        // Editar perfil
        binding.ivEditProfile.setOnClickListener(v -> {
            if (!enModoEdicion) {
                enModoEdicion = true;
                binding.ivEditProfile.setImageResource(R.drawable.save_icon);

                binding.etNombre.setEnabled(true);
                binding.etApellido.setEnabled(true);
                binding.etDni.setEnabled(true);
                binding.etTelefono.setEnabled(true);
                binding.etDireccion.setEnabled(true);
            } else {
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

                            binding.etNombre.setEnabled(false);
                            binding.etApellido.setEnabled(false);
                            binding.etDni.setEnabled(false);
                            binding.etTelefono.setEnabled(false);
                            binding.etDireccion.setEnabled(false);
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                        );
            }
        });

        // Foto perfil desde almacenamiento local
        File file = new File(requireContext().getFilesDir(), "foto_perfil.jpg");
        if (file.exists()) {
            binding.ivProfileImage.setImageURI(Uri.fromFile(file));
        }

        // Cambiar foto
        binding.ivChangePhoto.setOnClickListener(v -> mostrarDialogoFoto());

        // Abrir vista de datos del vehículo
        binding.llDatosVehiculo.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), VerDatosVehiculoTaxistaActivity.class);
            startActivity(intent);
        });

        // Cerrar sesión
        binding.btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
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
            File file = new File(requireContext().getFilesDir(), "foto_perfil.jpg");
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            binding.ivProfileImage.setImageURI(Uri.fromFile(file));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error al guardar imagen", Toast.LENGTH_SHORT).show();
        }
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
