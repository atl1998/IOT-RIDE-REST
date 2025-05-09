package com.example.hotelreservaapp.taxista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;

public class TaxiMapaFragment extends Fragment {

    private MaterialButton btnBuscarenMapa;

    public TaxiMapaFragment() {
        // Constructor vacío obligatorio
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.taxista_fragment_mapa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBuscarenMapa = view.findViewById(R.id.btnBuscarenMapa);

        btnBuscarenMapa.setOnClickListener(v -> {
            // Acción al presionar el botón de búsqueda
            // Aquí puedes mostrar un Toast, lanzar un diálogo o abrir otra vista
        });
    }
}
