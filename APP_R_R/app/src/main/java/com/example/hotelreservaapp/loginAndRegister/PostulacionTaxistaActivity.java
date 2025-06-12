package com.example.hotelreservaapp.loginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.databinding.ActivityPostulacionTaxistaBinding;

public class PostulacionTaxistaActivity extends AppCompatActivity {

    private ActivityPostulacionTaxistaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostulacionTaxistaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Acción para el botón de regresar
        binding.btnBack.setOnClickListener(v -> onBackPressed());

        // Acción para el botón de continuar
        binding.btnContinuar.setOnClickListener(v -> {
            // Solo pasar a la siguiente vista sin validaciones ni registro
            cargarVistaSubirFoto();
        });
    }

    // Metodo para cargar la siguiente vista (SubirFotoResgistroTaxistaActivity)
    private void cargarVistaSubirFoto() {
        // Cambiar a la actividad o vista de subir foto
        Intent intent = new Intent(PostulacionTaxistaActivity.this, SubirFotoResgistroTaxistaActivity.class);
        startActivity(intent);
    }
}
