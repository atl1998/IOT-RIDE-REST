package com.example.hotelreservaapp;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.hotelreservaapp.cliente.DetallesReserva;
import com.example.hotelreservaapp.cliente.HistorialEventos;
import com.example.hotelreservaapp.cliente.HomeCliente;
import com.example.hotelreservaapp.cliente.ListaHotelesCliente;
import com.example.hotelreservaapp.loginAndRegister.InicioActivity;
<<<<<<< Updated upstream
import com.example.hotelreservaapp.taxista.TaxistaMain;
import com.example.hotelreservaapp.taxista.fragments.TaxiInicioFragment;
=======
import com.example.hotelreservaapp.loginAndRegister.SplashActivity;
import com.example.hotelreservaapp.superadmin.SuperAdminActivity;
>>>>>>> Stashed changes
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    String channelId = "ChannelRideAndRest";

    // Declaración de botones
    MaterialButton btnCliente, btnTaxista, btnAdminHotel, btnSuperadmin, btnInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enlazar los botones con el layout
        btnCliente = findViewById(R.id.btnCliente);
        btnTaxista = findViewById(R.id.btnTaxista);
        btnAdminHotel = findViewById(R.id.btnAdminHotel);
        btnSuperadmin = findViewById(R.id.btnSuperadmin);
        btnInicio = findViewById(R.id.btnInicio);

        createNotificationChannel();

        // Acciones por botón (por ahora sin abrir otra Activity)
        btnCliente.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            startActivity(new Intent(this, HomeCliente.class));
        });

        btnTaxista.setOnClickListener(v -> {
            startActivity(new Intent(this, TaxistaMain.class));
        });

        btnAdminHotel.setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.hotelreservaapp.AdminHotel.MainActivity.class));
        });

        btnSuperadmin.setOnClickListener(v -> {
            startActivity(new Intent(this, SuperAdminMainActivity.class));
        });

        btnInicio.setOnClickListener(v -> {
            startActivity(new Intent(this, InicioActivity.class));
        });
    }
    // Para crear el canal uwu, esto es al incio de la app q creo q es esta c:
    public void createNotificationChannel() {
        //android.os.Build.VERSION_CODES.O == 26
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Canal de notificaciones RideAndRest HIGH",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal para notificaciones con prioridad default");
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
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{POST_NOTIFICATIONS},
                    101);
        }
    }
}
