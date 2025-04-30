package com.example.hotelreservaapp.base;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.superadmin.SuperAdminActivity;
import com.example.hotelreservaapp.superadmin.SuperAdminPerfilActivity;
import com.example.hotelreservaapp.superadmin.SuperAdminReportesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseBottomNavActivity extends AppCompatActivity {
    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    protected int obtenerIdActual() {
        if (this instanceof SuperAdminActivity) {
            return R.id.nav_usuarios;
        } else if (this instanceof SuperAdminReportesActivity) {
            return R.id.nav_reportes;
        } else if (this instanceof SuperAdminPerfilActivity) {
            return R.id.nav_perfil;
        } else {
            return -1;  // Nada seleccionado
        }
    }
    protected void configurarBottomNavigation(BottomNavigationView bottomNav) {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_usuarios) {
                startActivity(new Intent(this, SuperAdminActivity.class));
                return true;
            } else if (id == R.id.nav_reportes) {
                startActivity(new Intent(this, SuperAdminReportesActivity.class));
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, SuperAdminPerfilActivity.class));
                return true;
            } else {
                return false;
            }
        });
    }
}
