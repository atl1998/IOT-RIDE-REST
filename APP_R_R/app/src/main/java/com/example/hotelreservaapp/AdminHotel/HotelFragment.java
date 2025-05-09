package com.example.hotelreservaapp.AdminHotel;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.AdminhotelFragmentHotelBinding;
import com.example.hotelreservaapp.databinding.AdminhotelFragmentInicioBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;


public class HotelFragment extends Fragment {

    private AdminhotelFragmentHotelBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adminhotel_fragment_hotel, container, false);

        //Para ir a reportes usuario y servicio
        MaterialCardView cardHabitaciones = view.findViewById(R.id.cardHabitaciones);
        MaterialCardView cardServicio = view.findViewById(R.id.cardServicio);

        cardHabitaciones.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            startActivity(new Intent(getActivity(), HotelHabitaciones.class));
        });

        cardServicio.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            startActivity(new Intent(getActivity(), HotelServicios.class));
        });

        //Para ir a notificaciones
        MaterialButton btnNotificaiones = view.findViewById(R.id.NotificacionesAdminHotel);
        btnNotificaiones.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            startActivity(new Intent(getActivity(), NotificacionesActivity.class));
        });


         return view;
    }
}