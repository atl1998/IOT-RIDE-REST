package com.example.hotelreservaapp.superadmin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.MainActivity;
import com.example.hotelreservaapp.R;
import java.util.List;
import com.example.hotelreservaapp.databinding.SuperadminNotificacionesBinding;
import com.example.hotelreservaapp.model.Notificacion;
import com.example.hotelreservaapp.room.AppDatabase;

import java.util.List;

public class NotificacionesActivity extends AppCompatActivity {

    private SuperadminNotificacionesBinding binding;
    private NotificacionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Usa solo ViewBinding
        binding = SuperadminNotificacionesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ✅ Configura RecyclerView
        binding.recyclerNotificaciones.setLayoutManager(new LinearLayoutManager(this));

        // ✅ Carga las notificaciones desde Room
        List<Notificacion> lista = AppDatabase.getInstance(this).notificacionDao().obtenerTodas();

        adapter = new NotificacionAdapter(lista);
        binding.recyclerNotificaciones.setAdapter(adapter);
    }
}

