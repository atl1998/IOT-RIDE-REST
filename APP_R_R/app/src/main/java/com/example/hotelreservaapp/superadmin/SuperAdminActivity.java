package com.example.hotelreservaapp.superadmin;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.base.BaseBottomNavActivity;
import com.example.hotelreservaapp.databinding.ActivitySuperadminBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;

import java.util.Arrays;
import java.util.List;

public class SuperAdminActivity extends BaseBottomNavActivity {

    private ActivitySuperadminBinding binding;
    private List<Chip> chips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chips = Arrays.asList(binding.chipAdmin, binding.chipClientes, binding.chipTaxistas);
        setSelectedChip(binding.chipAdmin); // Por defecto

        for (Chip chip : chips) {
            chip.setOnClickListener(v -> setSelectedChip(chip));
        }

        binding.btnRegistrarUsuario.setOnClickListener(v -> {
            if (binding.chipAdmin.isChecked()) {
                startActivity(new Intent(this, RegistrarAdmHotelActivity.class));
            } else if (binding.chipClientes.isChecked()) {
                startActivity(new Intent(this, RegistrarClienteActivity.class));
            } else if (binding.chipTaxistas.isChecked()) {
                startActivity(new Intent(this, RegistrarTaxistaActivity.class));
            }
        });

        binding.btnSolicitudes.setOnClickListener(v -> {
            startActivity(new Intent(this, SolicitudesActivity.class));
        });
        //Uso de base pa poner la lógica en los activities
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        binding.bottomNavigationView.setItemRippleColor(ColorStateList.valueOf(Color.TRANSPARENT));
        binding.bottomNavigationView.setItemBackground(null);
        binding.bottomNavigationView.setStateListAnimator(null);
        configurarBottomNavigation(bottomNav);
    }

    private void setSelectedChip(Chip selected) {
        for (Chip chip : chips) {
            chip.setChecked(false);
        }
        selected.setChecked(true);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.tvListaUsuarios.getLayoutParams();

        if (binding.chipTaxistas.isChecked()) {
            binding.btnSolicitudes.setVisibility(View.VISIBLE);
            params.topToBottom = binding.btnSolicitudes.getId(); // 👉 Ancla a btnSolicitudes
        } else {
            binding.btnSolicitudes.setVisibility(View.GONE);
            params.topToBottom = binding.btnRegistrarUsuario.getId(); // 👉 Ancla a btnRegistrarUsuario
        }

        binding.tvListaUsuarios.setLayoutParams(params);

    }

}