package com.example.hotelreservaapp.AdminHotel.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotelreservaapp.AdminHotel.NotificacionesActivity;
import com.example.hotelreservaapp.AdminHotel.ReporteServicioActivity;
import com.example.hotelreservaapp.AdminHotel.ReporteUsuarioActivity;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.AdminhotelFragmentReportesBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;


public class ReportesFragment extends Fragment {


    private AdminhotelFragmentReportesBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adminhotel_fragment_reportes, container, false);

        //Para ir a reportes usuario y servicio
        MaterialCardView cardUsuario = view.findViewById(R.id.cardUsuario);
        MaterialCardView cardServicio = view.findViewById(R.id.cardServicio);

        cardUsuario.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            startActivity(new Intent(getActivity(), ReporteUsuarioActivity.class));
        });

        cardServicio.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            startActivity(new Intent(getActivity(), ReporteServicioActivity.class));
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