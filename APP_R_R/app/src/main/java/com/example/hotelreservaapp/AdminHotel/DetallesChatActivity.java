package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.AdminHotel.Fragments.ChatFragment;
import com.example.hotelreservaapp.R;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.cliente.ClienteChat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

public class DetallesChatActivity extends AppCompatActivity {

    private LinearLayout messagesContainer;
    private EditText messageEditText;
    private FloatingActionButton sendButton;
    private ScrollView scrollView;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private MaterialButton btnVolver;
    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminhotel_activity_detalles_chat);

        messagesContainer = findViewById(R.id.messagesContainer);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        scrollView = findViewById(R.id.scrollView);

        btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> {
            finish(); // ← Esto te devuelve al ChatFragment
        });

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "No has iniciado sesión", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Obtener el chatId desde el intent
        chatId = getIntent().getStringExtra("chatId");
        if (chatId == null) {
            Toast.makeText(this, "Error: chat no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Listener en tiempo real
        db.collection("chats")
                .document(chatId)
                .collection("mensajes")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("CHAT", "Error leyendo mensajes", e);
                        return;
                    }

                    messagesContainer.removeAllViews(); // limpiar vista
                    for (QueryDocumentSnapshot doc : snapshots) {
                        String contenido = doc.getString("contenido");
                        String remitenteId = doc.getString("remitenteId");

                        boolean esMio = currentUser.getUid().equals(remitenteId);
                        mostrarMensaje(contenido, esMio);
                    }

                    scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                });

        // Botón enviar
        sendButton.setOnClickListener(v -> {
            String texto = messageEditText.getText().toString().trim();
            if (texto.isEmpty()) return;

            Map<String, Object> mensaje = new HashMap<>();
            mensaje.put("contenido", texto);
            mensaje.put("remitenteId", currentUser.getUid());
            mensaje.put("timestamp", FieldValue.serverTimestamp());

            db.collection("chats")
                    .document(chatId)
                    .collection("mensajes")
                    .add(mensaje)
                    .addOnSuccessListener(doc -> {
                        messageEditText.setText("");
                    });
        });
    }

    private void mostrarMensaje(String texto, boolean esMio) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(15f);
        textView.setMaxWidth(800);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        if (esMio) {
            textView.setBackgroundResource(R.drawable.sent_message_background);
            textView.setTextColor(Color.WHITE);
            params.gravity = Gravity.END;
        } else {
            textView.setBackgroundResource(R.drawable.received_message_background);
            textView.setTextColor(Color.BLACK);
            params.gravity = Gravity.START;
        }

        textView.setLayoutParams(params);
        messagesContainer.addView(textView);
    }
}