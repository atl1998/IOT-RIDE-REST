package com.example.hotelreservaapp.taxista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.Objetos.Notificaciones;
import com.example.hotelreservaapp.Objetos.NotificacionesStorageHelper;
import com.example.hotelreservaapp.taxista.adapter.NotificacionesAdapter;
import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;

public class TaxistaNotificacionesFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificacionesAdapter adapter;
    private ArrayList<Notificaciones> listaNotificaciones;
    private NotificacionesStorageHelper storageHelper;

    public TaxistaNotificacionesFragment() {
        // Constructor vacío obligatorio
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.taxista_notificaciones, container, false);

        recyclerView = root.findViewById(R.id.recyclerNotificaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        storageHelper = new NotificacionesStorageHelper(requireContext());
        cargarNotificacionesDesdeArchivo();

        adapter = new NotificacionesAdapter(getContext(), listaNotificaciones);
        recyclerView.setAdapter(adapter);

        // Manejo del botón back
        MaterialButton backButton = root.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        return root;
    }

    private void cargarNotificacionesDesdeArchivo() {
        Notificaciones[] arrayNotificaciones = storageHelper.leerArchivoNotificacionesDesdeSubcarpeta();
        if (arrayNotificaciones != null) {
            listaNotificaciones = new ArrayList<>(Arrays.asList(arrayNotificaciones));
        } else {
            listaNotificaciones = new ArrayList<>();
        }
    }

    private void guardarNotificacionesEnArchivo() {
        Notificaciones[] array = listaNotificaciones.toArray(new Notificaciones[0]);
        storageHelper.guardarArchivoNotificacionesEnSubcarpeta(array);
    }

    /**
     * Metodo público para agregar notificaciones desde otras clases
     */
    public void agregarNotificacionEvento(String tipo, String titulo, String tituloAmigable,
                                          String mensaje, String mensajeExtra, long fecha) {
        int nuevoId = listaNotificaciones.size() > 0
                ? listaNotificaciones.get(listaNotificaciones.size() - 1).getId() + 1
                : 1;

        Notificaciones nueva = new Notificaciones(nuevoId, tipo, titulo, tituloAmigable, mensaje, mensajeExtra, fecha);
        listaNotificaciones.add(0, nueva);  // Agregamos al inicio para mayor visibilidad
        guardarNotificacionesEnArchivo();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();  // Refrescar lista si hubo cambios
    }
}
