package com.example.hotelreservaapp.AdminHotel.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.hotelreservaapp.AdminHotel.Model.Habitacion;
import com.example.hotelreservaapp.AdminHotel.Model.Hotel;
import com.example.hotelreservaapp.AdminHotel.RegistroHotelActivity;
import com.example.hotelreservaapp.AdminHotel.ViewModel.RegistroViewModel;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.AdminhotelRegistro4FragmentBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;


public class Registro4AddHabitacion_fragment extends Fragment {

    private AdminhotelRegistro4FragmentBinding binding;
    private MaterialButton btnContinuar4;
    private RegistroViewModel registroViewModel;
    private ImageButton btnBack;

    private static final int REQUEST_CAMERA = 1;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Habitacion habitacion = new Habitacion();
    private Uri cameraImageUri;
    private final ActivityResultLauncher<Intent> takePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    File file = new File(requireContext().getFilesDir(), "foto_habitacion.jpg");
                    binding.ivProfileImage.setImageURI(null);
                    binding.ivProfileImage.setImageURI(Uri.fromFile(file));
                }
            }
    );
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = AdminhotelRegistro4FragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final boolean[] enModoEdicion = {false};

        // Mostrar imagen si ya está guardada
        File file = new File(requireContext().getFilesDir(), "foto_habitacion.jpg");
        if (file.exists()) {
            binding.ivProfileImage.setImageURI(Uri.fromFile(file));
            binding.buttonOpenCamera.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.edit_square_24dp_black));
            binding.buttonOpenCamera.setText("Editar foto");
        }

        // Inicializar pickImageLauncher
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                guardarImagenEnArchivosInternos(imageUri);
            }
        });

        // Inicializar botón para cambiar foto
        binding.buttonOpenCamera.setOnClickListener(v -> mostrarDialogoFoto());

        //Lógica para continuar
        btnContinuar4 = binding.btnContinuar4;
        btnContinuar4.setOnClickListener(v -> {

            //Logica para instanciar el viewmodel
            registroViewModel = new ViewModelProvider(requireActivity()).get(RegistroViewModel.class);

            if (datosValidos()) {
                // Guardar los datos en el ViewModelregistroViewModel.setNombre(nombre);
                Hotel hotel = registroViewModel.getHotel().getValue();
                List<Habitacion> lista = hotel.getHabitaciones();
                lista.add(habitacion);
                hotel.setHabitaciones(lista);
                registroViewModel.setHotel(hotel);
                // Navegar al siguiente fragmento
                ((RegistroHotelActivity) requireActivity()).irASiguientePaso(new Registro3Habitaciones_fragment());
                Toast.makeText(getContext(), "Habitación añadida", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Ingresa los datos correctamente", Toast.LENGTH_SHORT).show();
            }
        });

        //Ir para atrás
        btnBack = binding.btnBack;
        btnBack.setOnClickListener(v -> {
            ((RegistroHotelActivity) requireActivity()).irASiguientePaso(new Registro3Habitaciones_fragment());
        });

    }

    private void guardarImagenEnArchivosInternos(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            File file = new File(requireContext().getFilesDir(), "foto_habitacion.jpg");
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
            File imageFile = new File(requireContext().getFilesDir(), "foto_habitacion.jpg");
            cameraImageUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", imageFile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            takePhotoLauncher.launch(cameraIntent);
            dialog.dismiss();
        });

        dialog.show();
    }

    private boolean datosValidos() {
        boolean valido = true;
        String tipo = binding.etTipo.getText().toString();
        String precioText = binding.etPrecio.getText().toString();
        String tamanoText = binding.etTamaO.getText().toString();
        String capacidad = binding.etCapacidad.getText().toString();
        File file = new File(requireContext().getFilesDir(), "foto_habitacion.jpg");

        //Valdación de formulario
        if (tipo.isEmpty()) {
            binding.tilTipo.setError("Campo obligatorio");
            valido = false;
        }  else binding.tilTipo.setError(null);

        if (precioText.isEmpty()) {
            binding.tilPrecio.setError("Campo obligatorio");
            valido = false;
        } else {
            try {
                double precio = Double.parseDouble(precioText);

                if (precio < 0) {
                    binding.tilPrecio.setError("El precio no puede ser negativo");
                    valido = false;
                } else binding.tilPrecio.setError(null);
            } catch (NumberFormatException e) {
                binding.tilPrecio.setError("Ingresa un número válido");
                valido = false;
            }
        }

        if (tamanoText.isEmpty()) {
            binding.tilTamaO.setError("Campo obligatorio");
            valido = false;
        } else {
            try {
                double tamano = Double.parseDouble(tamanoText);

                if (tamano < 0) {
                    binding.tilTamaO.setError("El tamaño no puede ser negativo");
                    valido = false;
                } else binding.tilTamaO.setError(null);
            } catch (NumberFormatException e) {
                binding.tilTamaO.setError("Ingresa un número válido");
                valido = false;
            }
        }

        if (capacidad.isEmpty()) {
            binding.tilCapacidad.setError("Campo obligatorio");
            valido = false;
        }  else binding.tilCapacidad.setError(null);

        if(file.exists() && valido) {
            habitacion.setTipo(tipo);
            habitacion.setPrecio(Double.parseDouble(precioText));
            habitacion.setTamano(Double.parseDouble(tamanoText));
            habitacion.setCapacidad(capacidad);
            habitacion.setUrl("foto_habitacion.jpg");
            return true;
        } else {
            Toast.makeText(getContext(), "Ingresa una foto", Toast.LENGTH_SHORT).show();
            valido = false;
        }
        return valido;
    }
}