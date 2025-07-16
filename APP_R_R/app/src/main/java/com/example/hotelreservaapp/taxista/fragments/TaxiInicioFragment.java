package com.example.hotelreservaapp.taxista.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.taxista.TaxistaMain;
import com.example.hotelreservaapp.taxista.adapter.TarjetaTaxistaAdapter;
import com.example.hotelreservaapp.taxista.model.TarjetaModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class TaxiInicioFragment extends Fragment implements TarjetaTaxistaAdapter.OnNotificacionListener {

    private RecyclerView recyclerView;
    private TarjetaTaxistaAdapter adapter;
    private List<TarjetaModel> datos;
    private View btnSolicitudes, btnHistorial;
    private TextView badgeNotificaciones;

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
        badgeNotificaciones = view.findViewById(R.id.badgeNotificaciones);

        datos = new ArrayList<>();

        btnSolicitudes.setOnClickListener(v -> filtrarSolicitudesActivas());
        btnHistorial.setOnClickListener(v -> filtrarHistorial());

        actualizarBadge();

        MaterialButton btnNotificaciones = view.findViewById(R.id.notificaciones_cliente);
        btnNotificaciones.setOnClickListener(v -> {
            if (getActivity() instanceof TaxistaMain) {
                TaxistaMain main = (TaxistaMain) getActivity();
                main.marcarNotificacionesComoLeidas();
                actualizarBadge();
                main.abrirFragmentoNotificaciones();
            }
        });
        //insertarSolicitudesDePruebaReales();
        cargarSolicitudesDesdeFirebase();
    }

    private void actualizarBadge() {
        if (getActivity() instanceof TaxistaMain) {
            int count = ((TaxistaMain) getActivity()).getContadorNotificacionesNoLeidas();
            badgeNotificaciones.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
            badgeNotificaciones.setText(String.valueOf(count));
        }
    }

    private void filtrarSolicitudesActivas() {
        List<TarjetaModel> activas = new ArrayList<>();
        for (TarjetaModel item : datos) {
            if ("Solicitado".equalsIgnoreCase(item.getEstado()) || "En progreso".equalsIgnoreCase(item.getEstado())) {
                activas.add(item);
            }
        }
        adapter = new TarjetaTaxistaAdapter(activas, getContext(), this, false);
        recyclerView.setAdapter(adapter);

        btnSolicitudes.setBackgroundColor(getResources().getColor(R.color.crema));
        btnHistorial.setBackgroundColor(getResources().getColor(R.color.transparente));
    }

    private void filtrarHistorial() {
        List<TarjetaModel> historial = new ArrayList<>();
        for (TarjetaModel item : datos) {
            if ("Cancelado".equalsIgnoreCase(item.getEstado()) || "Finalizado".equalsIgnoreCase(item.getEstado())) {
                historial.add(item);
            }
        }
        historial.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        adapter = new TarjetaTaxistaAdapter(historial, getContext(), this, true); // 👈 activa modo historial
        recyclerView.setAdapter(adapter);

        btnSolicitudes.setBackgroundColor(getResources().getColor(R.color.transparente));
        btnHistorial.setBackgroundColor(getResources().getColor(R.color.crema));
    }

    private void cargarSolicitudesDesdeFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("servicios_taxi")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    datos.clear();
                    for (var document : querySnapshot.getDocuments()) {
                        Log.d("FIREBASE", "DOC ID: " + document.getId() + " → " + document.getData());
                        TarjetaModel solicitud = document.toObject(TarjetaModel.class);
                        if (solicitud != null) {
                            solicitud.setIdCliente(document.getId());
                            datos.add(solicitud);
                        } else {
                            Log.w("FIREBASE", "Documento inválido: " + document.getId());
                        }
                    }
                    filtrarSolicitudesActivas();
                })
                .addOnFailureListener(e -> Log.e("FIREBASE", "Error al cargar datos", e));
    }

    @Override
    public void onViajeAceptado(TarjetaModel tarjeta) {
        if (getActivity() instanceof TaxistaMain) {
            TaxistaMain main = (TaxistaMain) getActivity();
            main.agregarNotificacionGlobal(new com.example.hotelreservaapp.Objetos.Notificaciones(
                    main.getListaGlobalNotificaciones().size() + 1,
                    "pedido",
                    "Solicitud aceptada",
                    "Solicitud aceptada",
                    "Has aceptado el pedido de " + tarjeta.getNombreCliente(),
                    "Recoger en: " + tarjeta.getUbicacionOrigen(),
                    System.currentTimeMillis()
            ));
            actualizarBadge();
            cargarSolicitudesDesdeFirebase();
        }
    }

    @Override
    public void onViajeCancelado(TarjetaModel tarjeta) {
        if (getActivity() instanceof TaxistaMain) {
            TaxistaMain main = (TaxistaMain) getActivity();
            main.agregarNotificacionGlobal(new com.example.hotelreservaapp.Objetos.Notificaciones(
                    main.getListaGlobalNotificaciones().size() + 1,
                    "pedido",
                    "Viaje cancelado",
                    "Viaje cancelado",
                    "Has cancelado el viaje con " + tarjeta.getNombreCliente(),
                    "Origen: " + tarjeta.getUbicacionOrigen(),
                    System.currentTimeMillis()
            ));
            actualizarBadge();
            cargarSolicitudesDesdeFirebase();
        }
    }

    public void onViajeConcluido(String nombreUsuario, String ubicacion) {
        if (getActivity() instanceof TaxistaMain) {
            TaxistaMain main = (TaxistaMain) getActivity();
            main.agregarNotificacionGlobal(new com.example.hotelreservaapp.Objetos.Notificaciones(
                    main.getListaGlobalNotificaciones().size() + 1,
                    "pedido",
                    "Viaje concluido",
                    "Viaje concluido",
                    "Has concluido el viaje con " + nombreUsuario,
                    "Lugar: " + ubicacion,
                    System.currentTimeMillis()
            ));
            actualizarBadge();
        }
    }


    private void insertarSolicitudesDePruebaReales() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Datos de prueba con ubicaciones reales de Lima
        String[] nombres = {"Elena Bustamante", "Mario Vargas", "Natalia Medina", "Lucía Fernández", "Roberto Carlos",
                "Camila Ríos", "Alonso Pérez", "Esteban Soto", "Jazmín Ortega", "Cesar Ramirez"};
        String[] correos = {"elena@mail.com", "mario@mail.com", "natalia@mail.com", "lucia@mail.com", "roberto@mail.com",
                "camila@mail.com", "alonso@mail.com", "esteban@mail.com", "jazmin@mail.com", "cesar@mail.com"};
        String[] telefonos = {"955111222", "999111222", "923456789", "912345678", "987654321",
                "934567890", "999111888", "976543210", "911222333", "993202932"};

        // Hoteles de origen (nombre, lat, lng)
        String[] hoteles = {"Hotel Marriot", "Hotel Casa Andina", "Hotel San Agustín", "Hotel Costa del Sol", "Hotel Inkaterra",
                "Hotel Libertador", "Hotel NM Lima", "Hotel Los Portales", "Hotel Belmond Miraflores", "Hotel Hilton Lima"};
        double[] latOrigen = {-12.1191, -12.1266, -12.0481, -12.0574, -12.1413,
                -12.1051, -12.1010, -12.0450, -12.1235, -12.0962};
        double[] lngOrigen = {-77.0300, -77.0282, -77.0330, -77.0443, -77.0322,
                -77.0373, -77.0365, -77.0302, -77.0308, -77.0280};

        // Destinos reales
        String[] destinos = {"Aeropuerto Jorge Chávez", "Estación Naranjal", "Terminal Yerbateros", "Aeropuerto Rodríguez Ballón",
                "Estación Central", "Terminal Plaza Norte", "Terminal Terrestre Atocongo", "Paradero 2 de Javier Prado", "Av. La Marina", "Aeropuerto Jorge Chávez"};
        double[] latDestino = {-12.0219, -11.9815, -12.0421, -16.3411, -12.0464,
                -11.9903, -12.1681, -12.0903, -12.0812, -12.0219};
        double[] lngDestino = {-77.1143, -77.0566, -76.9856, -71.9822, -77.0313,
                -77.0601, -76.9822, -77.0368, -77.0761, -77.1143};

        for (int i = 0; i < 10; i++) {
            TarjetaModel solicitud = new TarjetaModel();
            solicitud.setNombreCliente(nombres[i]);
            solicitud.setCorreoCliente(correos[i]);
            solicitud.setTelefonoCliente(telefonos[i]);
            solicitud.setFotoCliente("");
            solicitud.setUbicacionOrigen(hoteles[i]);
            solicitud.setDestino(destinos[i]);
            solicitud.setFecha("30 de junio");
            solicitud.setHora("10:" + (10 + i) + " AM");
            solicitud.setEstado("Solicitado");
            solicitud.setTimestamp(System.currentTimeMillis() + i * 100000);

            // Coordenadas reales
            solicitud.setLatOrigen(latOrigen[i]);
            solicitud.setLngOrigen(lngOrigen[i]);
            solicitud.setLatDestino(latDestino[i]);
            solicitud.setLngDestino(lngDestino[i]);

            db.collection("servicios_taxi")
                    .add(solicitud)
                    .addOnSuccessListener(doc -> {
                        Log.d("FIREBASE", "Solicitud real añadida: " + doc.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FIREBASE", "Error al insertar solicitud", e);
                    });
        }
    }

}
