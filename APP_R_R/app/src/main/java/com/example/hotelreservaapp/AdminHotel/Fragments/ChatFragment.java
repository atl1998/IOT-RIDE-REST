package com.example.hotelreservaapp.AdminHotel.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hotelreservaapp.AdminHotel.Adapter.ChatAdapter;
import com.example.hotelreservaapp.AdminHotel.Model.Chat;
import com.example.hotelreservaapp.R;
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

public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Chat> mensajeList;
    private List<Chat> mensajeListFull;
    private MaterialButton btnNotificaciones;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adminhotel_fragment_chat, container, false); // usa un layout diferente

        recyclerView = view.findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        /*btnNotificaciones = view.findViewById(R.id.notificaciones_cliente);

        btnNotificaciones.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ClienteNotificaciones.class);
            startActivity(intent);
        });*/

        mensajeList = new ArrayList<>();
        mensajeListFull = new ArrayList<>();
        adapter = new ChatAdapter(mensajeList);
        recyclerView.setAdapter(adapter);

        cargarChats();

        // Search
        EditText searchInput = view.findViewById(R.id.search_input);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMessages(s.toString());
            }
        });

        return view;
    }

    private void cargarChats() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("chats")
                .whereEqualTo("idAdminHotel", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mensajeList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String chatId = doc.getId();
                        String idUsuario = doc.getString("idUsuario");

                        // Nombre temporal mientras obtenemos el nombre del cliente
                        Chat chat = new Chat("Cliente desconocido", "", "Cargando...", false, false, false);
                        chat.setChatId(chatId);
                        mensajeList.add(chat);

                        // 🔍 Obtener nombre del cliente desde la colección 'usuarios'
                        db.collection("usuarios")
                                .document(idUsuario)
                                .get()
                                .addOnSuccessListener(usuarioDoc -> {
                                    if (usuarioDoc.exists()) {
                                        String nombreUsuario = usuarioDoc.getString("nombre");
                                        if (nombreUsuario != null) {
                                            chat.setNombreHotel(nombreUsuario); // ✅ Usamos el mismo campo del adapter
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });

                        // 🔁 Obtener el último mensaje
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
                    Collections.sort(mensajeList, (c1, c2) -> Boolean.compare(c1.isLeidoPorMi(), c2.isLeidoPorMi()));
                    adapter.updateList(mensajeList);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al cargar chats", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error al obtener chats del admin hotel", e);
                });
    }


    private void filterMessages(String query) {
        List<Chat> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(mensajeListFull);
        } else {
            for (Chat chat : mensajeListFull) {
                if (chat.getnombreHotel().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(chat);
                }
            }
        }
        adapter.updateList(filteredList);
    }

    private String obtenerHoraFormateada(Timestamp timestamp) {
        if (timestamp == null) return "";
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(date);
    }
}
