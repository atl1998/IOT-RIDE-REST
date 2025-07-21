package com.example.hotelreservaapp.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.taxista.adapter.TarjetaTaxistaAdapter;
import com.example.hotelreservaapp.taxista.model.MiniMapaFragment;
import com.example.hotelreservaapp.taxista.model.TarjetaModel;
import com.google.android.gms.maps.model.LatLng;

public class DetallesViajeActivity extends AppCompatActivity {
    private TextView status, nombre, correo, telefono, fecha, hora, horaFin, ubicacion, destino;
    private Button btnCancelar, btnAceptar;
    private TarjetaModel modeloActual;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_detalles_viaje);

        // Recuperar LatLng y estado para el mini-mapa
        LatLng origen    = getIntent().getParcelableExtra("origen");
        LatLng destinoLL = getIntent().getParcelableExtra("destino");
        String estado    = getIntent().getStringExtra("estado");

        // Insertar MiniMapaFragment con sus argumentos
        MiniMapaFragment miniMapa = MiniMapaFragment.newInstance(origen, destinoLL, estado);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapMiniFragment, miniMapa)
                .commit();

        // Vincular vistas
        status      = findViewById(R.id.status);
        nombre      = findViewById(R.id.tvNombreCompleto);
        correo      = findViewById(R.id.correoviajero);
        telefono    = findViewById(R.id.telefonoviajero);
        fecha       = findViewById(R.id.tvFecha);
        hora        = findViewById(R.id.tvHora);
        horaFin     = findViewById(R.id.valorHoraLlegada);
        ubicacion   = findViewById(R.id.tvUbicacion);
        destino     = findViewById(R.id.valorLugarDestino);
        btnCancelar = findViewById(R.id.btnCancelarSolicitud);
        btnAceptar  = findViewById(R.id.btnAceptarSolicitud);

        // Encontrar el modeloActual
        String nombreExtra = getIntent().getStringExtra("nombre");
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

        // Rellenar datos
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

        // Ajustar visibilidad de botones y color de estado
        actualizarVistaSegunEstado();

        // Botón Cancelar
        btnCancelar.setOnClickListener(v -> {
            modeloActual.setEstado("Cancelado");
            status.setText("Cancelado");
            status.setTextColor(ContextCompat.getColor(this, R.color.error_red));
            actualizarVistaSegunEstado();
        });

        // Botón Aceptar
        btnAceptar.setOnClickListener(v -> {
            boolean yaEnCurso = false;
            for (TarjetaModel t : TarjetaTaxistaAdapter.listaCompartida) {
                if ("En progreso".equalsIgnoreCase(t.getEstado())) {
                    yaEnCurso = true;
                    break;
                }
            }
            if (yaEnCurso) {
                Toast.makeText(
                        this,
                        "No puedes aceptar el viaje porque ya tienes uno en curso",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                modeloActual.setEstado("En progreso");
                status.setText("En progreso");
                status.setTextColor(ContextCompat.getColor(this, R.color.verde_aceptar));
                actualizarVistaSegunEstado();

                Intent intentMapa = new Intent(this, MapaActividad.class);
                intentMapa.putExtra("origen",  origen);
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
            case "Cancelado":
                status.setTextColor(ContextCompat.getColor(this, R.color.error_red));
                btnAceptar.setVisibility(View.GONE);
                btnCancelar.setVisibility(View.GONE);
                break;
            case "Finalizado":
                status.setTextColor(ContextCompat.getColor(this, R.color.negro));
                btnAceptar.setVisibility(View.GONE);
                btnCancelar.setVisibility(View.GONE);
                break;
        }
    }
}
