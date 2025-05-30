package com.example.hotelreservaapp.AdminHotel;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hotelreservaapp.AdminHotel.ViewModel.RegistroViewModel;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.adapter.Adminhotel_UsuarioAdapter;
import com.example.hotelreservaapp.databinding.AdminhotelRegistro2FragmentBinding;
import com.example.hotelreservaapp.databinding.AdminhotelRegsitro3FragmentBinding;
import com.example.hotelreservaapp.model.UsuarioListaSuperAdmin;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;


public class Registro3Habitaciones_fragment extends Fragment {

    private AdminhotelRegsitro3FragmentBinding binding;

    private RegistroViewModel registroViewModel;
    private RecyclerView rvHabitaciones;
    private com.example.hotelreservaapp.AdminHotel.HabitacionAdapter adapter;
    private List<Habitaciones> listaHabitaciones;

    private MaterialButton btnContinuar3;
    private ImageView btnAdd;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = AdminhotelRegsitro3FragmentBinding.inflate(inflater, container, false);
        registroViewModel = new ViewModelProvider(requireActivity()).get(RegistroViewModel.class);

        listaHabitaciones = new ArrayList<>(); // inicial vac

        // Configurar el RecyclerView
        rvHabitaciones = binding.listaHabitaciones;
        rvHabitaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHabitaciones.setHasFixedSize(true); // Usa un LinearLayoutManager
        adapter = new HabitacionAdapter(listaHabitaciones,getContext(),  new HabitacionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Habitaciones habitacion = listaHabitaciones.get(position);
                /*mostrarPopupSeleccion(habitacion);*/
            }

            @Override
            public void onSeleccionCambio() {
                /*verificarSeleccion();*/
            }
        });
        rvHabitaciones.setAdapter(adapter);

        // Observa LiveData para actualizar lista y adaptador
        registroViewModel.getListHabitaciones().observe(getViewLifecycleOwner(), habitaciones -> {
            if (habitaciones != null) {
                listaHabitaciones.clear();
                listaHabitaciones.addAll(habitaciones);
                adapter.notifyDataSetChanged();
            }
        });

        // Si está vacío, carga datos iniciales (solo una vez)
        if (registroViewModel.getListHabitaciones().getValue() == null || registroViewModel.getListHabitaciones().getValue().isEmpty()) {
            registroViewModel.setNuevaListaHabitaciones(cargarData());
        }

        //logica para pasar a otro fragmento
        btnContinuar3 = binding.btnContinuar3;
        btnContinuar3.setOnClickListener(v -> {
            if (listaHabitaciones != null) {
                // Guardar los datos en el ViewModelregistroViewModel.setNombre(nombre);
                // Navegar al siguiente fragmento


                //((RegistroHotelActivity) requireActivity()).irASiguientePaso(new Registro3Habitaciones_fragment());
                startActivity(new Intent(getActivity(), MainActivity.class));
            } else {
                Toast.makeText(getContext(), "Ingresa una foto", Toast.LENGTH_SHORT).show();
            }
        });

        //logica para añadir un cuarto
        btnAdd = binding.btnAdd;
        btnAdd.setOnClickListener(v -> {
            ((RegistroHotelActivity) requireActivity()).irASiguientePaso(new Registro4AddHabitacion_fragment());
        });


        return binding.getRoot();
    }

    private List<Habitaciones> cargarData() {
        List<Habitaciones> listaHa = new ArrayList<>();
        listaHa.add(new Habitaciones(
                "Habitacion superior - 1 cama grande",
                "- Precio para 2 adultos\n- 1 cama doble grande\n- Turneño 25 m2\n- WiFi de alto velocidad\n- Desayuno incluido",
                3,
                354.00,
                "Grande",
                25,
                "adminhotel_habitacion1.jpg"
        ));


        return listaHa;
    }


}