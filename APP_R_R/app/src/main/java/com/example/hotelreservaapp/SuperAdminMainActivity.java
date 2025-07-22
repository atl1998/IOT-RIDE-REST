package com.example.hotelreservaapp;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.hotelreservaapp.model.Notificacion;
import com.example.hotelreservaapp.room.AppDatabase;
import com.example.hotelreservaapp.superadmin.GestionUsuariosFragment;
import com.example.hotelreservaapp.superadmin.PerfilFragment;
import com.example.hotelreservaapp.superadmin.ReportesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SuperAdminMainActivity extends AppCompatActivity {
    String channelId = "ChannelRideAndRest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_main_activity);

        createNotificationChannel();
        insertarNotificacionesEstaticas();


        // Mostrar el fragmento inicial por defecto
        if (savedInstanceState == null) {
            loadFragment(new GestionUsuariosFragment());
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationSA);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();

            if (itemId == R.id.menu_inicio) {
                selectedFragment = new GestionUsuariosFragment();
            } else if (itemId == R.id.menu_reportes) {
                selectedFragment = new ReportesFragment();
            } else if (itemId == R.id.menu_perfil) {
                selectedFragment = new PerfilFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

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
            ActivityCompat.requestPermissions(SuperAdminMainActivity.this,
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
    }

}