package com.example.hotelreservaapp.superadmin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.adapter.SolicitudAdapter;
import com.example.hotelreservaapp.adapter.UsuarioAdapter;

import com.example.hotelreservaapp.databinding.SuperadminSolicitudesActivityBinding;
import com.example.hotelreservaapp.model.PostulacionTaxista;
import com.example.hotelreservaapp.model.SolicitudTaxista;
import com.example.hotelreservaapp.model.UsuarioListaSuperAdmin;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class SolicitudesActivity extends AppCompatActivity  {

    private SuperadminSolicitudesActivityBinding binding;
    private SolicitudAdapter adapter;
    private List<PostulacionTaxista> listaSolicitudes = new ArrayList<>();

    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SuperadminSolicitudesActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();

        // Botón de regreso
        binding.btnBack.setOnClickListener(v -> finish());

        // RecyclerView
        adapter = new SolicitudAdapter(this, new ArrayList<>());
        binding.recyclerSolicitudes.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerSolicitudes.setAdapter(adapter);

        // Buscar
        binding.etBuscarSolicitud.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filtrar(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Cargar data
        cargarSolicitudesDesdeFirestore();
    }

    private void cargarSolicitudesDesdeFirestore() {
        // Limpiamos la lista actual antes de cargar nuevos datos
        listaSolicitudes.clear();

        // Consulta a la colección "postulacionesTaxistas"
        // Filtramos por estadoSolicitud == "pendiente_revision"
        firestore.collection("postulacionesTaxistas")
                .whereEqualTo("estadoSolicitud", "pendiente")
                // Opcional: ordenar por fecha de postulación para ver las más antiguas primero
                .orderBy("fechaPostulacion", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (com.google.firebase.firestore.DocumentSnapshot document : task.getResult()) {
                            // Convertimos el documento a un objeto PostulacionTaxista
                            PostulacionTaxista postulacion = document.toObject(PostulacionTaxista.class);
                            if (postulacion != null) {
                                // Puedes establecer el ID del documento si lo necesitas en el adaptador
                                // postulacion.setId(document.getId());
                                listaSolicitudes.add(postulacion);
                            }
                        }
                        // Actualizar el adaptador con la lista cargada
                        adapter.setListaCompleta(listaSolicitudes);
                        adapter.notifyDataSetChanged(); // Notificar cambios al RecyclerView
                        if (listaSolicitudes.isEmpty()) {
                            Toast.makeText(this, "No hay solicitudes pendientes.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("FirestoreError", "Error al cargar solicitudes: " + task.getException());
                        Toast.makeText(this, "Error al cargar solicitudes.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}