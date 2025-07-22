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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListaHabitaciones extends AppCompatActivity {

    private RecyclerView rvHabitaciones;
    private HabitacionAdapter adapter;
    private List<Habitacion> listaHabitaciones;

    private MaterialButton btnVolver;
    private FirebaseFirestore db;

    Button btnReservar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_habitaciones);

        rvHabitaciones = findViewById(R.id.rvHabitaciones);
        rvHabitaciones.setLayoutManager(new LinearLayoutManager(this));
        rvHabitaciones.setHasFixedSize(true);

        String hotelId=getIntent().getStringExtra("hotelId");
        long fechaInicioMillis = getIntent().getLongExtra("fechaInicio", -1);
        long fechaFinMillis = getIntent().getLongExtra("fechaFin", -1);
        int adultos = getIntent().getIntExtra("adultos", 0);
        int ninos = getIntent().getIntExtra("ninos", 0);

        // Inicializar lista de habitaciones
        listaHabitaciones = new ArrayList<>();
        db= FirebaseFirestore.getInstance();

        // Obtener los productos del usuario desde Firestore
        obtenerHabitaciones(hotelId);

        // Configurar el adaptador

        adapter = new HabitacionAdapter(listaHabitaciones, new HabitacionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // 🔁 Deseleccionar todas las demás habitaciones
                for (int i = 0; i < listaHabitaciones.size(); i++) {
                    if (i != position) {
                        listaHabitaciones.get(i).setSeleccionadas(0);
                    }
                }

                // Actualizar el adapter para reflejar los cambios visualmente
                adapter.notifyDataSetChanged();

                // Mostrar el popup solo para la habitación seleccionada
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
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "Debes iniciar sesión para reservar", Toast.LENGTH_SHORT).show();
                return;
            }

            Habitacion habitacionSeleccionada = null;
            for (Habitacion habitacion : listaHabitaciones) {
                if (habitacion.getSeleccionadas() > 0) {
                    habitacionSeleccionada = habitacion;
                    break;
                }
            }


            if (habitacionSeleccionada==null) {
                Toast.makeText(this, "Selecciona al menos una habitación", Toast.LENGTH_SHORT).show();
                return;
            }


            Intent intent = new Intent(this, ValidacionTarjeta.class);
            intent.putExtra("hotelId", getIntent().getStringExtra("hotelId"));
            intent.putExtra("fechaInicio", getIntent().getLongExtra("fechaInicio", -1));
            intent.putExtra("fechaFin", getIntent().getLongExtra("fechaFin", -1));
            intent.putExtra("adultos", getIntent().getIntExtra("adultos", 0));
            intent.putExtra("ninos", getIntent().getIntExtra("ninos", 0));
            intent.putExtra("habitacionSeleccionada", habitacionSeleccionada);

            startActivity(intent);
        });

        btnVolver = findViewById(R.id.volverHotel);
        btnVolver.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            finish();
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

        titulo.setText(habitacion.getNombre());

        final int[] cantidad = {1}; // Cantidad por defecto
        double precioPorUnidad = habitacion.getPrecio();

        precioTotal.setText("Precio total: S/" + String.format("%.2f", precioPorUnidad));

        incrementar.setOnClickListener(v -> {
            if (cantidad[0] < habitacion.getCantDisponible()) {
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
            // ✅ Deseleccionar las demás habitaciones
            for (Habitacion h : listaHabitaciones) {
                h.setSeleccionadas(0);
            }

            // ✅ Seleccionar solo la actual
            habitacion.setSeleccionadas(cantidad[0]);

            adapter.notifyDataSetChanged();
            dialog.dismiss();
            verificarSeleccion();
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

    private void obtenerHabitaciones(String hotelId) {
        db.collection("Hoteles")
                .document(hotelId)
                .collection("habitaciones")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaHabitaciones.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();  // ID del documento
                            String nombreHab = document.getString("nombreHabitacion");
                            Double precioHab = document.getDouble("precioPorNoche");
                            Long cantHabLong = document.getLong("cantidadDisponible");

                            int cantHab = cantHabLong != null ? cantHabLong.intValue() : 0;

                            Habitacion habitacion = new Habitacion(id, nombreHab, cantHab, precioHab != null ? precioHab : 0.0);
                            listaHabitaciones.add(habitacion);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("Firebase", "Error al obtener habitaciones", task.getException());
                        Toast.makeText(ListaHabitaciones.this, "Error al cargar las habitaciones", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}