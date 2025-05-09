package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class ListaHabitaciones extends AppCompatActivity {

    private RecyclerView rvHabitaciones;
    private HabitacionAdapter adapter;
    private List<Habitacion> listaHabitaciones;

    private MaterialButton btnVolver;

    Button btnReservar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_habitaciones);

        rvHabitaciones = findViewById(R.id.rvHabitaciones);
        rvHabitaciones.setLayoutManager(new LinearLayoutManager(this));
        rvHabitaciones.setHasFixedSize(true);

        // Inicializar lista de habitaciones
        listaHabitaciones = new ArrayList<>();
        listaHabitaciones.add(new Habitacion(
                "Habitacion superior - 1 cama grande",
                "- Precio para 2 adultos\n- 1 cama doble grande\n- Turneño 25 m2\n- WiFi de alto velocidad\n- Desayuno incluido",
                3,
                354.00,
                "Grande",
                25
        ));

        listaHabitaciones.add(new Habitacion(
                "Habitacion deluxe cama extragrande",
                "- Precio para 2 adultos\n- 1 cama doble extra grande\n- Turneño 30 m2\n- WiFi de alto velocidad\n- Desayuno incluido",
                2,
                417.00,
                "Extra grande",
                30
        ));
        listaHabitaciones.add(new Habitacion(
                "Habitacion deluxe cama extragrande",
                "- Precio para 2 adultos\n- 1 cama doble extra grande\n- Turneño 30 m2\n- WiFi de alto velocidad\n- Desayuno incluido",
                2,
                417.00,
                "Extra grande",
                30
        ));
        listaHabitaciones.add(new Habitacion(
                "Habitacion deluxe cama extragrande",
                "- Precio para 2 adultos\n- 1 cama doble extra grande\n- Turneño 30 m2\n- WiFi de alto velocidad\n- Desayuno incluido",
                2,
                417.00,
                "Extra grande",
                30
        ));
        listaHabitaciones.add(new Habitacion(
                "Habitacion deluxe cama extragrande",
                "- Precio para 2 adultos\n- 1 cama doble extra grande\n- Turneño 30 m2\n- WiFi de alto velocidad\n- Desayuno incluido",
                2,
                417.00,
                "Extra grande",
                30
        ));

        adapter = new HabitacionAdapter(listaHabitaciones, new HabitacionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Habitacion habitacion = listaHabitaciones.get(position);
                mostrarPopupSeleccion(habitacion);
            }

            @Override
            public void onSeleccionCambio() {
                verificarSeleccion();
            }
        });

        rvHabitaciones.setAdapter(adapter);
        btnReservar = findViewById(R.id.btnReservarAhora);
        btnReservar.setOnClickListener(v -> {
                    //por ahora directamente al mio bala
            startActivity(new Intent(this, HistorialEventos.class));
        });


        btnVolver = findViewById(R.id.volverHotel);
        btnVolver.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            startActivity(new Intent(this, DetallesHotel.class));
        });

    }
    private void mostrarPopupSeleccion(Habitacion habitacion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.cliente_seleccion_popup_habitaciones, null);

        TextView titulo = view.findViewById(R.id.titulo_habitacion);
        Button incrementar = view.findViewById(R.id.incrementar_button);
        Button decrementar = view.findViewById(R.id.decrementar_button);
        TextView cantidadText = view.findViewById(R.id.cantidad_textview);
        TextView precioTotal = view.findViewById(R.id.precio_total_textview);
        Button confirmar = view.findViewById(R.id.confirmar_button);

        titulo.setText(habitacion.getTitulo());

        final int[] cantidad = {1}; // Cantidad por defecto
        double precioPorUnidad = habitacion.getPrecio();

        precioTotal.setText("Precio total: S/" + String.format("%.2f", precioPorUnidad));

        incrementar.setOnClickListener(v -> {
            if (cantidad[0] < habitacion.getDisponibles()) {
                cantidad[0]++;
                cantidadText.setText(String.valueOf(cantidad[0]));
                precioTotal.setText("Precio total: S/" + String.format("%.2f", cantidad[0] * precioPorUnidad));
            }
        });

        decrementar.setOnClickListener(v -> {
            if (cantidad[0] > 1) {
                cantidad[0]--;
                cantidadText.setText(String.valueOf(cantidad[0]));
                precioTotal.setText("Precio total: S/" + String.format("%.2f", cantidad[0] * precioPorUnidad));
            }
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        confirmar.setOnClickListener(v -> {
            Toast.makeText(this, "Reservaste " + cantidad[0] + " habitación(es)", Toast.LENGTH_SHORT).show();
            habitacion.setSeleccionadas(cantidad[0]);
            adapter.notifyDataSetChanged();
            dialog.dismiss();
            verificarSeleccion(); // <-- Nueva llamada aquí
        });
    }
    private void verificarSeleccion() {
        boolean haySeleccion = false;
        for (Habitacion h : listaHabitaciones) {
            if (h.getSeleccionadas() > 0) {
                haySeleccion = true;
                break;
            }
        }
        btnReservar.setVisibility(haySeleccion ? View.VISIBLE : View.GONE);
    }

}