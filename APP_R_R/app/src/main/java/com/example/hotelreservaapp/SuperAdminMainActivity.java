package com.example.hotelreservaapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.hotelreservaapp.superadmin.GestionUsuariosFragment;
import com.example.hotelreservaapp.superadmin.PerfilFragment;
import com.example.hotelreservaapp.superadmin.ReportesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SuperAdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_main_activity);

        // Mostrar el fragmento inicial por defecto
        if (savedInstanceState == null) {
            loadFragment(new GestionUsuariosFragment());
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationSA);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();

            if (itemId == R.id.menu_inicio) {
                selectedFragment = new GestionUsuariosFragment();
            } else if (itemId == R.id.menu_reportes) {
                selectedFragment = new ReportesFragment();
            } else if (itemId == R.id.menu_perfil) {
                selectedFragment = new PerfilFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}