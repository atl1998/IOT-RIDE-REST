package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.adapter.Adminhotel_UsuarioAdapter;
import com.example.hotelreservaapp.databinding.AdminhotelRegistro2FragmentBinding;
import com.example.hotelreservaapp.databinding.AdminhotelRegsitro3FragmentBinding;
import com.example.hotelreservaapp.model.UsuarioListaSuperAdmin;

import java.util.ArrayList;
import java.util.List;


public class Registro3Habitaciones_fragment extends Fragment {

    private AdminhotelRegsitro3FragmentBinding binding;

    private RecyclerView rvHabitaciones;
    private com.example.hotelreservaapp.AdminHotel.HabitacionAdapter adapter;
    private List<Habitaciones> listaHabitaciones;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = AdminhotelRegsitro3FragmentBinding.inflate(inflater, container, false);

        cargarData();

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


        return binding.getRoot();
    }

    private void cargarData() {
        listaHabitaciones = new ArrayList<>();
        listaHabitaciones.add(new Habitaciones(
                "Habitacion superior - 1 cama grande",
                "- Precio para 2 adultos\n- 1 cama doble grande\n- Turneño 25 m2\n- WiFi de alto velocidad\n- Desayuno incluido",
                3,
                354.00,
                "Grande",
                25,
                "adminhotel_habitacion1.jpg"
        ));

        listaHabitaciones.add(new Habitaciones(
                "Habitacion deluxe cama extragrande",
                "- Precio para 2 adultos\n- 1 cama doble extra grande\n- Turneño 30 m2\n- WiFi de alto velocidad\n- Desayuno incluido",
                2,
                417.00,
                "Extra grande",
                30,
                "adminhotel_habitacion3.jpg"
        ));
        listaHabitaciones.add(new Habitaciones(
                "Habitacion deluxe cama extragrande",
                "- Precio para 2 adultos\n- 1 cama doble extra grande\n- Turneño 30 m2\n- WiFi de alto velocidad\n- Desayuno incluido",
                2,
                417.00,
                "Extra grande",
                30,
                "adminhotel_habitacion3.jpg"
        ));
        listaHabitaciones.add(new Habitaciones(
                "Habitacion deluxe cama extragrande",
                "- Precio para 2 adultos\n- 1 cama doble extra grande\n- Turneño 30 m2\n- WiFi de alto velocidad\n- Desayuno incluido",
                2,
                417.00,
                "Extra grande",
                30,
                "adminhotel_habitacion3.jpg"
        ));
        listaHabitaciones.add(new Habitaciones(
                "Habitacion deluxe cama extragrande",
                "- Precio para 2 adultos\n- 1 cama doble extra grande\n- Turneño 30 m2\n- WiFi de alto velocidad\n- Desayuno incluido",
                2,
                417.00,
                "Extra grande",
                30,
                "adminhotel_habitacion3.jpg"
        ));
    }
}