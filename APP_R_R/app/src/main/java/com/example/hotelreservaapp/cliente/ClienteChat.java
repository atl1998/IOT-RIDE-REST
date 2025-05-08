package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClienteChat extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Chat> mensajeList;
    private List<Chat> mensajeListFull; // Para guardar una copia de la lista completa


    private MaterialButton btnNotificaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cliente_chat);

        BottomNavigationView bottomNav = findViewById(R.id.bottonNavigationView);
        bottomNav.setSelectedItemId(R.id.chat_cliente);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.inicioCliente) {
                startActivity(new Intent(this, HomeCliente.class));
            } else if (id == R.id.chat_cliente) {
                startActivity(new Intent(this, ClienteChat.class));
            } else if (id == R.id.historialCliente) {
                startActivity(new Intent(this, HistorialEventos.class));
            } else if (id == R.id.perfilCliente) {
                startActivity(new Intent(this, PerfilCliente.class));
            }

            return true;
        });

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lista de mensajes (se llenan 20 chats simulados)
        mensajeList = new ArrayList<>();
        mensajeList.add(new Chat("Hotel Riviera", "10:15", "Yo: ¿Está incluida la cena?", false, true, true));
        mensajeList.add(new Chat("Hotel Montaña", "10:20", "El check-in es a partir de las 2pm.", true, false, false));
        mensajeList.add(new Chat("Hotel Paraíso", "10:22", "Yo: ¿A qué hora puedo hacer el check-out?", false, true, true));
        mensajeList.add(new Chat("Hotel Estrella", "10:25", "Yo: Ya estoy en la recepción", false, true, true));
        mensajeList.add(new Chat("Hotel Oasis", "10:30", "Su habitación ya está lista.", true, false, false));
        mensajeList.add(new Chat("Hotel Sol y Luna", "10:34", "Yo: ¿Hay servicio a la habitación?", false, true, true));
        mensajeList.add(new Chat("Hotel La Costa", "10:40", "El spa está abierto hasta las 8pm.", true, false, true));
        mensajeList.add(new Chat("Hotel Azul", "10:42", "Yo: Necesito toallas adicionales", false, true, true));
        mensajeList.add(new Chat("Hotel Primavera", "10:45", "Claro, en un momento se las llevamos.", true, false, true));
        mensajeList.add(new Chat("Hotel Mar Azul", "10:47", "Yo: ¿Puedo hacer late check-out?", false, true, true));
        mensajeList.add(new Chat("Hotel Luna", "10:50", "Está confirmado su desayuno a las 9.", true, false, true));
        mensajeList.add(new Chat("Hotel Bella Vista", "10:52", "Yo: Perfecto, gracias", false, true, true));
        mensajeList.add(new Chat("Hotel El Paraíso", "10:55", "El Wi-Fi es gratuito en todo el hotel.", true, false, true));
        mensajeList.add(new Chat("Hotel Sur", "10:58", "Yo: ¿Dónde está el parqueadero?", false, true, true));
        mensajeList.add(new Chat("Hotel Río", "11:00", "Está al fondo a la derecha.", true, false, true));
        mensajeList.add(new Chat("Hotel Azul Marino", "11:02", "Yo: Llego en 15 minutos", false, true, true));
        mensajeList.add(new Chat("Hotel Montemar", "11:05", "Lo esperamos con gusto.", true, false, true));
        mensajeList.add(new Chat("Hotel Las Palmas", "11:08", "Yo: ¿Desea agregar un servicio extra?", false, true, true));
        mensajeList.add(new Chat("Hotel El Dorado", "11:10", "Yo: Sí, una botella de vino por favor", false, true, true));
        mensajeList.add(new Chat("Hotel Mirador", "11:12", "Enseguida se la llevamos.", true, false, true));

        // Crear una copia de la lista original para realizar el filtrado
        mensajeListFull = new ArrayList<>(mensajeList);

        // Ordenar: los no leídos primero
        Collections.sort(mensajeList, (c1, c2) -> Boolean.compare(c1.isLeidoPorMi(), c2.isLeidoPorMi()));

        // Crear y configurar el adaptador
        adapter = new ChatAdapter(mensajeList);
        recyclerView.setAdapter(adapter);

        // Configurar la barra de búsqueda
        EditText searchInput = findViewById(R.id.search_input);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // No se necesita implementar nada aquí
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Filtrar los mensajes mientras el usuario escribe
                filterMessages(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No se necesita implementar nada aquí
            }
        });



        btnNotificaciones = findViewById(R.id.notificaciones_cliente);
        btnNotificaciones.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            Intent intent = new Intent(this, ClienteNotificaciones.class);
            startActivity(intent);
        });
    }
    // Método para filtrar los mensajes por nombre del hotel
    private void filterMessages(String query) {
        List<Chat> filteredList = new ArrayList<>();

        // Si la búsqueda está vacía, muestra todos los mensajes
        if (query.isEmpty()) {
            filteredList.addAll(mensajeListFull);
        } else {
            // Filtrar los mensajes que contienen la búsqueda en el nombre del hotel
            for (Chat chat : mensajeListFull) {
                if (chat.getnombreHotel().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(chat);
                }
            }
        }

        // Actualizar el adaptador con la lista filtrada
        adapter.updateList(filteredList);
    }
}