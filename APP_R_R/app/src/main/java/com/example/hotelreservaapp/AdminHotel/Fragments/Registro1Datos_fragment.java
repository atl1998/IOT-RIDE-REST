package com.example.hotelreservaapp.AdminHotel.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.hotelreservaapp.AdminHotel.Model.Hotel;
import com.example.hotelreservaapp.AdminHotel.RegistroHotelActivity;
import com.example.hotelreservaapp.AdminHotel.ViewModel.RegistroViewModel;
import com.example.hotelreservaapp.AdminHotel.ViewModel.UbigeoViewModel;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.AdminhotelRegistro1FragmentBinding;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;


public class Registro1Datos_fragment extends Fragment {

    TextInputEditText etNombre, etDescripcion,  etDireccion, etContacto;
    AutoCompleteTextView etDepartamento, etProvincia, etDistrito;
    MaterialButton btnContinuar1;
    private RegistroViewModel registroViewModel;

    private AdminhotelRegistro1FragmentBinding binding;
    private UbigeoViewModel vm;

    private ArrayAdapter<String> depAdapter;
    private ArrayAdapter<String> provAdapter, distAdapter;

    private String[] depCodes;
    private String depSel, provSel;

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
        etContacto = binding.etContacto;
        btnContinuar1 = binding.btnContinuar1;

        depCodes = getResources().getStringArray(R.array.dep_codigos);

        String[] depNames = getResources().getStringArray(R.array.dep_nombres);
        /* ----- Adapter de Departamentos ----- */
        depAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                depNames);
        etDepartamento.setAdapter(depAdapter);


        /* ----- ViewModel ----- */
        vm = new ViewModelProvider(this).get(UbigeoViewModel.class);

        /* DEP clic → llenar provincias */
        etDepartamento.setOnItemClickListener((p, v1, pos, id) -> {
            depSel = depCodes[pos];
            vm.setDepartamento(depSel);
            etProvincia.setText(""); etDistrito.setText("");
        });

        /* Provincias LiveData */
        vm.getProvincias().observe(getViewLifecycleOwner(), list -> {
            ArrayAdapter<String> provAd = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_list_item_1,
                    list.stream().map(UbigeoViewModel.Par::nombre).toList());
            etProvincia.setAdapter(provAd);
        });

        /* PROV clic → llenar distritos */
        etProvincia.setOnItemClickListener((p, v12, pos, id) -> {
            UbigeoViewModel.Par par =
                    vm.getProvincias().getValue().get(pos);
            provSel = par.codigo();
            vm.setProvincia(depSel, provSel);
            etDistrito.setText("");
        });

        /* Distritos LiveData */
        vm.getDistritos().observe(getViewLifecycleOwner(), list -> {
            ArrayAdapter<String> distAd = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_list_item_1,
                    list.stream().map(UbigeoViewModel.Par::nombre).toList());
            etDistrito.setAdapter(distAd);
        });



        btnContinuar1.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            String departamento = etDepartamento.getText().toString().trim();
            String provincia = etProvincia.getText().toString().trim();
            String distrito = etDistrito.getText().toString().trim();
            String direccion = etDireccion.getText().toString().trim();
            String contacto = etContacto.getText().toString().trim();
            boolean incluyeTaxi = binding.checkboxTaxi.isChecked();

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
                hotel.setContacto(contacto);
                hotel.setServicioTaxi(incluyeTaxi);

                // Guardar los datos en el ViewModel
                registroViewModel.setHotel(hotel);

                // Navegar al siguiente fragmento
                ((RegistroHotelActivity) requireActivity()).irASiguientePaso(new Registro2Foto_fragment());
            } else {
                Toast.makeText(getContext(), "Ingresa datos validos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /* Obtener códigos seleccionados */
    public @Nullable String getUbigeo() {
        if (etDistrito.getText().toString().isEmpty()) return null;
        String distCode = vm.getDistritos().getValue().stream()
                .filter(p -> p.nombre().equals(etDistrito.getText().toString()))
                .findFirst().map(UbigeoViewModel.Par::codigo).orElse(null);
        return depSel + "-" + provSel + "-" + distCode;
    }

    public boolean DatosValidos() {
        boolean valido = true;
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String departamento = etDepartamento.getText().toString().trim();
        String provincia = etProvincia.getText().toString().trim();
        String distrito = etDistrito.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String contacto = etContacto.getText().toString().trim();

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

        if (contacto.isEmpty()) {
            binding.tilContacto.setError("Campo obligatorio");
            valido = false;
        } else if (!contacto.matches("^9\\d{8}$")) {
            // Regex: 9 seguido de 8 dígitos más (total 9 dígitos)
            binding.tilContacto.setError("Ingresa un celular válido (9 dígitos, inicia con 9)");
            valido = false;
        } else {
            binding.tilContacto.setError(null);
        }


        return valido;
    }
}