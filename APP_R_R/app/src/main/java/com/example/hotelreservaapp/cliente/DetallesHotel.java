package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetallesHotel extends AppCompatActivity {

    private RecyclerView recyclerComentarios;
    private ComentarioAdapter comentarioAdapter;
    private List<Comentario> listaComentarios;

    private MaterialButton btnVolver;

    private TextView tvHotelName;


    private long fechaInicioMillis;
    private long fechaFinMillis;
    private int adultos;
    private int ninos;



    MaterialButton btnBusqueda;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_detalles_hotel);


        // Obtener el hotelId desde el Intent
        String hotelId = getIntent().getStringExtra("hotelId");
        // ✅ Obtener los datos enviados por HotelAdapter
        fechaInicioMillis = getIntent().getLongExtra("fechaInicio", -1);
        fechaFinMillis = getIntent().getLongExtra("fechaFin", -1);
        adultos = getIntent().getIntExtra("adultos", 0);
        ninos = getIntent().getIntExtra("ninos", 0);

        // Aquí, puedes obtener los detalles del hotel usando el hotelId, por ejemplo con Firestore:
        obtenerDetallesHotel(hotelId);

        // Botón para elegir habitación
        btnBusqueda = findViewById(R.id.btnChooseRoom);
        btnBusqueda.setOnClickListener(v -> {
            // Pasar el hotelId a la actividad ListaHabitaciones
            Intent intent = new Intent(this, ListaHabitaciones.class);
            intent.putExtra("hotelId", hotelId);  // Pasamos el hotelId al Intent
            intent.putExtra("fechaInicio", fechaInicioMillis);
            intent.putExtra("fechaFin", fechaFinMillis);
            intent.putExtra("adultos", adultos);
            intent.putExtra("ninos", ninos);
            startActivity(intent);  // Iniciar la actividad ListaHabitaciones
        });

        // Botón para volver
        btnVolver = findViewById(R.id.volverAnterior);
        btnVolver.setOnClickListener(v -> {
            startActivity(new Intent(this, ListaHotelesCliente.class));
        });

        // --- CONFIGURAR COMENTARIOS ---
        recyclerComentarios = findViewById(R.id.recyclerViewComentarios);
        recyclerComentarios.setLayoutManager(new LinearLayoutManager(this));

        listaComentarios = new ArrayList<>();
        comentarioAdapter = new ComentarioAdapter(this, listaComentarios);
        recyclerComentarios.setAdapter(comentarioAdapter);
        cargarDatosEjemplo();




        // --- CONFIGURAR CARRUSEL DE IMÁGENES ---
        ImageCarousel carousel = findViewById(R.id.carousel);
        carousel.registerLifecycle(getLifecycle());


        List<CarouselItem> images = new ArrayList<>();
        images.add(new CarouselItem(R.drawable.hotel1_img1));
        images.add(new CarouselItem(R.drawable.hotel1_img2));
        images.add(new CarouselItem(R.drawable.hotel1_img3));
        carousel.setData(images);

    }


    /**
     * Método para cargar datos de ejemplo en la lista de comentarios
     */
    private void cargarDatosEjemplo() {
        // Agregar algunos comentarios de ejemplo
        listaComentarios.add(new Comentario("Jose Antonio", 4.0f, "La atención y el servicio fueron excelentes", R.drawable.chat_persona));
        listaComentarios.add(new Comentario("María López", 5.0f, "Me encantó la habitación, muy cómoda y limpia", R.drawable.chat_persona));
        listaComentarios.add(new Comentario("Carlos Rodríguez", 3.5f, "Buena ubicación pero el desayuno no es muy variado", R.drawable.chat_persona));
        listaComentarios.add(new Comentario("Ana Gómez", 4.5f, "El personal del hotel es muy amable y atento", R.drawable.chat_persona));
        listaComentarios.add(new Comentario("Pedro Sánchez", 3.0f, "El WiFi no funcionaba correctamente en mi habitación", R.drawable.chat_persona));
    }

    private void obtenerDetallesHotel(String hotelId) {
        // Lógica para obtener los detalles del hotel desde Firestore usando hotelId
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Hoteles").document(hotelId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String nombre = document.getString("nombre");
                        String ubicacion = document.getString("ubicacion");
                        String contacto = document.getString("contacto");
                        // Asignar estos valores a los TextViews
                        tvHotelName = findViewById(R.id.tvHotelName);  // Asegúrate de que tienes este TextView en tu layout
                        tvHotelName.setText(nombre);
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error al obtener detalles del hotel", e));
    }

}