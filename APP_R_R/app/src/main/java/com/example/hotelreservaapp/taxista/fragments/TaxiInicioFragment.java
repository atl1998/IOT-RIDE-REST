package com.example.hotelreservaapp.taxista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.hotelreservaapp.R;
import com.google.android.material.card.MaterialCardView;

public class TaxiInicioFragment extends Fragment {

    private MaterialCardView cardSolicitudes, cardViajes, cardHistorial;

    public TaxiInicioFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.taxista_fragment_inicio, container, false);

        // Referencias a las tarjetas
        cardSolicitudes = view.findViewById(R.id.cardSolicitudes);
        cardViajes = view.findViewById(R.id.cardViajes);
        cardHistorial = view.findViewById(R.id.cardHistorial);

        // Click en Solicitudes -> fragment de mapa
        cardSolicitudes.setOnClickListener(v -> abrirFragment(new TaxiMapaFragment()));

        // Click en Viajes (podrías cambiar esto a un fragment de viajes activos si tienes)
        cardViajes.setOnClickListener(v -> abrirFragment(new TaxiMapaFragment()));

        // Click en Historial
        cardHistorial.setOnClickListener(v -> abrirFragment(new TaxiHistorialFragment()));

        return view;
    }

    private void abrirFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity()
                .getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fragmentContainerTaxista, fragment); // Este ID debe coincidir con el del contenedor
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
