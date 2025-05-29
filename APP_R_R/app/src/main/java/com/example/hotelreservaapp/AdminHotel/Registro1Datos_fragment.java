package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.AdminhotelRegistro1FragmentBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;


public class Registro1Datos_fragment extends Fragment {

    TextInputEditText etNombre, etDescripcion, etDepartamento, etProvincia, etDistrito, etDireccion;
    MaterialButton btnContinuar1;

    private AdminhotelRegistro1FragmentBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = AdminhotelRegistro1FragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

         etNombre = binding.etNombre;
         etDescripcion = binding.etDescripcion;
         etDepartamento = binding.etDepartamento;
         etProvincia = binding.etProvincia;
         etDistrito = binding.etDistrito;
         etDireccion = binding.etDireccion;
         btnContinuar1 = binding.btnContinuar1;


        btnContinuar1.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            String departamento = etDepartamento.getText().toString().trim();
            String provincia = etProvincia.getText().toString().trim();
            String distrito = etDistrito.getText().toString().trim();
            String direccion = etDireccion.getText().toString().trim();

            //Logica para instanciar el viewmodel
            //registroViewModel = new ViewModelProvider(requireActivity()).get(RegistroViewModel.class);

            if (DatosValidos()) {
                // Guardar los datos en el ViewModelregistroViewModel.setNombre(nombre);
                // Navegar al siguiente fragmento


                ((RegistroHotelActivity) requireActivity()).irASiguientePaso(new Registro2Foto_fragment());
            } else {
                Toast.makeText(getContext(), "Ingresa datos validos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public boolean DatosValidos() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String departamento = etDepartamento.getText().toString().trim();
        String provincia = etProvincia.getText().toString().trim();
        String distrito = etDistrito.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();

        if (nombre.isEmpty() || descripcion.isEmpty() || departamento.isEmpty() || provincia.isEmpty() || distrito.isEmpty() || direccion.isEmpty()) {
            return false;
        }

        return true;
    }
}