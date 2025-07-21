package com.example.hotelreservaapp.AdminHotel.Fragments;

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

import com.example.hotelreservaapp.AdminHotel.Adapter.HabitacionAdapter;
import com.example.hotelreservaapp.AdminHotel.Model.Habitacion;
import com.example.hotelreservaapp.AdminHotel.Model.Hotel;
import com.example.hotelreservaapp.AdminHotel.RegistroHotelActivity;
import com.example.hotelreservaapp.AdminHotel.ViewModel.RegistroViewModel;
import com.example.hotelreservaapp.databinding.AdminhotelRegistro3FragmentBinding;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


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

        listaHabitaciones = new ArrayList<>(); // inicial vacia

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


}