package com.example.hotelreservaapp.cliente.TaxistaCliente;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.hotelreservaapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class RequestTaxi extends AppCompatActivity {

    private LottieAnimationView animView;
    private TextView          tvLooking;
    private Button            btnCancel;
    private FrameLayout       bottomSheet;

    // Firebase
    private FirebaseAuth      auth;
    private FirebaseUser      user;
    private FirebaseFirestore db;
    private ListenerRegistration reservaListener;

    // Parámetros pasados al startActivity
    private String idHotel, idReserva;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_request_taxi);

        // Bind vistas
        animView     = findViewById(R.id.animation);
        tvLooking    = findViewById(R.id.textViewLookingFor);
        btnCancel    = findViewById(R.id.btnCancelRequest);
        bottomSheet  = findViewById(R.id.bottom_sheet);

        // Lottie
        animView.playAnimation();

        // Firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db   = FirebaseFirestore.getInstance();

        // Extras
        idHotel   = getIntent().getStringExtra("idHotel");
        idReserva = getIntent().getStringExtra("idReserva");

        // botón cancelar → volver atrás
        btnCancel.setOnClickListener(v -> {
            // opcional: borrar flag en Firestore si quieres
            finish();
        });

        if (user == null || idReserva == null) {
            Toast.makeText(this, "Faltan datos para continuar", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Escuchamos el estado de la reserva
        DocumentReference ref = db
                .collection("usuarios")
                .document(user.getUid())
                .collection("Reservas")
                .document(idReserva);

        reservaListener = ref.addSnapshotListener((snap, e) -> {
            if (e != null || snap == null || !snap.exists()) return;

            String estado = snap.getString("solicitarTaxista");
            if ("En progreso".equalsIgnoreCase(estado)) {
                // ocultamos
                bottomSheet.setVisibility(FrameLayout.GONE);
                // lanzamos siguiente pantalla (por ejemplo tu MapaActividad)
                Intent i = new Intent(RequestTaxi.this, MapaActividadCliente.class);
                // le pasamos los mismos extras que guardaste en el servicio
                i.putExtra("nombreCliente",    snap.getString("nombreCliente"));
                i.putExtra("telefonoCliente",  snap.getString("telefonoCliente"));
                i.putExtra("fotoCliente",      snap.getString("fotoCliente"));
                i.putExtra("latOrigen",        snap.getDouble("latOrigen"));
                i.putExtra("lngOrigen",        snap.getDouble("lngOrigen"));
                i.putExtra("latDestino",       snap.getDouble("latDestino"));
                i.putExtra("lngDestino",       snap.getDouble("lngDestino"));
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // limpiamos listener
        if (reservaListener != null) {
            reservaListener.remove();
        }
    }
}
