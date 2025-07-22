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
import com.example.hotelreservaapp.Objetos.Notificaciones;
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.taxista_fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView        = view.findViewById(R.id.recyclerTaxista);
        btnSolicitudes      = view.findViewById(R.id.btnSolicitudes);
        btnHistorial        = view.findViewById(R.id.btnHistorial);
        badgeNotificaciones = view.findViewById(R.id.badgeNotificaciones);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        datos = new ArrayList<>();

        btnSolicitudes.setOnClickListener(v -> filtrarSolicitudesActivas());
        btnHistorial.setOnClickListener(v -> filtrarHistorial());

        // Botón de notificaciones en el header
        MaterialButton btnNotificaciones = requireActivity().findViewById(R.id.notificacionesTaxista);
        btnNotificaciones.setOnClickListener(v -> {
            if (getActivity() instanceof TaxistaMain) {
                TaxistaMain main = (TaxistaMain) getActivity();
                main.marcarNotificacionesComoLeidas();
                actualizarBadge();
                main.abrirFragmentoNotificaciones();
            }
        });

        actualizarBadge();
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
            String st = item.getEstado();
            if ("Solicitado".equalsIgnoreCase(st)
                    || "Aceptado".equalsIgnoreCase(st)
                    || "EnCurso".equalsIgnoreCase(st)) {
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
            String st = item.getEstado();
            if ("Cancelado".equalsIgnoreCase(st) || "Finalizado".equalsIgnoreCase(st)) {
                historial.add(item);
            }
        }
        // Orden descendente por timestamp: más recientes primero
        historial.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        adapter = new TarjetaTaxistaAdapter(historial, getContext(), this, true);
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
                    for (var doc : querySnapshot.getDocuments()) {
                        Log.d("FIREBASE", "DOC ID: " + doc.getId() + " → " + doc.getData());
                        TarjetaModel sol = doc.toObject(TarjetaModel.class);
                        if (sol != null) {
                            sol.setIdDocument(doc.getId());
                            datos.add(sol);
                        } else {
                            Log.w("FIREBASE", "Documento inválido: " + doc.getId());
                        }
                    }
                    filtrarSolicitudesActivas();  // Muestra activas por defecto
                })
                .addOnFailureListener(e -> Log.e("FIREBASE", "Error al cargar datos", e));
    }

    @Override
    public void onViajeAceptado(TarjetaModel tarjeta) {
        if (getActivity() instanceof TaxistaMain) {
            TaxistaMain main = (TaxistaMain) getActivity();
            main.agregarNotificacionGlobal(new Notificaciones(
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
            main.agregarNotificacionGlobal(new Notificaciones(
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

    /** Si en algún punto deseas notificar “viaje concluido” desde aquí: **/
    public void onViajeConcluido(String nombreUsuario, String ubicacion) {
        if (getActivity() instanceof TaxistaMain) {
            TaxistaMain main = (TaxistaMain) getActivity();
            main.agregarNotificacionGlobal(new Notificaciones(
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
}
