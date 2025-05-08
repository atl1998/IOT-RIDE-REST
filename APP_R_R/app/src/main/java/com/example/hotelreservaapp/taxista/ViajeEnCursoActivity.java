package com.example.hotelreservaapp.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.taxista.adapter.TarjetaTaxistaAdapter;
import com.example.hotelreservaapp.taxista.model.TarjetaModel;
import com.google.android.material.button.MaterialButton;

public class ViajeEnCursoActivity extends AppCompatActivity {

    TextView nombreCliente, telefonoviajero;
    MaterialButton btnVolver, btnFinalizarViaje, btnCancelarViaje;

    private String nombre;
    private String telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_viaje_en_curso);

        // Referencias
        nombreCliente = findViewById(R.id.txtNombreCliente);
        telefonoviajero = findViewById(R.id.telefonoviajero);
        btnVolver = findViewById(R.id.btnVolver);
        btnFinalizarViaje = findViewById(R.id.btnFinalizarViaje);
        btnCancelarViaje = findViewById(R.id.btnCancelarViaje);

        // Obtener extras
        nombre = getIntent().getStringExtra("nombre");
        telefono = getIntent().getStringExtra("telefono");

        if (nombre != null) nombreCliente.setText("Nombre: " + nombre);
        if (telefono != null) telefonoviajero.setText("Contacto: " + telefono);

        // Botón Volver → Regresa a TaxiInicioFragment
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(ViajeEnCursoActivity.this, TaxistaMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Botón Finalizar Viaje → Lanza vista de lectura QR
        btnFinalizarViaje.setOnClickListener(v -> {
            Intent qrIntent = new Intent(ViajeEnCursoActivity.this, QrLecturaActivity.class);
            qrIntent.putExtra("nombre", nombre);
            qrIntent.putExtra("telefono", telefono);
            startActivity(qrIntent);
        });

        // Botón Cancelar → Cambia estado y vuelve a inicio
        btnCancelarViaje.setOnClickListener(v -> {
            boolean actualizado = false;
            for (TarjetaModel t : TarjetaTaxistaAdapter.listaCompartida) {
                if (t.getNombreUsuario().equals(nombre) && t.getTelefono().equals(telefono)) {
                    t.setEstado("Cancelado");
                    actualizado = true;
                    break;
                }
            }

            if (actualizado) {
                Toast.makeText(this, "Viaje cancelado", Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(ViajeEnCursoActivity.this, TaxistaMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
