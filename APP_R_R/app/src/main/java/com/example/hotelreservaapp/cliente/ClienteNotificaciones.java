package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.NotificacionDetalles;
import com.example.hotelreservaapp.Objetos.Notificaciones;
import com.example.hotelreservaapp.Objetos.NotificacionesStorageHelper;
import com.example.hotelreservaapp.Objetos.NotificationManagerNoAPP;
import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;
import java.util.List;

public class ClienteNotificaciones extends AppCompatActivity {
    private RecyclerView recyclerNotificaciones;
    private MaterialButton borrarNotificacioens;

    private NotificationManagerNoAPP notificationManagerNoAPP; // tu clase para manejar la lista
    private NotificacionesStorageHelper storageHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cliente_notificaciones);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerNotificaciones = findViewById(R.id.recyclerNotificaciones);
        recyclerNotificaciones.setLayoutManager(new LinearLayoutManager(this));

        // Instanciamos el helper para leer el archivo
        NotificacionesStorageHelper storageHelper = new NotificacionesStorageHelper(this);
        Notificaciones[] notificacionesGuardadas = storageHelper.leerArchivoNotificacionesDesdeSubcarpeta();

        if (notificacionesGuardadas != null && notificacionesGuardadas.length > 0) {
            List<Notificaciones> listaNotificaciones = Arrays.asList(notificacionesGuardadas);
            NotificacionAdapter adapter = new NotificacionAdapter(listaNotificaciones, this, notificacion -> {
                Intent intent = new Intent(ClienteNotificaciones.this, NotificacionDetalles.class);
                intent.putExtra("notificacion", notificacion);  // notificacion es el objeto completo
                startActivity(intent);
            });
            recyclerNotificaciones.setAdapter(adapter);
        }


        Button btnVolver = findViewById(R.id.volverPagina);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Esto regresa al Activity anterior
            }
        });

        borrarNotificacioens = findViewById(R.id.borrarNotificacioens);

        borrarNotificacioens.setOnClickListener(v -> {
            storageHelper.borrarArchivoNotificaciones(this);
            SharedPreferences sharedPreferences = getSharedPreferences("ReservaPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("horaLlegada"); // Aquí pones la clave que quieres borrar
            editor.remove("SolicitarCheckout");
            editor.remove("ServicioTaxi");
            editor.apply(); // O editor.commit();

            // Recargar actividad
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        });
    }

}
