package com.example.hotelreservaapp.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;

public class MapaActividad extends AppCompatActivity {

    TextView nombreCliente, telefonoviajero;
    MaterialButton btnVolver, btnConfirmarRecojo;
    boolean redirigirAViajeEnCurso = false;
    String nombre, telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_mapa);

        nombreCliente = findViewById(R.id.txtNombreCliente);
        telefonoviajero = findViewById(R.id.telefonoviajero);
        btnVolver = findViewById(R.id.btnVolver);
        btnConfirmarRecojo = findViewById(R.id.btnConfirmarRecojo);

        // Obtener extras
        Intent intent = getIntent();
        nombre = intent.getStringExtra("nombre");
        telefono = intent.getStringExtra("telefono");
        redirigirAViajeEnCurso = intent.getBooleanExtra("viajeEnCurso", false);

        if (nombre != null) nombreCliente.setText("Nombre: " + nombre);
        if (telefono != null) telefonoviajero.setText("Contacto: " + telefono);

        btnVolver.setOnClickListener(v -> {
            if (redirigirAViajeEnCurso) {
                Intent i = new Intent(this, ViajeEnCursoActivity.class);
                i.putExtra("nombre", nombre);
                i.putExtra("telefono", telefono);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            } else {
                finish(); // regresa al flujo anterior (recojo)
            }
        });

        btnConfirmarRecojo.setOnClickListener(v -> {
            Intent i = new Intent(this, ViajeEnCursoActivity.class);
            i.putExtra("nombre", nombre);
            i.putExtra("telefono", telefono);
            startActivity(i);
        });
    }
}
