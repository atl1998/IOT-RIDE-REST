package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

public class DetalleChat extends AppCompatActivity {

    private MaterialButton btnVolver;
    private EditText messageEditText;
    private FloatingActionButton sendButton;
    private LinearLayout messagesContainer;

    private static final String PREF_NAME = "chat_prefs";
    private static final String KEY_MESSAGES = "mensajes_chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_activity_detalle_chat);

        btnVolver = findViewById(R.id.btnVolverChatCliente);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        messagesContainer = findViewById(R.id.messagesContainer);

        btnVolver.setOnClickListener(v -> startActivity(new Intent(this, ClienteChat.class)));

        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                addSentMessage(message);
                saveMessageToLocalStorage(message);
                messageEditText.setText(""); // limpiar campo
            }
        });

        loadMessagesFromLocalStorage(); // 🔄 cargar al iniciar
    }

    private void addSentMessage(String message) {
        View sentMessageView = LayoutInflater.from(this)
                .inflate(R.layout.item_cliente_message_sent, messagesContainer, false);

        TextView messageText = sentMessageView.findViewById(R.id.text_message);
        TextView messageTime = sentMessageView.findViewById(R.id.text_time);

        messageText.setText(message);
        messageTime.setText(obtenerHoraActual());

        messagesContainer.addView(sentMessageView);
        scrollToBottom();
    }

    private String obtenerHoraActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("America/Lima"));
        return sdf.format(new Date());
    }

    private void saveMessageToLocalStorage(String message) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String oldMessages = prefs.getString(KEY_MESSAGES, "[]");

        try {
            JSONArray jsonArray = new JSONArray(oldMessages);
            jsonArray.put(message); // Agregar nuevo mensaje
            prefs.edit().putString(KEY_MESSAGES, jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadMessagesFromLocalStorage() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String savedMessages = prefs.getString(KEY_MESSAGES, "[]");

        try {
            JSONArray jsonArray = new JSONArray(savedMessages);
            for (int i = 0; i < jsonArray.length(); i++) {
                String mensaje = jsonArray.getString(i);
                addSentMessage(mensaje);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void scrollToBottom() {
        final View scrollView = findViewById(R.id.scrollView); // Asegúrate de que este ID sea correcto
        scrollView.post(() -> scrollView.scrollTo(0, messagesContainer.getBottom()));
    }
}
