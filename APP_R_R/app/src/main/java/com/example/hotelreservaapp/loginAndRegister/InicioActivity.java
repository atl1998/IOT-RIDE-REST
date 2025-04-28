package com.example.hotelreservaapp.loginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;

public class InicioActivity extends AppCompatActivity {

    MaterialButton btnLogIn;
    TextView tvRegistro, tvPostula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Enlazar vistas
        btnLogIn = findViewById(R.id.btnLogIn);
        tvRegistro = findViewById(R.id.tvRegistro);
        tvPostula = findViewById(R.id.tvPostula);

        // Acción del botón de login
        btnLogIn.setOnClickListener(v -> {
            // Aquí iría LoginActivity
            Intent intent = new Intent(InicioActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Acción del enlace "Regístrate"
        tvRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        // Acción del enlace "Postula aquí"
        tvPostula.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, PostulacionTaxistaActivity.class);
            startActivity(intent);
        });
    }
}
