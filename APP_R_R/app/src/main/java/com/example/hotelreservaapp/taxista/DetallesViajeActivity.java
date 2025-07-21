package com.example.hotelreservaapp.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.taxista.adapter.TarjetaTaxistaAdapter;
import com.example.hotelreservaapp.taxista.model.TarjetaModel;

public class DetallesViajeActivity extends AppCompatActivity {

    TextView status, nombre, correo, telefono, fecha, hora, horaFin, ubicacion, destino;
    Button btnCancelar, btnAceptar;
    View abrirMapa;
    TarjetaModel modeloActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_detalles_viaje);

        status = findViewById(R.id.status);
        nombre = findViewById(R.id.tvNombreCompleto);
        correo = findViewById(R.id.correoviajero);
        telefono = findViewById(R.id.telefonoviajero);
        fecha = findViewById(R.id.tvFecha);
        hora = findViewById(R.id.tvHora);
        horaFin = findViewById(R.id.valorHoraLlegada);
        ubicacion = findViewById(R.id.tvUbicacion);
        destino = findViewById(R.id.valorLugarDestino);
        btnCancelar = findViewById(R.id.btnCancelarSolicitud);
        btnAceptar = findViewById(R.id.btnAceptarSolicitud);
        abrirMapa = findViewById(R.id.abrirMapa);

        Intent intent = getIntent();
        String nombreExtra = intent.getStringExtra("nombre");

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

        nombre.setText(modeloActual.getNombreCliente());
        correo.setText(modeloActual.getCorreoCliente());
        telefono.setText(modeloActual.getTelefonoCliente());
        fecha.setText(modeloActual.getFecha());
        hora.setText(modeloActual.getHora());
        ubicacion.setText(modeloActual.getUbicacionOrigen());
        destino.setText(modeloActual.getDestino());
        status.setText(modeloActual.getEstado());
        horaFin.setText(modeloActual.getHoraFinalizacion() != null ? modeloActual.getHoraFinalizacion() : "-");

        actualizarVistaSegunEstado();

        abrirMapa.setOnClickListener(v -> {
            Intent mapaIntent = new Intent(this, MapaActividad.class);
            mapaIntent.putExtra("ubicacion", modeloActual.getUbicacionOrigen());
            startActivity(mapaIntent);
        });

        btnCancelar.setOnClickListener(v -> {
            modeloActual.setEstado("Cancelado");
            status.setText("Cancelado");
            status.setTextColor(getResources().getColor(R.color.error_red));
            btnCancelar.setVisibility(View.GONE);
            btnAceptar.setVisibility(View.GONE);
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
                Toast.makeText(this, "No puedes aceptar el viaje porque ya tienes uno en curso", Toast.LENGTH_SHORT).show();
            } else {
                modeloActual.setEstado("En progreso");
                status.setText("En progreso");
                status.setTextColor(getResources().getColor(R.color.verde_aceptar));
                btnAceptar.setVisibility(View.GONE);
                btnCancelar.setVisibility(View.VISIBLE);

                Intent intentMapa = new Intent(this, MapaActividad.class);
                intentMapa.putExtra("ubicacion", modeloActual.getUbicacionOrigen());
                startActivity(intentMapa);
            }
        });

        findViewById(R.id.añadirfavoritos).setOnClickListener(v -> finish());
    }

    private void actualizarVistaSegunEstado() {
        String estado = modeloActual.getEstado();

        switch (estado) {
            case "En progreso":
                status.setTextColor(getResources().getColor(R.color.verde_aceptar));
                btnCancelar.setVisibility(View.VISIBLE);
                btnAceptar.setVisibility(View.GONE);
                break;
            case "Solicitado":
                status.setTextColor(getResources().getColor(R.color.azul));
                btnAceptar.setVisibility(View.VISIBLE);
                btnCancelar.setVisibility(View.GONE);
                break;
            case "Cancelado":
                status.setTextColor(getResources().getColor(R.color.error_red));
                btnAceptar.setVisibility(View.GONE);
                btnCancelar.setVisibility(View.GONE);
                break;
            case "Finalizado":
                status.setTextColor(getResources().getColor(R.color.negro));
                btnAceptar.setVisibility(View.GONE);
                btnCancelar.setVisibility(View.GONE);
                break;
        }
    }
}