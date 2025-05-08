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

import com.example.hotelreservaapp.databinding.SuperadminSolicitudesActivityBinding;
import com.example.hotelreservaapp.model.SolicitudTaxista;
import com.example.hotelreservaapp.model.UsuarioListaSuperAdmin;

import java.util.ArrayList;
import java.util.List;

public class SolicitudesActivity extends AppCompatActivity  {

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
        listaSolicitudes.add(new SolicitudTaxista("Jorge", "Coronado", "DNI", "12345678", "1990-01-01", "987654321", "Av. Lima 123", "maxwell@pucp.edu.pe", "A1A-000", "coronado.png", "placa1.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Pedro", "Paredes", "DNI", "87654321", "1988-06-15", "912345678", "Jr. Amazonas 456", "pedro@correo.com", "A1A-000", "pedro.jpg", "placa2.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Mario", "Casas", "DNI", "45678912", "1992-03-10", "922334455", "Calle Los Pinos 789", "mario@demo.com", "A1A-000", "mario.jpg", "placa3.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Jorge", "Coronado", "DNI", "12345679", "1991-02-01", "987654320", "Av. La Marina 123", "coronadomaxwell@pucp.edu.pe", "A1A-000", "coronado.png", "placa1.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Jorge", "Coronado 2", "DNI", "12345680", "1991-05-20", "987654322", "Av. Arequipa 321", "gcoronado2@pucp.edu.pe", "A1A-000", "coronado.png", "placa1b.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Jorge", "Coronado 3", "DNI", "12345681", "1991-07-15", "987654323", "Av. Brasil 555", "gcoronado3@pucp.edu.pe", "A1A-000", "coronado.png", "placa1c.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Jorge", "Coronado 4", "DNI", "12345682", "1991-08-01", "987654324", "Av. Javier Prado 999", "gcoronado4@pucp.edu.pe", "A1A-000", "coronado.png", "placa1d.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Pedro", "Paredes", "DNI", "87654322", "1988-08-20", "912345679", "Jr. Ayacucho 202", "pparedes@pucp.edu.pe", "A1A-000", "paredes.png", "placa2.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Mario", "Casas", "DNI", "45678913", "1992-09-10", "922334456", "Calle Las Gardenias 707", "mcasas@demo.com", "A1A-000", "casas.png", "placa3.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Lucía", "Herrera", "DNI", "40203344", "1995-10-11", "933445566", "Av. Perú 456", "lherrera@correo.com", "A1A-000", "lucia.png", "placa4.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Luis", "Fernández", "DNI", "33445566", "1987-04-18", "944556677", "Av. Abancay 101", "lfernandez@correo.com", "A1A-000", "luis.png", "placa5.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Carlos", "Ramírez", "DNI", "99887766", "1989-12-05", "955667788", "Jr. Cusco 305", "cramirez@correo.com", "A1A-000", "carlos.png", "placa6.jpg"));
        listaSolicitudes.add(new SolicitudTaxista("Ana", "Torres", "DNI", "11223344", "1993-11-23", "966778899", "Av. Grau 222", "atorres@correo.com", "A1A-000", "ana.png", "placa7.jpg"));
        adapter.setListaCompleta(listaSolicitudes);
    }
}