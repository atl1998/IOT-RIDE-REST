package com.example.hotelreservaapp.superadmin;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.base.BaseBottomNavActivity;
import com.example.hotelreservaapp.databinding.ActivitySuperadminBinding;
import com.example.hotelreservaapp.databinding.SuperadminReportesActivityBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SuperAdminReportesActivity extends BaseBottomNavActivity {
    private SuperadminReportesActivityBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SuperadminReportesActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Uso de base pa poner la lógica en los activities
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        int currentItemId = obtenerIdActual();
        bottomNav.setSelectedItemId(currentItemId);
        configurarBottomNavigation(bottomNav);
    }
}