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

public class DetalleChat extends AppCompatActivity {

    private MaterialButton btnVolver;
    private EditText messageEditText;
    private FloatingActionButton sendButton;
    private LinearLayout messagesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_activity_detalle_chat);

        btnVolver = findViewById(R.id.btnVolverChatCliente);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        messagesContainer = findViewById(R.id.messagesContainer);

        btnVolver.setOnClickListener(v -> {
            startActivity(new Intent(this, ClienteChat.class));
        });

        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                addSentMessage(message);
                messageEditText.setText(""); // limpiar campo
            }
        });
    }

    private void addSentMessage(String message) {
        View sentMessageView = LayoutInflater.from(this).inflate(R.layout.item_cliente_message_sent, messagesContainer, false);

        TextView messageText = sentMessageView.findViewById(R.id.text_message);
        TextView messageTime = sentMessageView.findViewById(R.id.text_time);

        messageText.setText(message);
        messageTime.setText(obtenerHoraActual());

        messagesContainer.addView(sentMessageView);
    }

    private String obtenerHoraActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("America/Lima"));
        return sdf.format(new Date());
    }
}
