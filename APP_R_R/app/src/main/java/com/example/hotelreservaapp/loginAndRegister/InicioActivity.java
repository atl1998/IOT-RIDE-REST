package com.example.hotelreservaapp.loginAndRegister;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.hotelreservaapp.MainActivity;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.model.Notificacion;
import com.example.hotelreservaapp.room.AppDatabase;
import com.google.android.material.button.MaterialButton;

public class InicioActivity extends AppCompatActivity {
    //String channelId = "ChannelRideAndRest";
    MaterialButton btnLogIn;
    TextView tvRegistro, tvPostula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Enlazar vistas
        btnLogIn = findViewById(R.id.btnLogIn);
        tvRegistro = findViewById(R.id.tvRegistro);
        tvPostula = findViewById(R.id.tvPostula);

        //createNotificationChannel();
        //insertarNotificacionesEstaticas();

        // Acción del botón de login
        btnLogIn.setOnClickListener(v -> {
            // Aquí iría LoginActivity
            Intent intent = new Intent(InicioActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Acción del enlace "Regístrate"
        tvRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        // Acción del enlace "Postula aquí"
        tvPostula.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, PostulacionTaxistaActivity.class);
            startActivity(intent);
        });

    }
    /*
    public void createNotificationChannel() {
        //android.os.Build.VERSION_CODES.O == 26
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "RideAndRest",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal para notificaciones con prioridad high");
            channel.setShowBadge(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            askPermission();
        }
    }

    public void askPermission(){
        //android.os.Build.VERSION_CODES.TIRAMISU == 33
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(InicioActivity.this,
                    new String[]{POST_NOTIFICATIONS},
                    101);
        }
    }

    private void insertarNotificacionesEstaticas() {
        SharedPreferences prefs = getSharedPreferences("notis_prefs", MODE_PRIVATE);
        boolean yaInsertadas = prefs.getBoolean("notis_insertadas", false);

        if (!yaInsertadas) {
            AppDatabase db = AppDatabase.getInstance(this);
            db.notificacionDao().insertar(new Notificacion(
                    "Bienvenido SuperAdmin!",
                    "Puedes revisar y gestionar las solicitudes de nuevos usuarios.",
                    System.currentTimeMillis(),
                    false
            ));

            db.notificacionDao().insertar(new Notificacion(
                    "Revisión de reportes semanales",
                    "Existen nuevos reportes para analizar esta semana.",
                    System.currentTimeMillis(),
                    false
            ));

            db.notificacionDao().insertar(new Notificacion(
                    "Nuevas funciones",
                    "Se han añadido nuevas funciones disponibles para el rol SuperAdmin.",
                    System.currentTimeMillis(),
                    false
            ));

            prefs.edit().putBoolean("notis_insertadas", true).apply(); // 👈 Marcar como insertado
        }
    }*/
}
