package com.example.hotelreservaapp.superadmin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.adapter.LogsAdapter;
import com.example.hotelreservaapp.databinding.SuperadminPerfilFragmentBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PerfilFragment extends Fragment {

    private SuperadminPerfilFragmentBinding binding;

    public PerfilFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = SuperadminPerfilFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnEditar = binding.ivEditProfile;
        TextInputEditText etNombre = binding.etNombre;
        TextInputEditText etApellido = binding.etApellido;
        TextInputEditText etCorreo = binding.etCorreo;
        TextInputEditText etDni = binding.etDni;
        TextInputEditText etTelefono = binding.etTelefono;
        TextInputEditText etDireccion = binding.etDireccion;

        final boolean[] enModoEdicion = {false};

        btnEditar.setOnClickListener(v -> {
            enModoEdicion[0] = !enModoEdicion[0];

            etNombre.setEnabled(enModoEdicion[0]);
            etApellido.setEnabled(enModoEdicion[0]);
            etDni.setEnabled(enModoEdicion[0]);
            etTelefono.setEnabled(enModoEdicion[0]);
            etDireccion.setEnabled(enModoEdicion[0]);
            etCorreo.setEnabled(false); // El correo no se edita

            if (enModoEdicion[0]) {
                btnEditar.setImageResource(R.drawable.save_icon);
            } else {
                btnEditar.setImageResource(R.drawable.edit_square_24dp_black);

                // Obtener datos ingresados
                String nombre = etNombre.getText().toString().trim();
                String apellido = etApellido.getText().toString().trim();
                String dni = etDni.getText().toString().trim();
                String telefono = etTelefono.getText().toString().trim();
                String direccion = etDireccion.getText().toString().trim();

                Toast.makeText(requireContext(), "Datos actualizados", Toast.LENGTH_SHORT).show();
            }
        });

        //Modo Oscuro :D

        //C busca en el layout el switch
        MaterialSwitch switchModoOscuro = view.findViewById(R.id.switch_modo_oscuro);

        // Obtener SharedPreferences a partir del archivo user_prefs
        //MODE_PRIVATE: Solo la app puede leer los datos
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        //Lee la preferencia modo_oscuro y le pone valor default false si no existía
        boolean modoOscuroActivo = prefs.getBoolean("modo_oscuro", false);

        // Establece que el switch este marcado si es que está activo el modo oscuro
        switchModoOscuro.setChecked(modoOscuroActivo);

        // Listener para cambios
        switchModoOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Guardar preferencia
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("modo_oscuro", isChecked);
            editor.apply();

            // Aplicar tema
            //AppCompatDelegate cambia el modo de tema global de la app
            if (isChecked) {
                //FUERZA MODO OSCURO :D
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                //Modo claro: POR DEFECTO
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }


}