package com.example.hotelreservaapp.cliente.TaxistaCliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.hotelreservaapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class RequestTaxi extends AppCompatActivity {

    private LottieAnimationView animView;
    private TextView           tvLooking;
    private Button             btnCancel;

    private FirebaseFirestore  db;
    private ListenerRegistration serviceListener;
    private String             serviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_request_taxi);

        animView   = findViewById(R.id.animation);
        tvLooking  = findViewById(R.id.textViewLookingFor);
        btnCancel  = findViewById(R.id.btnCancelRequest);

        db         = FirebaseFirestore.getInstance();
        serviceId  = getIntent().getStringExtra("serviceId");

        // Botón cancelar vuelve atrás
        btnCancel.setOnClickListener(v -> finish());

        // Escuchamos cambios en la solicitud
        DocumentReference ref = db.collection("servicios_taxi")
                .document(serviceId);
        serviceListener = ref.addSnapshotListener((snap, e) -> {
            if (e != null || snap == null || !snap.exists()) return;

            String estado = snap.getString("estado");
            if ("Aceptado".equalsIgnoreCase(estado)) {
                // El taxista aceptó: leemos también sus datos
                String nombreTaxista   = snap.getString("taxistaNombre");
                String telefonoTaxista = snap.getString("taxistaTelefono");
                String fotoTaxista     = snap.getString("taxistaFotoUrl");

                // Coordenadas de origen/destino (se las pasaste al crear la solicitud)
                double latOrigen  = getIntent().getDoubleExtra("latOrigen",  0.0);
                double lngOrigen  = getIntent().getDoubleExtra("lngOrigen",  0.0);
                double latDestino = getIntent().getDoubleExtra("latDestino", 0.0);
                double lngDestino = getIntent().getDoubleExtra("lngDestino", 0.0);

                // Lanzamos la vista de mapa del cliente
                Intent i = new Intent(RequestTaxi.this, MapaActividadCliente.class);
                i.putExtra("serviceId",       serviceId);
                i.putExtra("nombreTaxista",   nombreTaxista);
                i.putExtra("telefonoTaxista", telefonoTaxista);
                i.putExtra("fotoTaxista",     fotoTaxista);
                i.putExtra("latOrigen",       latOrigen);
                i.putExtra("lngOrigen",       lngOrigen);
                i.putExtra("latDestino",      latDestino);
                i.putExtra("lngDestino",      lngDestino);

                startActivity(i);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceListener != null) {
            serviceListener.remove();
        }
    }
}
