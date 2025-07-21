package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClienteChat extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Chat> mensajeList;
    private List<Chat> mensajeListFull; // Para guardar una copia de la lista completa

    private MaterialButton btnNotificaciones;

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cliente_chat);

        bottomNav = findViewById(R.id.bottonNavigationView);
        configurarBottomNav();

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String currentUserId = currentUser.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mensajeList = new ArrayList<>();
        mensajeListFull = new ArrayList<>();
        adapter = new ChatAdapter(mensajeList);
        recyclerView.setAdapter(adapter);

        // Cargar chats donde el usuario participa
        db.collection("chats")
                .whereArrayContains("participantes", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mensajeList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String chatId = doc.getId();
                        List<String> participantes = (List<String>) doc.get("participantes");

                        // Obtener el ID del otro participante
                        String otroUsuarioId = participantes.stream()
                                .filter(id -> !id.equals(currentUserId))
                                .findFirst()
                                .orElse("Desconocido");

                        // Nombre temporal por defecto
                        String nombreChat = "Chat con otro usuario";

                        // Inicializar chat con mensaje vacío, lo actualizaremos después
                        Chat chat = new Chat(nombreChat, "", "Cargando...", false, false, false);
                        chat.setChatId(chatId);
                        mensajeList.add(chat);

                        // Obtener el último mensaje de ese chat
                        db.collection("chats")
                                .document(chatId)
                                .collection("mensajes")
                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(msnSnapshot -> {
                                    if (!msnSnapshot.isEmpty()) {
                                        DocumentSnapshot ultimoMensajeDoc = msnSnapshot.getDocuments().get(0);
                                        String texto = ultimoMensajeDoc.getString("contenido");
                                        Timestamp timestamp = ultimoMensajeDoc.getTimestamp("timestamp");
                                        String hora = obtenerHoraFormateada(timestamp);
                                        String remitenteId = ultimoMensajeDoc.getString("remitenteId");

                                        chat.setUltimoMensaje(texto);
                                        chat.setHoraMensaje(hora);
                                        chat.setEnviadoPorMi(currentUserId.equals(remitenteId));
                                    }

                                    adapter.notifyDataSetChanged();
                                });
                    }


                    mensajeListFull = new ArrayList<>(mensajeList);

                    // Ordenar chats (si lo deseas, por ejemplo: no leídos primero)
                    Collections.sort(mensajeList, (c1, c2) -> Boolean.compare(c1.isLeidoPorMi(), c2.isLeidoPorMi()));

                    adapter.updateList(mensajeList);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar chats", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error en consulta de chats", e);
                });


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

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.chat_cliente);
    }
    private void configurarBottomNav() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.chat_cliente) {
                return true; // Ya estás en esta pantalla
            } else if (id == R.id.inicioCliente) {
                startActivity(new Intent(this, HomeCliente.class));
            } else if (id == R.id.historialCliente) {
                startActivity(new Intent(this, HistorialEventos.class));
            } else if (id == R.id.perfilCliente) {
                startActivity(new Intent(this, PerfilCliente.class));
            }
            return true;
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

    private String obtenerHoraFormateada(Timestamp timestamp) {
        if (timestamp == null) return "";
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(date);
    }
}