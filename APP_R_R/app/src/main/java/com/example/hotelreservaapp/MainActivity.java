package com.example.hotelreservaapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.loginAndRegister.InicioActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    // Declaración de botones
    MaterialButton btnCliente, btnTaxista, btnAdminHotel, btnSuperadmin, btnInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enlazar los botones con el layout
        btnCliente = findViewById(R.id.btnCliente);
        btnTaxista = findViewById(R.id.btnTaxista);
        btnAdminHotel = findViewById(R.id.btnAdminHotel);
        btnSuperadmin = findViewById(R.id.btnSuperadmin);
        btnInicio = findViewById(R.id.btnInicio);

        // Acciones por botón (por ahora sin abrir otra Activity)
        btnCliente.setOnClickListener(v -> {
            // Ejemplo: ir a ClienteActivity
            // startActivity(new Intent(this, ClienteActivity.class));
        });

        btnTaxista.setOnClickListener(v -> {
            // startActivity(new Intent(this, TaxistaActivity.class));
        });

        btnAdminHotel.setOnClickListener(v -> {
            // startActivity(new Intent(this, AdminHotelActivity.class));
        });

        btnSuperadmin.setOnClickListener(v -> {
            // startActivity(new Intent(this, SuperadminActivity.class));
        });

        btnInicio.setOnClickListener(v -> {
            startActivity(new Intent(this, InicioActivity.class));
        });
    }
}
