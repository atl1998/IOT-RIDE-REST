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
import android.widget.Toast;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class PerfilFragment extends Fragment {


    private AdminhotelFragmentPerfilBinding binding;

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

        ImageView btnEditar = binding.ivEditProfile;
        TextInputEditText etNombre = binding.etNombre;
        TextInputEditText etApellido = binding.etApellido;
        TextInputEditText etCorreo = binding.etCorreo;
        TextInputEditText etDni = binding.etDni;
        TextInputEditText etTelefono = binding.etTelefono;
        TextInputEditText etDireccion = binding.etDireccion;

        final boolean[] enModoEdicion = {false};

        btnEditar.setOnClickListener(v -> {
            enModoEdicion[0] = !enModoEdicion[0];

            etNombre.setEnabled(enModoEdicion[0]);
            etApellido.setEnabled(enModoEdicion[0]);
            etDni.setEnabled(enModoEdicion[0]);
            etTelefono.setEnabled(enModoEdicion[0]);
            etDireccion.setEnabled(enModoEdicion[0]);
            etCorreo.setEnabled(false); // El correo no se edita

            if (enModoEdicion[0]) {
                btnEditar.setImageResource(R.drawable.save_icon);
            } else {
                btnEditar.setImageResource(R.drawable.edit_square_24dp_black);

                // Obtener datos ingresados
                String nombre = etNombre.getText().toString().trim();
                String apellido = etApellido.getText().toString().trim();
                String dni = etDni.getText().toString().trim();
                String telefono = etTelefono.getText().toString().trim();
                String direccion = etDireccion.getText().toString().trim();

                Toast.makeText(requireContext(), "Datos actualizados", Toast.LENGTH_SHORT).show();
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

        binding.btnCerrarSesion.setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            //requireActivity().finish();
            SharedPreferences.Editor editor = prefs.edit();
            //Borramos la sesión owo
            editor.clear();
            //C aplican los cambios
            editor.apply();
            //Toad
            Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
            // Volver al login
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            //Evitamos que el usuario pueda volver dando atrás limpiando las actividades de la pila
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
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