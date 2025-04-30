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
import com.example.hotelreservaapp.databinding.SuperadminRegistrarTaxistaActivityBinding;

import java.util.Calendar;
import java.util.Locale;

public class RegistrarTaxistaActivity extends AppCompatActivity {

    private SuperadminRegistrarTaxistaActivityBinding binding;
    private final Calendar calendar = Calendar.getInstance();

    private final ActivityResultLauncher<Intent> pickImageUsuarioLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    binding.imagePreview1.setVisibility(View.VISIBLE);
                    binding.imagePreview1.setImageURI(uri);
                    binding.btnSubirFotoUsuario.setVisibility(View.GONE);   // ocultar "Subir foto"
                    binding.btnEditarFotoUsuario.setVisibility(View.VISIBLE); // mostrar "Editar foto"
                }
            }
    );

    private final ActivityResultLauncher<Intent> pickImageAutoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri2 = result.getData().getData();
                    binding.imagePreview2.setVisibility(View.VISIBLE);
                    binding.imagePreview2.setImageURI(uri2);
                    binding.btnSubirFotoAuto.setVisibility(View.GONE);   // ocultar "Subir foto"
                    binding.btnEditarFotoAuto.setVisibility(View.VISIBLE); // mostrar "Editar foto"
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SuperadminRegistrarTaxistaActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configurarBotonBack();
        configurarCampoFechaNacimiento();
        configurarBotonRegistro();
        binding.btnSubirFotoUsuario.setOnClickListener(v -> lanzarSelectorDeImagenUsuario());
        binding.btnEditarFotoUsuario.setOnClickListener(v -> lanzarSelectorDeImagenUsuario());
        binding.btnSubirFotoAuto.setOnClickListener(v -> lanzarSelectorDeImagenAuto());
        binding.btnEditarFotoAuto.setOnClickListener(v -> lanzarSelectorDeImagenAuto());
    }

    private void configurarBotonBack() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void configurarCampoFechaNacimiento() {
        binding.etNacimiento.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(this, (view, y, m, d) -> {
                String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y);
                binding.etNacimiento.setText(fecha);
            }, year, month, day).show();
        });
    }

    private void lanzarSelectorDeImagenUsuario() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageUsuarioLauncher.launch(intent);
    }

    private void lanzarSelectorDeImagenAuto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageAutoLauncher.launch(intent);
    }

    private void configurarBotonRegistro() {
        binding.btnRegistrar.setOnClickListener(v -> finish());
    }
}