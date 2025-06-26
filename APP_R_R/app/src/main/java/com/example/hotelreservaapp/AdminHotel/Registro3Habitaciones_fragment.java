package com.example.hotelreservaapp.AdminHotel;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hotelreservaapp.AdminHotel.Adapter.HabitacionAdapter;
import com.example.hotelreservaapp.AdminHotel.Model.Habitacion;
import com.example.hotelreservaapp.AdminHotel.Model.Hotel;
import com.example.hotelreservaapp.AdminHotel.ViewModel.RegistroViewModel;
import com.example.hotelreservaapp.LogManager;
import com.example.hotelreservaapp.databinding.AdminhotelRegistro3FragmentBinding;
import com.example.hotelreservaapp.model.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Registro3Habitaciones_fragment extends Fragment {

    private AdminhotelRegistro3FragmentBinding binding;

    private RegistroViewModel registroViewModel;
    private RecyclerView rvHabitaciones;
    private HabitacionAdapter adapter;
    private List<Habitacion> listaHabitaciones;

    private MaterialButton btnContinuar3;
    private ImageView btnAdd;
    private FirebaseUser usuarioActual;
    private FirebaseFirestore db;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = AdminhotelRegistro3FragmentBinding.inflate(inflater, container, false);
        registroViewModel = new ViewModelProvider(requireActivity()).get(RegistroViewModel.class);
        db = FirebaseFirestore.getInstance();
        usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        listaHabitaciones = new ArrayList<>(); // inicial vac

        // Configurar el RecyclerView
        rvHabitaciones = binding.listaHabitaciones;
        rvHabitaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHabitaciones.setHasFixedSize(true); // Usa un LinearLayoutManager
        adapter = new HabitacionAdapter(listaHabitaciones,getContext(),  new HabitacionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Habitacion habitacion = listaHabitaciones.get(position);
                /*mostrarPopupSeleccion(habitacion);*/
            }

            @Override
            public void onSeleccionCambio() {
                /*verificarSeleccion();*/
            }
        });
        rvHabitaciones.setAdapter(adapter);

        // Observa LiveData para actualizar lista y adaptador
        registroViewModel.getHotel().observe(getViewLifecycleOwner(), hotel -> {
            if (hotel != null && hotel.getHabitaciones() != null) {
                listaHabitaciones.clear();
                listaHabitaciones.addAll(hotel.getHabitaciones());
                adapter.notifyDataSetChanged();
            }
        });


        // Si está vacío, carga datos iniciales (solo una vez)
        if (registroViewModel.getHotel().getValue().getHabitaciones() == null || registroViewModel.getHotel().getValue().getHabitaciones().isEmpty()) {
            Hotel hotel =  registroViewModel.getHotel().getValue();
            hotel.setHabitaciones(cargarData());
            registroViewModel.setHotel(hotel);
        }

        //logica para pasar a otro fragmento
        btnContinuar3 = binding.btnContinuar3;
        btnContinuar3.setOnClickListener(v -> {
            if (listaHabitaciones != null) {
                // Navegar al siguiente fragmento
                ((RegistroHotelActivity) requireActivity()).irASiguientePaso(new Registro5Servicios_fragment());
            } else {
                Toast.makeText(getContext(), "Ingresa una habitación", Toast.LENGTH_SHORT).show();
            }
        });

        //logica para añadir un cuarto
        btnAdd = binding.btnAdd;
        btnAdd.setOnClickListener(v -> {
            ((RegistroHotelActivity) requireActivity()).irASiguientePaso(new Registro4AddHabitacion_fragment());
        });


        return binding.getRoot();
    }

    private List<Habitacion> cargarData() {
        List<Habitacion> listaHa = new ArrayList<>();
        listaHa.add(new Habitacion(
                "Standar",
                354.00,
                16.5,
                "Para dos personas",
                "adminhotel_habitacion1.jpg"
        ));
        return listaHa;
    }

}