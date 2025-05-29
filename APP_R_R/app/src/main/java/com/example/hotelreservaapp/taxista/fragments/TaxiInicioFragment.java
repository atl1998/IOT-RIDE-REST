package com.example.hotelreservaapp.taxista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.taxista.adapter.TarjetaTaxistaAdapter;
import com.example.hotelreservaapp.taxista.model.TarjetaModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class TaxiInicioFragment extends Fragment implements TarjetaTaxistaAdapter.OnNotificacionListener {

    private RecyclerView recyclerView;
    private TarjetaTaxistaAdapter adapter;
    private List<TarjetaModel> datos;
    private View btnSolicitudes, btnHistorial;

    public TaxiInicioFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.taxista_fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerTaxista);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnSolicitudes = view.findViewById(R.id.btnSolicitudes);
        btnHistorial = view.findViewById(R.id.btnHistorial);

        // Inicializar datos de ejemplo
        datos = new ArrayList<>();
        datos.add(new TarjetaModel("Roberto Carlos", "rcarlos@mail.com", "987654321", "28 de abril", "6:30 PM", "Hotel Inkaterra", "Aeropuerto Jorge Chávez", "En progreso"));
        datos.add(new TarjetaModel("Lucía Fernández", "luciaf@mail.com", "912345678", "29 de abril", "7:00 PM", "Hotel Costa del Sol", "Terminal Plaza Norte", "Solicitado"));
        datos.add(new TarjetaModel("Mario Vargas", "mvargas@mail.com", "999111222", "30 de abril", "8:15 PM", "Hotel Casa Andina", "Terminal Yerbateros", "Solicitado"));
        datos.add(new TarjetaModel("Camila Ríos", "camirios@mail.com", "934567890", "1 de mayo", "5:45 PM", "Hotel Libertador", "Aeropuerto Rodríguez Ballón", "Solicitado"));
        datos.add(new TarjetaModel("Esteban Soto", "esoto@mail.com", "976543210", "2 de mayo", "6:00 PM", "Hotel Los Portales", "Terminal Terrestre Cusco", "Cancelado"));
        datos.add(new TarjetaModel("Natalia Medina", "nmedina@mail.com", "923456789", "3 de mayo", "7:30 PM", "Hotel San Agustín", "Aeropuerto Alejandro Velasco Astete", "Finalizado"));

        filtrarSolicitudesActivas();

        btnSolicitudes.setOnClickListener(v -> filtrarSolicitudesActivas());
        btnHistorial.setOnClickListener(v -> filtrarHistorial());

        // Configuración del botón de notificaciones
        MaterialButton btnNotificaciones = view.findViewById(R.id.notificaciones_cliente);
        btnNotificaciones.setOnClickListener(v -> abrirFragmentoNotificaciones());
    }

    private void filtrarSolicitudesActivas() {
        List<TarjetaModel> activas = new ArrayList<>();
        for (TarjetaModel item : datos) {
            if (item.getEstado().equalsIgnoreCase("Solicitado") || item.getEstado().equalsIgnoreCase("En progreso")) {
                activas.add(item);
            }
        }
        adapter = new TarjetaTaxistaAdapter(activas, getContext(), this);
        recyclerView.setAdapter(adapter);

        btnSolicitudes.setBackgroundColor(getResources().getColor(R.color.crema));
        btnHistorial.setBackgroundColor(getResources().getColor(R.color.transparente));
    }

    private void filtrarHistorial() {
        List<TarjetaModel> historial = new ArrayList<>();
        for (TarjetaModel item : datos) {
            if (item.getEstado().equalsIgnoreCase("Cancelado") || item.getEstado().equalsIgnoreCase("Finalizado")) {
                historial.add(item);
            }
        }
        adapter = new TarjetaTaxistaAdapter(historial, getContext(), this);
        recyclerView.setAdapter(adapter);

        btnSolicitudes.setBackgroundColor(getResources().getColor(R.color.transparente));
        btnHistorial.setBackgroundColor(getResources().getColor(R.color.crema));
    }

    @Override
    public void onViajeAceptado(TarjetaModel tarjeta) {
        // Aquí debes obtener la referencia al fragmento de notificaciones y agregar notificación
        TaxistaNotificacionesFragment fragNoti = (TaxistaNotificacionesFragment) getParentFragmentManager()
                .findFragmentByTag("FRAG_NOTIFICACIONES");

        if (fragNoti != null) {
            fragNoti.agregarNotificacionEvento(
                    "pedido",
                    "Solicitud aceptada",
                    "Solicitud aceptada",
                    "Has aceptado el pedido de " + tarjeta.getNombreUsuario(),
                    "Recoger en: " + tarjeta.getUbicacion(),
                    System.currentTimeMillis()
            );
        }
    }

    @Override
    public void onViajeCancelado(TarjetaModel tarjeta) {
        TaxistaNotificacionesFragment fragNoti = (TaxistaNotificacionesFragment) getParentFragmentManager()
                .findFragmentByTag("FRAG_NOTIFICACIONES");

        if (fragNoti != null) {
            fragNoti.agregarNotificacionEvento(
                    "pedido",
                    "Viaje cancelado",
                    "Viaje cancelado",
                    "Has cancelado el viaje con " + tarjeta.getNombreUsuario(),
                    "Origen: " + tarjeta.getUbicacion(),
                    System.currentTimeMillis()
            );
        }
    }

    // Metodo para abrir el fragmento de notificaciones
    private void abrirFragmentoNotificaciones() {
        TaxistaNotificacionesFragment fragmentNotificaciones = new TaxistaNotificacionesFragment();

        // Se reemplaza el fragmento actual (TaxiInicioFragment) por el fragmento notificaciones
        // y se añade a backstack para poder regresar
        getParentFragmentManager()
                .beginTransaction()
                .replace(((ViewGroup)getView().getParent()).getId(), fragmentNotificaciones, "FRAG_NOTIFICACIONES")
                .addToBackStack(null)
                .commit();
    }
}
