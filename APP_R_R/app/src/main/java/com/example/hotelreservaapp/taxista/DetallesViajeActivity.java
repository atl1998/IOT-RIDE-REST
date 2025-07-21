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
import com.example.hotelreservaapp.taxista.adapter.TarjetaTaxistaAdapter;
import com.example.hotelreservaapp.taxista.model.MiniMapaFragment;
import com.example.hotelreservaapp.taxista.model.TarjetaModel;
import com.google.android.gms.maps.model.LatLng;

public class DetallesViajeActivity extends AppCompatActivity {
    private ImageView ivFotoCliente;
    private TextView status, nombre, correo, telefono, fecha, hora, horaFin, ubicacion, destino;
    private Button btnCancelar, btnAceptar;
    private TarjetaModel modeloActual;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_detalles_viaje);

        // 1) Mini-mapa
        LatLng origen    = getIntent().getParcelableExtra("origen");
        LatLng destinoLL = getIntent().getParcelableExtra("destino");
        String estado    = getIntent().getStringExtra("estado");
        MiniMapaFragment miniMapa = MiniMapaFragment.newInstance(origen, destinoLL, estado);
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

        // 3) Recuperar el nombreCliente y fotoCliente
        String nombreExtra = getIntent().getStringExtra("nombreCliente");
        String fotoExtra   = getIntent().getStringExtra("fotoCliente");

        // 4) Buscar el modelo en listaCompartida
        for (TarjetaModel t : TarjetaTaxistaAdapter.listaCompartida) {
            if (t.getNombreCliente().equals(nombreExtra)) {
                modeloActual = t;
                break;
            }
        }
        if (modeloActual == null) {
            Toast.makeText(this, "No se pudo encontrar el viaje", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 5) Cargar la foto de perfil (si nos la pasan como extra, la usamos; si no, la del modelo)
        String fotoUrl = fotoExtra != null ? fotoExtra : modeloActual.getFotoCliente();
        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            Glide.with(this)
                    .load(fotoUrl)
                    .placeholder(R.drawable.default_profile)
                    .into(ivFotoCliente);
        } else {
            ivFotoCliente.setImageResource(R.drawable.default_profile);
        }

        // 6) Rellenar datos
        nombre.setText(modeloActual.getNombreCliente());
        correo.setText(modeloActual.getCorreoCliente());
        telefono.setText(modeloActual.getTelefonoCliente());
        fecha.setText(modeloActual.getFecha());
        hora.setText(modeloActual.getHora());
        ubicacion.setText(modeloActual.getUbicacionOrigen());
        destino.setText(modeloActual.getDestino());
        status.setText(modeloActual.getEstado());
        horaFin.setText(
                modeloActual.getHoraFinalizacion() != null
                        ? modeloActual.getHoraFinalizacion()
                        : "-"
        );

        // 7) Ajustar botones
        actualizarVistaSegunEstado();

        // 8) Listeners
        btnCancelar.setOnClickListener(v -> {
            modeloActual.setEstado("Cancelado");
            status.setText("Cancelado");
            status.setTextColor(ContextCompat.getColor(this, R.color.error_red));
            actualizarVistaSegunEstado();
        });

        btnAceptar.setOnClickListener(v -> {
            boolean yaEnCurso = false;
            for (TarjetaModel t : TarjetaTaxistaAdapter.listaCompartida) {
                if ("En progreso".equalsIgnoreCase(t.getEstado())) {
                    yaEnCurso = true;
                    break;
                }
            }
            if (yaEnCurso) {
                Toast.makeText(this,
                        "No puedes aceptar el viaje porque ya tienes uno en curso",
                        Toast.LENGTH_SHORT).show();
            } else {
                modeloActual.setEstado("En progreso");
                status.setText("En progreso");
                status.setTextColor(ContextCompat.getColor(this, R.color.verde_aceptar));
                actualizarVistaSegunEstado();
                Intent intentMapa = new Intent(this, MapaActividad.class);
                intentMapa.putExtra("origen", origen);
                intentMapa.putExtra("destino", destinoLL);
                startActivity(intentMapa);
            }
        });

        findViewById(R.id.añadirfavoritos).setOnClickListener(v -> finish());
    }

    private void actualizarVistaSegunEstado() {
        String est = modeloActual.getEstado();
        switch (est) {
            case "En progreso":
                status.setTextColor(ContextCompat.getColor(this, R.color.verde_aceptar));
                btnAceptar.setVisibility(View.GONE);
                btnCancelar.setVisibility(View.VISIBLE);
                break;
            case "Solicitado":
                status.setTextColor(ContextCompat.getColor(this, R.color.azul));
                btnAceptar.setVisibility(View.VISIBLE);
                btnCancelar.setVisibility(View.GONE);
                break;
            default: // Cancelado o Finalizado
                status.setTextColor(ContextCompat.getColor(this, R.color.error_red));
                btnAceptar.setVisibility(View.GONE);
                btnCancelar.setVisibility(View.GONE);
                break;
        }
    }
}
