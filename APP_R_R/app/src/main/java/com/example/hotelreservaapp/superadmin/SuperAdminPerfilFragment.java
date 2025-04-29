package com.example.riderest.superadmin;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.riderest.R;
import com.example.riderest.databinding.SuperadminFragmentPerfilBinding;

public class SuperAdminPerfilFragment extends Fragment {

    private SuperadminFragmentPerfilBinding binding;

    public SuperAdminPerfilFragment() {
        super(R.layout.superadmin_fragment_perfil);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding = SuperadminFragmentPerfilBinding.bind(view);

        binding.btnEditar.setOnClickListener(v -> {
            binding.etNombre.setEnabled(true);
            binding.etTipoDoc.setEnabled(true);
            binding.etNumDoc.setEnabled(true);
            binding.etFechaNac.setEnabled(true);
            binding.etCorreo.setEnabled(true);
            binding.etTelefono.setEnabled(true);
            binding.etDomicilio.setEnabled(true);
        });

        // Por si quieres deshabilitar después de guardar
        // binding.btnGuardar.setOnClickListener(v -> habilitarCampos(false));
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}