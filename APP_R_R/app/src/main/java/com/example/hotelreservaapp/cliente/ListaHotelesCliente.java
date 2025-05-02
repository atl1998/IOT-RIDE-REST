package com.example.hotelreservaapp.cliente;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class ListaHotelesCliente extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Hotel> listaHoteles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_activity_lista_hoteles); // tu layout principal

        recyclerView = findViewById(R.id.recyclerView); // asegúrate que exista en tu XML
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaHoteles = new ArrayList<>();
        llenarListaHoteles(); // método que crea datos de ejemplo

        HotelAdapter adapter = new HotelAdapter(this, listaHoteles);
        recyclerView.setAdapter(adapter);


    }

    private void llenarListaHoteles() {
        listaHoteles.add(new Hotel("Hotel Lima", 4.5f, "9.1 Excelente - 200 opiniones", "Centro de Lima", "28 abr al 2 may", "S/350", R.drawable.hotel1));
        listaHoteles.add(new Hotel("Hotel Cusco", 4.0f, "8.8 Fabuloso - 150 opiniones", "Cusco Histórico", "1 may al 5 may", "S/290", R.drawable.hotel2));
        // agrega más hoteles
    }
}