package com.example.hotelreservaapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.cliente.DetallesReserva;
import com.example.hotelreservaapp.cliente.HistorialEventos;
import com.example.hotelreservaapp.cliente.HomeCliente;
import com.example.hotelreservaapp.cliente.ListaHotelesCliente;
import com.example.hotelreservaapp.loginAndRegister.InicioActivity;
import com.example.hotelreservaapp.superadmin.SuperAdminActivity;
import com.example.hotelreservaapp.taxista.TaxistaMain;
import com.example.hotelreservaapp.taxista.fragments.TaxiInicioFragment;
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
            //por ahora directamente al mio bala
            startActivity(new Intent(this, HomeCliente.class));
        });

        btnTaxista.setOnClickListener(v -> {
            startActivity(new Intent(this, TaxistaMain.class));
        });

        btnAdminHotel.setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.hotelreservaapp.AdminHotel.MainActivity.class));
        });

        btnSuperadmin.setOnClickListener(v -> {
            startActivity(new Intent(this, SuperAdminActivity.class));
        });

        btnInicio.setOnClickListener(v -> {
            startActivity(new Intent(this, InicioActivity.class));
        });
    }
}
