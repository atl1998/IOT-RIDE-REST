package com.example.riderest.superadmin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.riderest.R;
import com.example.riderest.databinding.SuperadminFragmentGestionUsuariosBinding;
import com.google.android.material.chip.Chip;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class SuperAdminGestionUsuariosFragment extends Fragment {

    private SuperadminFragmentGestionUsuariosBinding binding;
    private List<Chip> chips;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = SuperadminFragmentGestionUsuariosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chips = Arrays.asList(binding.chipAdmin, binding.chipClientes, binding.chipTaxistas);

        // Marcar por defecto
        setSelectedChip(binding.chipAdmin);

        for (Chip chip : chips) {
            chip.setOnClickListener(v -> setSelectedChip(chip));
        }

        binding.btnRegistrarUsuario.setOnClickListener(v -> {
            if (binding.chipAdmin.isChecked()) {
                NavHostFragment.findNavController(this).navigate(R.id.registrarAdmHotel);
            } else if (binding.chipClientes.isChecked()) {
                NavHostFragment.findNavController(this).navigate(R.id.registrarAdmHotel);
            } else if (binding.chipTaxistas.isChecked()) {
                NavHostFragment.findNavController(this).navigate(R.id.registrarTaxista);
            }
        });
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
