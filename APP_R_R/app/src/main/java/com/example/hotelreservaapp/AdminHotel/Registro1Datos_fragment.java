package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hotelreservaapp.AdminHotel.Model.Hotel;
import com.example.hotelreservaapp.AdminHotel.ViewModel.RegistroViewModel;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.AdminhotelRegistro1FragmentBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;


public class Registro1Datos_fragment extends Fragment {

    TextInputEditText etNombre, etDescripcion, etDepartamento, etProvincia, etDistrito, etDireccion;
    MaterialButton btnContinuar1;
    private RegistroViewModel registroViewModel;

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
            registroViewModel = new ViewModelProvider(requireActivity()).get(RegistroViewModel.class);

            if (DatosValidos()) {
                // Guardar los datos
                Hotel hotel = registroViewModel.getHotel().getValue();
                hotel.setNombre(nombre);
                hotel.setDescripcion(descripcion);
                hotel.setDepartamento(departamento);
                hotel.setProvincia(provincia);
                hotel.setDistrito(distrito);
                hotel.setDireccion(direccion);

                // Guardar los datos en el ViewModel
                registroViewModel.setHotel(hotel);

                // Navegar al siguiente fragmento
                ((RegistroHotelActivity) requireActivity()).irASiguientePaso(new Registro2Foto_fragment());
            } else {
                Toast.makeText(getContext(), "Ingresa datos validos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public boolean DatosValidos() {
        boolean valido = true;
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String departamento = etDepartamento.getText().toString().trim();
        String provincia = etProvincia.getText().toString().trim();
        String distrito = etDistrito.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();

        if (nombre.isEmpty()) {
            binding.tilNombres.setError("Campo obligatorio");
            valido = false;
        }  else binding.tilNombres.setError(null);

        if (descripcion.isEmpty()) {
            binding.tilDescripcion.setError("Campo obligatorio");
            valido = false;
        } else binding.tilDescripcion.setError(null);

        if (departamento.isEmpty()) {
            binding.tilDepartamento.setError("Campo obligatorio");
            valido = false;
        } else binding.tilDepartamento.setError(null);

        if (provincia.isEmpty()) {
            binding.tilProvincia.setError("Campo obligatorio");
            valido = false;
        } else binding.tilProvincia.setError(null);

        if (distrito.isEmpty()) {
            binding.tilDistrito.setError("Campo obligatorio");
            valido = false;
        } else binding.tilDistrito.setError(null);

        if (direccion.isEmpty()) {
            binding.tilDireccion.setError("Campo obligatorio");
            valido = false;
        } else binding.tilDireccion.setError(null);



        return valido;
    }
}