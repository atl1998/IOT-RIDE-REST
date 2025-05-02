package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;

public class DetallesHotel extends AppCompatActivity {
    MaterialButton btnBusqueda;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_detalles_hotel);

        btnBusqueda = findViewById(R.id.btnChooseRoom);
        btnBusqueda.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            startActivity(new Intent(this, ListaHabitaciones.class));
        });
    }
}