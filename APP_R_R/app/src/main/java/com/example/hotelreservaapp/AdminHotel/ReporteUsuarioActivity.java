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
import com.example.hotelreservaapp.AdminHotel.ReporteUsuarioAdapter;
import com.example.hotelreservaapp.model.Usuario;
import com.example.hotelreservaapp.model.UsuarioListaSuperAdmin;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ReporteUsuarioActivity extends AppCompatActivity {

    MaterialButton btnNotificaiones;
    MaterialButton btnBuscar;
    
    ReporteUsuarioAdapter adapter;
    
    RecyclerView recyclerView;
    
    List<UsuarioListaSuperAdmin> lsitaUsuarios = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.adminhotel_activity_reporte_usuario);
        cargarUsuariosDeEjemplo();

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
            adapter = new ReporteUsuarioAdapter(this , lsitaUsuarios);
            recyclerView.setAdapter(adapter);

        });


    }

    private void cargarUsuariosDeEjemplo() {
        lsitaUsuarios.add(new UsuarioListaSuperAdmin("Jorge Coronado", "Check-in: Lun 07, 14:35", "Vier 11","coronado.png" , true));
        lsitaUsuarios.add(new UsuarioListaSuperAdmin("Lucía Quispe", "Check-in: Lun 07, 14:35", "Vier 11", "", true));
        lsitaUsuarios.add(new UsuarioListaSuperAdmin("Ana Pérez", "Check-in: Lun 07, 14:35", "Vier 11", "", true));
        lsitaUsuarios.add(new UsuarioListaSuperAdmin("Giorgio Maxwell", "Check-in: Lun 07, 14:35", "Vier 11", "coronado.png", true));
        lsitaUsuarios.add(new UsuarioListaSuperAdmin("Nilo Cori", "Check-in: Lun 07, 14:35", "Vier 11", "", true));
        lsitaUsuarios.add(new UsuarioListaSuperAdmin("Juan Pérez", "Check-in: Lun 07, 14:35", "Vier 11", "", true));
        lsitaUsuarios.add(new UsuarioListaSuperAdmin("Adrian Tipo", "Check-in: Lun 07, 14:35", "Vier 11", "", true));
        lsitaUsuarios.add(new UsuarioListaSuperAdmin("Adrian López", "Check-in: Lun 07, 14:35", "Vier 11", "", true));
        lsitaUsuarios.add(new UsuarioListaSuperAdmin("Pedro BM", "Check-in: Lun 07, 14:35", "Vier 11", "pedro.png", true));
    }
}