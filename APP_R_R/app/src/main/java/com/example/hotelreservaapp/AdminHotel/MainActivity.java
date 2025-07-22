package com.example.hotelreservaapp.AdminHotel;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.hotelreservaapp.AdminHotel.Fragments.HotelFragment;
import com.example.hotelreservaapp.AdminHotel.Fragments.InicioFragment;
import com.example.hotelreservaapp.AdminHotel.Fragments.PerfilFragment;
import com.example.hotelreservaapp.AdminHotel.Fragments.ReportesFragment;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.adapter.UsuarioAdapter;
import com.example.hotelreservaapp.databinding.AdminhotelMainBinding;
import com.example.hotelreservaapp.model.UsuarioListaSuperAdmin;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    String channelId = "ChannelRideAndRestAdmin";
    private AdminhotelMainBinding binding;
    private UsuarioAdapter adapter;
    private List<UsuarioListaSuperAdmin> listaOriginal = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminhotel_main);

        createNotificationChannel();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.adminhotel_menu_inicio) {
                loadFragment(new InicioFragment());
            } else if (id == R.id.adminhotel_menu_hotel) {
                loadFragment(new HotelFragment());
            } else if (id == R.id.adminhotel_menu_reportes) {
                loadFragment(new ReportesFragment());
            } else if (id == R.id.adminhotel_menu_perfil) {
                loadFragment(new PerfilFragment());
            }
            return true;
        });



        // Cargar el fragmento inicial
        if (savedInstanceState == null) {
            loadFragment(new InicioFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.adminhotel_container_view, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void createNotificationChannel() {
        //android.os.Build.VERSION_CODES.O == 26
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "RideAndRestAdmin",
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
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{POST_NOTIFICATIONS},
                    101);
        }
    }
}