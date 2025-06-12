package com.example.hotelreservaapp.taxista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hotelreservaapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

public class TaxiMapaFragment extends Fragment implements OnMapReadyCallback {

    private MaterialButton btnBuscarenMapa;
    private GoogleMap mMap;

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

        // Buscar mapa desde el fragmento hijo
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnBuscarenMapa.setOnClickListener(v -> {
            // Aquí podrías lanzar un diálogo de búsqueda de lugares con Autocomplete
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Configurar mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Marcador de ejemplo
        LatLng lima = new LatLng(-12.0464, -77.0428);
        mMap.addMarker(new MarkerOptions().position(lima).title("Estás en Lima"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, 15));
    }
}
