package com.example.hotelreservaapp.superadmin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.adapter.SolicitudAdapter;
import com.example.hotelreservaapp.adapter.UsuarioAdapter;
import com.example.hotelreservaapp.base.BaseBottomNavActivity;
import com.example.hotelreservaapp.databinding.ActivitySuperadminBinding;
import com.example.hotelreservaapp.databinding.SuperadminSolicitudesActivityBinding;
import com.example.hotelreservaapp.model.SolicitudTaxista;
import com.example.hotelreservaapp.model.UsuarioListaSuperAdmin;

import java.util.ArrayList;
import java.util.List;

public class SolicitudesActivity extends BaseBottomNavActivity {

    private SuperadminSolicitudesActivityBinding binding;
    private SolicitudAdapter adapter;
    private List<SolicitudTaxista> listaSolicitudes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SuperadminSolicitudesActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        cargarSolicitudesDeEjemplo();
    }

    private void cargarSolicitudesDeEjemplo() {
        listaSolicitudes.add(new SolicitudTaxista("Jorge coronado", "maxwell@pucp.edu.pe", "A1A-000", "coronado.png", "placa1.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Pedro Paredes", "pedro@correo.com", "A1A-000", "pedro.jpg", "placa2.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Mario Casas", "mario@demo.com", "A1A-000", "mario.jpg", "placa3.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Jorge Coronado", "coronadomaxwell@pucp.edu.pe", "A1A-000", "coronado.png", "placa1.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Jorge Coronado 2", "gcoronado2@pucp.edu.pe", "A1A-000", "coronado.png", "placa1b.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Jorge Coronado 3", "gcoronado3@pucp.edu.pe", "A1A-000", "coronado.png", "placa1c.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Jorge Coronado 4", "gcoronado4@pucp.edu.pe", "A1A-000", "coronado.png", "placa1d.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Pedro Paredes", "pparedes@pucp.edu.pe", "A1A-000", "paredes.png", "placa2.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Mario Casas", "mcasas@demo.com", "A1A-000", "casas.png", "placa3.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Lucía Herrera", "lherrera@correo.com", "A1A-000", "lucia.png", "placa4.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Luis Fernández", "lfernandez@correo.com", "A1A-000", "luis.png", "placa5.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Carlos Ramírez", "cramirez@correo.com", "A1A-000", "carlos.png", "placa6.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Ana Torres", "atorres@correo.com", "A1A-000", "ana.png", "placa7.jpg"));
        adapter.setListaCompleta(listaSolicitudes);
    }
}