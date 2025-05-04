package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.hotelreservaapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class HomeCliente extends AppCompatActivity {
    MaterialButton btnBusqueda;
    private HotelCarouselManager carouselManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottonNavigationView);
        bottomNav.setSelectedItemId(R.id.inicioCliente);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.inicioCliente) {
                startActivity(new Intent(this, HomeCliente.class));
            } else if (id == R.id.chat_cliente) {
                startActivity(new Intent(this, ClienteChat.class));
            } else if (id == R.id.historialCliente) {
                startActivity(new Intent(this, HistorialEventos.class));
            } else if (id == R.id.perfilCliente) {
                startActivity(new Intent(this, PagoConfirmado.class));
            }

            return true;
        });

        btnBusqueda = findViewById(R.id.buscar);
        btnBusqueda.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            startActivity(new Intent(this, ListaHotelesCliente.class));
        });
    }
}