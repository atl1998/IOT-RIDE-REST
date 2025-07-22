package com.example.hotelreservaapp.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.taxista.model.MiniMapaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class DetallesViajeActivity extends AppCompatActivity {
    // Estados
    private static final String EST_SOLICITADO = "Solicitado";
    private static final String EST_ACEPTADO   = "Aceptado";
    private static final String EST_EN_CURSO   = "EnCurso";
    private static final String EST_CANCELADO  = "Cancelado";
    private static final String EST_FINALIZADO = "Finalizado";

    private ImageView ivFotoCliente;
    private TextView status, nombre, correo, telefono, fecha, hora, horaFin, ubicacion, destino;
    private Button btnCancelar, btnAceptar;
    private String idDocument;        // ← para actualizar en Firestore
    private String estadoActual;      // ← estado sobre el que trabajamos

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_detalles_viaje);

        // 1) Mini-mapa
        LatLng origen    = getIntent().getParcelableExtra("origen");
        LatLng destinoLL = getIntent().getParcelableExtra("destino");
        estadoActual     = getIntent().getStringExtra("estado");
        MiniMapaFragment miniMapa = MiniMapaFragment.newInstance(origen, destinoLL, estadoActual);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapMiniFragment, miniMapa)
                .commit();

        // 2) findViewById
        ivFotoCliente = findViewById(R.id.ivFotoCliente);
        status        = findViewById(R.id.status);
        nombre        = findViewById(R.id.tvNombreCompleto);
        correo        = findViewById(R.id.correoviajero);
        telefono      = findViewById(R.id.telefonoviajero);
        fecha         = findViewById(R.id.tvFecha);
        hora          = findViewById(R.id.tvHora);
        horaFin       = findViewById(R.id.valorHoraLlegada);
        ubicacion     = findViewById(R.id.tvUbicacion);
        destino       = findViewById(R.id.valorLugarDestino);
        btnCancelar   = findViewById(R.id.btnCancelarSolicitud);
        btnAceptar    = findViewById(R.id.btnAceptarSolicitud);

        // 3) Extras
        idDocument     = getIntent().getStringExtra("idDocument");
        String nombreExtra    = getIntent().getStringExtra("nombreCliente");
        String fotoExtra      = getIntent().getStringExtra("fotoCliente");
        String correoExtra    = getIntent().getStringExtra("correo");
        String telefonoExtra  = getIntent().getStringExtra("telefono");
        String fechaExtra     = getIntent().getStringExtra("fecha");
        String horaExtra      = getIntent().getStringExtra("hora");
        String horaFinExtra   = getIntent().getStringExtra("horaFin");
        String ubicExtra      = getIntent().getStringExtra("ubicacionText");
        String destExtra      = getIntent().getStringExtra("destinoText");

        // 4) Cargar foto
        if (fotoExtra != null && !fotoExtra.isEmpty()) {
            Glide.with(this)
                    .load(fotoExtra)
                    .placeholder(R.drawable.default_profile)
                    .into(ivFotoCliente);
        } else {
            ivFotoCliente.setImageResource(R.drawable.default_profile);
        }

        // 5) Rellenar demás campos
        nombre.setText(nombreExtra);
        correo.setText(correoExtra);
        telefono.setText(telefonoExtra);
        fecha.setText(fechaExtra);
        hora.setText(horaExtra);
        horaFin.setText(horaFinExtra != null ? horaFinExtra : "-");
        ubicacion.setText(ubicExtra);
        destino.setText(destExtra);
        status.setText(estadoActual);

        // 6) Botones según estado
        actualizarVistaSegunEstado();

        // 7) Listeners
        btnAceptar.setOnClickListener(v -> onAceptarClicked());
        btnCancelar.setOnClickListener(v -> onCancelarClicked());
    }

    private void actualizarVistaSegunEstado() {
        switch (estadoActual) {
            case EST_SOLICITADO:
                status.setTextColor(ContextCompat.getColor(this, R.color.azul));
                btnAceptar.setText("Aceptar Solicitud");
                btnAceptar.setVisibility(View.VISIBLE);
                btnCancelar.setVisibility(View.GONE);
                break;
            case EST_ACEPTADO:
                status.setTextColor(ContextCompat.getColor(this, R.color.verde_aceptar));
                btnAceptar.setText("Confirmar Recojo");
                btnAceptar.setVisibility(View.VISIBLE);
                btnCancelar.setText("Cancelar Solicitud");
                btnCancelar.setVisibility(View.VISIBLE);
                break;
            case EST_EN_CURSO:
                status.setTextColor(ContextCompat.getColor(this, R.color.verde_aceptar));
                btnAceptar.setText("Finalizar Viaje");
                btnAceptar.setVisibility(View.VISIBLE);
                btnCancelar.setText("Cancelar Viaje");
                btnCancelar.setVisibility(View.VISIBLE);
                break;
            default: // Cancelado o Finalizado
                status.setTextColor(ContextCompat.getColor(this,
                        EST_CANCELADO.equals(estadoActual) ?
                                R.color.error_red : R.color.negro));
                btnAceptar.setVisibility(View.GONE);
                btnCancelar.setVisibility(View.GONE);
        }
    }

    private void onAceptarClicked() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (EST_SOLICITADO.equals(estadoActual)) {
            // Verificar que NO haya otro Aceptado o EnCurso
            db.collection("servicios_taxi")
                    .whereIn("estado", Arrays.asList(EST_ACEPTADO, EST_EN_CURSO))
                    .get()
                    .addOnSuccessListener(snap -> {
                        if (!snap.isEmpty()) {
                            Toast.makeText(this,
                                    "Ya tienes un viaje aceptado o en curso",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // No hay conflicto, actualizar a Aceptado
                        db.collection("servicios_taxi")
                                .document(idDocument)
                                .update("estado", EST_ACEPTADO)
                                .addOnSuccessListener(u -> {
                                    estadoActual = EST_ACEPTADO;
                                    status.setText(EST_ACEPTADO);
                                    actualizarVistaSegunEstado();
                                    Toast.makeText(this,
                                            "Solicitud aceptada",
                                            Toast.LENGTH_SHORT).show();
                                });
                    });
        }
        else if (EST_ACEPTADO.equals(estadoActual)) {
            // Verificar que NO haya otro EnCurso distinto de este
            db.collection("servicios_taxi")
                    .whereEqualTo("estado", EST_EN_CURSO)
                    .get()
                    .addOnSuccessListener(snap -> {
                        boolean conflicto = false;
                        for (var doc : snap.getDocuments()) {
                            if (!doc.getId().equals(idDocument)) {
                                conflicto = true;
                                break;
                            }
                        }
                        if (conflicto) {
                            Toast.makeText(this,
                                    "Ya tienes un viaje en curso",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // No hay conflicto, actualizar a EnCurso
                        db.collection("servicios_taxi")
                                .document(idDocument)
                                .update("estado", EST_EN_CURSO)
                                .addOnSuccessListener(u -> {
                                    estadoActual = EST_EN_CURSO;
                                    status.setText(EST_EN_CURSO);
                                    actualizarVistaSegunEstado();
                                    // pasar al mapa en curso
                                    Intent i = new Intent(this, MapaActividad.class);
                                    i.putExtra("origen", (Bundle) getIntent().getParcelableExtra("origen"));
                                    i.putExtra("destino", (Bundle) getIntent().getParcelableExtra("destino"));
                                    i.putExtra("estado", EST_EN_CURSO);
                                    startActivity(i);
                                });
                    });
        }
        else if (EST_EN_CURSO.equals(estadoActual)) {
            // Finalizar via QR
            Intent qr = new Intent(this, QrLecturaActivity.class);
            qr.putExtra("idDocument", idDocument);
            startActivity(qr);
        }
    }

    private void onCancelarClicked() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("servicios_taxi")
                .document(idDocument)
                .update("estado", EST_CANCELADO)
                .addOnSuccessListener(u -> {
                    estadoActual = EST_CANCELADO;
                    status.setText(EST_CANCELADO);
                    actualizarVistaSegunEstado();
                    Toast.makeText(this,
                            "Viaje cancelado",
                            Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}
