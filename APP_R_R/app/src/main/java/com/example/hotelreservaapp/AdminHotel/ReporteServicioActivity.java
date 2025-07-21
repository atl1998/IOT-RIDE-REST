package com.example.hotelreservaapp.AdminHotel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.AdminHotel.Adapter.ReporteServicioAdapter;
import com.example.hotelreservaapp.AdminHotel.Model.Servicio;
import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ReporteServicioActivity extends AppCompatActivity {

    List<Servicio> serviciosList = new ArrayList<>();
    MaterialButton btnNotificaiones;
    MaterialButton btnBuscar;
    ReporteServicioAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminhotel_activity_reporte_servicio);
        cargarServiciosDeEjemplo();

        //Para ir a notificaciones
        btnNotificaiones = findViewById(R.id.NotificacionesAdminHotel);
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

        //Para jalar el recyclerview
        btnBuscar = findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(v ->{
            recyclerView = findViewById(R.id.recyclerUsers);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            adapter = new ReporteServicioAdapter(serviciosList, this);
            recyclerView.setAdapter(adapter);

        });

    }

    private void cargarServiciosDeEjemplo() {
        serviciosList.add(new Servicio("Buffet criollo", "ga", 332.00, "url"));
        serviciosList.add(new Servicio("Desayuno buffet", "ga", 132.00, "url"));

    }
}