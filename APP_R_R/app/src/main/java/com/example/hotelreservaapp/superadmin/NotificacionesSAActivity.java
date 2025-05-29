package com.example.hotelreservaapp.superadmin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.SuperadminNotificacionesBinding;
import com.example.hotelreservaapp.model.Notificacion;
import com.example.hotelreservaapp.room.AppDatabase;
import com.google.android.material.button.MaterialButton;

public class NotificacionesSAActivity extends AppCompatActivity {

    private SuperadminNotificacionesBinding binding;
    private NotificacionSAAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Usa solo ViewBinding
        binding = SuperadminNotificacionesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ✅ Configura RecyclerView
        binding.recyclerNotificaciones.setLayoutManager(new LinearLayoutManager(this));

        binding.backButton.setOnClickListener(v -> finish());

        // ✅ Carga las notificaciones desde Room
        List<Notificacion> lista = AppDatabase.getInstance(this).notificacionDao().obtenerTodas();

        adapter = new NotificacionSAAdapter(lista, this);
        binding.recyclerNotificaciones.setAdapter(adapter);
    }
}

