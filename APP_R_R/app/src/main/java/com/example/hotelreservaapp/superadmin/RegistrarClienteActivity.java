package com.example.hotelreservaapp.superadmin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.SuperadminRegistrarAdmHotelActivityBinding;
import com.example.hotelreservaapp.databinding.SuperadminRegistrarClienteActivityBinding;

import java.util.Calendar;
import java.util.Locale;

public class RegistrarClienteActivity extends AppCompatActivity {

    private SuperadminRegistrarClienteActivityBinding binding;
    private final Calendar calendar = Calendar.getInstance();

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    binding.imagePreview.setVisibility(View.VISIBLE);
                    binding.imagePreview.setImageURI(selectedImageUri);
                    binding.btnSubirFoto.setVisibility(View.GONE);   // ocultar "Subir foto"
                    binding.btnEditarFoto.setVisibility(View.VISIBLE); // mostrar "Editar foto"
                    Toast.makeText(this, "Imagen seleccionada correctamente", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SuperadminRegistrarClienteActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarBotonBack();
        configurarBotonRegistro();
        configurarCampoFechaNacimiento();
        binding.btnSubirFoto.setOnClickListener(v -> lanzarSelectorDeImagen());
        binding.btnEditarFoto.setOnClickListener(v -> lanzarSelectorDeImagen());
    }

    // Botón de retroceso
    private void configurarBotonBack() {
        binding.btnBack.setOnClickListener(v -> finish());
    }
    //Cambiará pronto
    private void configurarBotonRegistro() {
        binding.btnRegistrar.setOnClickListener(v -> finish());
    }
    // Lógica de campo Fecha de Nacimiento
    private void configurarCampoFechaNacimiento() {
        binding.etFechaNacimiento.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
                String fechaFormateada = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                        selectedDay, selectedMonth + 1, selectedYear);
                binding.etFechaNacimiento.setText(fechaFormateada);
            }, year, month, day).show();
        });
    }

    // Subir foto desde galería
    private void lanzarSelectorDeImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }
}