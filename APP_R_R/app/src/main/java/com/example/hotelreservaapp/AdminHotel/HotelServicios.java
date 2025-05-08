package com.example.hotelreservaapp.AdminHotel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class HotelServicios extends AppCompatActivity {

    private RecyclerView rvServicios;
    private com.example.hotelreservaapp.AdminHotel.ServiciosAdapter adapter;
    private List<Servicios> listaServicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.adminhotel_activity_hotel_servicios);

        rvServicios = findViewById(R.id.listaServicios);
        rvServicios.setLayoutManager(new LinearLayoutManager(this));
        rvServicios.setHasFixedSize(true);

        // Inicializar lista de habitaciones
        listaServicios = new ArrayList<>();
        listaServicios.add(new Servicios(
                "Buffet Almuerzo",
                "50",
                "adminhotel_servicio_buffet.jpg",
                "El buffet incluye todo tipo de comida criolla; nuestra carta incluye segundos como el arroz con pollo, carapulcra, entre otros; bebidas y postras como la chicha, mazamorra, agua de maracuyá, arroz con leche, causa rellena y más"
        ));


        listaServicios.add(new Servicios(
                "Clase de yoga",
                "30",
                "adminhotel_servicio_yoga.jpg",
                "Únete a nuestra clase de yoga, donde experimentarás una práctica relajante que mejora tu flexibilidad, fortalece el cuerpo y equilibra la mente. Ideal para todos los niveles, con un enfoque en el bienestar y la calma interior.\n"
        ));

        listaServicios.add(new Servicios(
                "Gimnasio",
                "40",
                "adminhotel_servicio_hym.jpg",
                "Transforma tu cuerpo y mente en nuestro gimnasio, donde ofrecemos entrenamientos personalizados para mejorar tu fuerza, resistencia y bienestar general. ¡Únete a nosotros y alcanza tus metas de fitness en un ambiente motivador!\n"
        ));



        //Inicializamos el adapter
        adapter = new ServiciosAdapter(listaServicios,this,  new ServiciosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Servicios servicio = listaServicios.get(position);
                /*mostrarPopupSeleccion(habitacion);*/
            }

            @Override
            public void onSeleccionCambio() {
                /*verificarSeleccion();*/
            }
        });

        rvServicios.setAdapter(adapter);
        MaterialButton btnDetalles = findViewById(R.id.btnAgregar);


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