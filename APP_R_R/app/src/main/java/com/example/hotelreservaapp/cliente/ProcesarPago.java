package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class ProcesarPago extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_procesar_pago);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.inicioCliente) {
                startActivity(new Intent(this, HomeCliente.class));
                return true;
            } else if (id == R.id.chat_cliente) {
                startActivity(new Intent(this, ClienteChat.class));
                return true;
            } else if (id == R.id.historialCliente) {
                startActivity(new Intent(this, HistorialEventos.class));
                return true;
            } else if (id == R.id.perfilCliente) {
                startActivity(new Intent(this, PerfilCliente.class));
                return true;
            }
            return false;
        });
        MaterialButton btnNotificaciones = findViewById(R.id.notificaciones_cliente);
        btnNotificaciones.setOnClickListener(v -> {
            Intent intent = new Intent(this, ClienteNotificaciones.class);
            startActivity(intent);
        });
        Button btnrealizarpago = findViewById(R.id.btn_realizar_pago);
        btnrealizarpago.setOnClickListener(v -> {
            Intent intent = new Intent(this, PagoConTarjeta.class);
            startActivity(intent);
        });
    }
}