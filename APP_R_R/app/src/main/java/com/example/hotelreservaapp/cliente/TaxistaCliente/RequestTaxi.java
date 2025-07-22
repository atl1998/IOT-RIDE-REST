package com.example.hotelreservaapp.cliente.TaxistaCliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import com.airbnb.lottie.LottieAnimationView;
import com.example.hotelreservaapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class RequestTaxi extends AppCompatActivity {

    private LottieAnimationView animView;
    private TextView          tvLooking;
    private Button            btnCancel;

    private FirebaseFirestore db;
    private ListenerRegistration serviceListener;
    private String serviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_request_taxi);

        animView  = findViewById(R.id.animation);
        tvLooking = findViewById(R.id.textViewLookingFor);
        btnCancel = findViewById(R.id.btnCancelRequest);

        db        = FirebaseFirestore.getInstance();
        serviceId = getIntent().getStringExtra("serviceId");

        // Si cancelas, simplemente regresas atrás
        btnCancel.setOnClickListener(v -> finish());

        // Escuchamos el cambio de estado de la solicitud
        DocumentReference ref = db.collection("servicios_taxi")
                .document(serviceId);
        serviceListener = ref.addSnapshotListener((snap, e) -> {
            if (e!=null || snap==null || !snap.exists()) return;
            String estado = snap.getString("estado");
            if ("En progreso".equalsIgnoreCase(estado)) {
                // taxista aceptó → lanzamos mapa
                Intent i = new Intent(RequestTaxi.this, MapaActividadCliente.class);
                i.putExtra("nombreCliente",   getIntent().getStringExtra("nombreCliente"));
                i.putExtra("telefonoCliente", getIntent().getStringExtra("telefonoCliente"));
                i.putExtra("fotoCliente",     getIntent().getStringExtra("fotoCliente"));
                i.putExtra("latOrigen",       getIntent().getDoubleExtra("latOrigen",0));
                i.putExtra("lngOrigen",       getIntent().getDoubleExtra("lngOrigen",0));
                i.putExtra("latDestino",      getIntent().getDoubleExtra("latDestino",0));
                i.putExtra("lngDestino",      getIntent().getDoubleExtra("lngDestino",0));
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
