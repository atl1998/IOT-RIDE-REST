package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.cliente.Hotel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminhotel_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.adminhotel_menu_inicio) {
                loadFragment(new InicioFragment());
            } else if (id == R.id.adminhotel_menu_hotel) {
                loadFragment(new HotelFragment());
            } else if (id == R.id.adminhotel_menu_reportes) {
                loadFragment(new ReportesFragment());
            } else if (id == R.id.adminhotel_menu_perfil) {
                loadFragment(new PerfilFragment());
            }
            return true;
        });



        // Cargar el fragmento inicial
        if (savedInstanceState == null) {
            loadFragment(new InicioFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.adminhotel_container_view, fragment);
        transaction.addToBackStack(null); // Esto permite navegar entre los fragmentos sin perderlos
        transaction.commit();
    }
}