package com.example.hotelreservaapp.cliente;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class ListaHabitaciones extends AppCompatActivity {

    private RecyclerView rvHabitaciones;
    private HabitacionAdapter adapter;
    private List<Habitacion> listaHabitaciones;

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

        // Configurar adaptador
        adapter = new HabitacionAdapter(listaHabitaciones, position -> {
            Habitacion habitacionSeleccionada = listaHabitaciones.get(position);
            Toast.makeText(ListaHabitaciones.this,
                    "Seleccionaste: " + habitacionSeleccionada.getTitulo(),
                    Toast.LENGTH_SHORT).show();
            // Aquí puedes agregar la lógica para reservar la habitación
        });

        rvHabitaciones.setAdapter(adapter);
    }
}