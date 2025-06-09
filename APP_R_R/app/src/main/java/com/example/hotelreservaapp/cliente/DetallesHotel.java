package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.os.Bundle;

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


    MaterialButton btnBusqueda;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_detalles_hotel);

        // Botón para elegir habitación
        btnBusqueda = findViewById(R.id.btnChooseRoom);
        btnBusqueda.setOnClickListener(v -> {
            startActivity(new Intent(this, ListaHabitaciones.class));
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
}