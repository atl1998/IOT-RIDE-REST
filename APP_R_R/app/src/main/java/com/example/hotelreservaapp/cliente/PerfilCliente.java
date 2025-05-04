package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PerfilCliente extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_perfil);

        BottomNavigationView bottomNav = findViewById(R.id.bottonNavigationView);
        bottomNav.setSelectedItemId(R.id.perfilCliente);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.inicioCliente) {
                startActivity(new Intent(this, HomeCliente.class));
            } else if (id == R.id.chat_cliente) {
                //startActivity(new Intent(this, ListaHotelesCliente.class));
            } else if (id == R.id.historialCliente) {
                startActivity(new Intent(this, HistorialEventos.class));
            } else if (id == R.id.perfilCliente) {
                startActivity(new Intent(this, PagoConfirmado.class));
            }

            return true;
        });
    }
}