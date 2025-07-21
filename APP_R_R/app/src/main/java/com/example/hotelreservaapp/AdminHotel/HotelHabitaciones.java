package com.example.hotelreservaapp.AdminHotel;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.AdminHotel.Adapter.HabitacionAdapter;
import com.example.hotelreservaapp.AdminHotel.Model.Habitacion;
import com.example.hotelreservaapp.AdminHotel.ViewModel.RegistroViewModel;
import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HotelHabitaciones extends AppCompatActivity {

    private RecyclerView rvHabitaciones;
    private HabitacionAdapter adapter;
    private List<Habitacion> listaHabitaciones;
    private FirebaseFirestore db;
    private FirebaseUser usuarioActual;
    private String idHotel;

    private RegistroViewModel registroViewModel;

    Button btnReservar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminhotel_activity_hotel_habitaciones);

        rvHabitaciones = findViewById(R.id.listaHabitaciones);
        rvHabitaciones.setLayoutManager(new LinearLayoutManager(this));
        rvHabitaciones.setHasFixedSize(true);

        db = FirebaseFirestore.getInstance();
        usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        registroViewModel = new ViewModelProvider(this).get(RegistroViewModel.class);

        // Inicializar lista de habitaciones
        listaHabitaciones = new ArrayList<>();

        //Inicializamos el adapter
        adapter = new HabitacionAdapter(listaHabitaciones,this,  new HabitacionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Habitacion habitacion = listaHabitaciones.get(position);
                /*mostrarPopupSeleccion(habitacion);*/
            }

            @Override
            public void onSeleccionCambio() {
                /*verificarSeleccion();*/
            }
        });

        rvHabitaciones.setAdapter(adapter);

        registroViewModel.getHotel().observe(this, hotel -> {
            if (hotel == null) return;
            listaHabitaciones.clear();
            listaHabitaciones.addAll(hotel.getHabitaciones());
            adapter.notifyDataSetChanged();
        });

        //Cargamos el id del hotel
        if (usuarioActual != null) {
            db.collection("usuarios").document(usuarioActual.getUid()).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String idHotel = document.getString("idHotel");
                            registroViewModel.loadHotelWithSubcollections(idHotel);
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                    );
        }


        /*btnReservar = findViewById(R.id.btnReservarAhora);

        btnReservar.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            startActivity(new Intent(this, DetalleHabitacionActivity.class));
        });*

        /*
        private void mostrarPopupSeleccion(Habitaciones habitacion){
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
        }*/

        /*
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
        }*/

        //Para ir a notificaciones
        MaterialButton btnNotificaiones = findViewById(R.id.NotificacionesAdminHotel);
        btnNotificaiones.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            startActivity(new Intent(this, NotificacionesActivity.class));
        });

        // Usamos un OnClickListener estándar
        MaterialButton backButton = findViewById(R.id.backBottom);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Usar el método tradicional
            }
        });
    }
}