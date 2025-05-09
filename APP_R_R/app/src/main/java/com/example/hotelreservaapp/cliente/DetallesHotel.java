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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetallesHotel extends AppCompatActivity {

    private RecyclerView recyclerComentarios;
    private ComentarioAdapter comentarioAdapter;
    private List<Comentario> listaComentarios;

    private MaterialButton btnVolver;


    private ViewPager2 imageCarousel;
    MaterialButton btnBusqueda;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_detalles_hotel);

        btnBusqueda = findViewById(R.id.btnChooseRoom);
        btnBusqueda.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            startActivity(new Intent(this, ListaHabitaciones.class));
        });



        // Configurar el RecyclerView
        recyclerComentarios = findViewById(R.id.recyclerView);
        recyclerComentarios.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar la lista de comentarios
        listaComentarios = new ArrayList<>();
        // Crear el adaptador y asignarlo al RecyclerView
        comentarioAdapter = new ComentarioAdapter(this, listaComentarios);
        recyclerComentarios.setAdapter(comentarioAdapter);

        // Cargar datos de ejemplo (esto podría venir de una API o base de datos)
        cargarDatosEjemplo();



        imageCarousel = findViewById(R.id.imageCarousel);

        // Lista de imágenes del carrusel (asegúrate de que existan en drawable)
        List<Integer> images = Arrays.asList(
                R.drawable.hotel1_img1,
                R.drawable.hotel1_img2,
                R.drawable.hotel1_img3
        );

        ImageCarouselAdapter adapter = new ImageCarouselAdapter(images);
        imageCarousel.setAdapter(adapter);

        btnVolver = findViewById(R.id.volverAnterior);
        btnVolver.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            startActivity(new Intent(this, ListaHotelesCliente.class));
        });
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