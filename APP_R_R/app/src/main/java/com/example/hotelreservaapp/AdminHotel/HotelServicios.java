package com.example.hotelreservaapp.AdminHotel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.AdminHotel.Adapter.ServicioAdapter;
import com.example.hotelreservaapp.AdminHotel.Model.Servicio;
import com.example.hotelreservaapp.AdminHotel.ViewModel.RegistroViewModel;
import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HotelServicios extends AppCompatActivity {

    private RecyclerView rvServicios;
    private com.example.hotelreservaapp.AdminHotel.Adapter.ServicioAdapter adapter;
    private List<Servicio> listaServicios;
    private FirebaseFirestore db;
    private FirebaseUser usuarioActual;
    private RegistroViewModel registroViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.adminhotel_activity_hotel_servicios);

        rvServicios = findViewById(R.id.listaServicios);
        rvServicios.setLayoutManager(new LinearLayoutManager(this));
        rvServicios.setHasFixedSize(true);

        db = FirebaseFirestore.getInstance();
        usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        registroViewModel = new ViewModelProvider(this).get(RegistroViewModel.class);


        // Inicializar lista de habitaciones
        listaServicios = new ArrayList<>();
        //Inicializamos el adapter
        adapter = new ServicioAdapter(listaServicios,this,  new ServicioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Servicio servicio = listaServicios.get(position);
                /*mostrarPopupSeleccion(habitacion);*/
            }

            @Override
            public void onSeleccionCambio() {
                /*verificarSeleccion();*/
            }
        });
        rvServicios.setAdapter(adapter);

        registroViewModel.getHotel().observe(this, hotel -> {
                if (hotel == null) return;
                listaServicios.clear();
                listaServicios.addAll(hotel.getServicios());
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

        //MaterialButton btnDetalles = findViewById(R.id.btnAgregar);


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