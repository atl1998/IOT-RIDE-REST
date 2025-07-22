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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.EventListener;
import java.util.ArrayList;
import java.util.List;

public class TaxiInicioFragment extends Fragment implements TarjetaTaxistaAdapter.OnNotificacionListener {

    private RecyclerView recyclerView;
    private TarjetaTaxistaAdapter adapter;
    private List<TarjetaModel> datos;
    private View btnSolicitudes, btnHistorial;
    private TextView badgeNotificaciones;
    private boolean mostrandoHistorial = false;
    private ListenerRegistration registroSolicitudes;

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

        btnSolicitudes.setOnClickListener(v -> {
            mostrandoHistorial = false;
            filtrarSolicitudesActivas();
        });
        btnHistorial.setOnClickListener(v -> {
            mostrandoHistorial = true;
            filtrarHistorial();
        });

        MaterialButton btnNotifs = requireActivity().findViewById(R.id.notificacionesTaxista);
        btnNotifs.setOnClickListener(v -> {
            if (getActivity() instanceof TaxistaMain) {
                TaxistaMain main = (TaxistaMain) getActivity();
                main.marcarNotificacionesComoLeidas();
                actualizarBadge();
                main.abrirFragmentoNotificaciones();
            }
        });

        actualizarBadge();
        escucharSolicitudesFirebase();
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
        historial.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        adapter = new TarjetaTaxistaAdapter(historial, getContext(), this, true);
        recyclerView.setAdapter(adapter);

        btnSolicitudes.setBackgroundColor(getResources().getColor(R.color.transparente));
        btnHistorial.setBackgroundColor(getResources().getColor(R.color.crema));
    }

    private void escucharSolicitudesFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        registroSolicitudes = db.collection("servicios_taxi")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snaps,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("FIRESTORE_LISTEN", "Error en la escucha", e);
                            return;
                        }
                        datos.clear();
                        for (DocumentSnapshot doc : snaps.getDocuments()) {
                            TarjetaModel sol = doc.toObject(TarjetaModel.class);
                            if (sol != null) {
                                sol.setIdDocument(doc.getId());
                                datos.add(sol);
                            }
                        }
                        // Re-aplica el filtro actual
                        if (mostrandoHistorial) filtrarHistorial();
                        else filtrarSolicitudesActivas();
                    }
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (registroSolicitudes != null) registroSolicitudes.remove();
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
            // la escucha ya refresca automáticamente
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
        }
    }

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
