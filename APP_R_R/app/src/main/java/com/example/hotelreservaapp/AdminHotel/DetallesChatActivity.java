package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.cliente.ClienteChat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
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
    private TextView nombreSoporte,nombreHotel,direccionHotel, precioHotel;
    private ImageView imagenHotel,profilImage;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String chatId,hotelId,adminId;
    private MaterialButton btnVolver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_activity_detalle_chat);

        messagesContainer = findViewById(R.id.messagesContainer);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        scrollView = findViewById(R.id.scrollView);


        nombreHotel = findViewById(R.id.hotelName);
        direccionHotel = findViewById(R.id.hotelLocation);
        precioHotel = findViewById(R.id.hotelPrice);
        imagenHotel = findViewById(R.id.hotelImage);
        profilImage=findViewById(R.id.profileImage);

        // Botón para volver
        btnVolver = findViewById(R.id.btnVolverChatCliente);
        btnVolver.setOnClickListener(v -> {
            startActivity(new Intent(this, ClienteChat.class));
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

        db.collection("chats")
                .document(chatId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        adminId = document.getString("idUsuario");
                        hotelId = document.getString("idHotel");
                        Log.d("DETALLE_CHAT", "ID del hotel extraído del chat: " + hotelId);

                        if (hotelId != null && !hotelId.isEmpty()) {
                            cargarDatosHotel(hotelId);
                        }

                        // ✅ Solo ahora que ya tienes adminId, haz esta consulta
                        if (adminId != null) {
                            db.collection("usuarios")
                                    .document(adminId)
                                    .get()
                                    .addOnSuccessListener(docu -> {
                                        if (docu.exists()) {
                                            String nombreAdmin = docu.getString("nombre");
                                            String fotoAdmin = docu.getString("urlFotoPerfil");
                                            nombreSoporte = findViewById(R.id.contactName);
                                            nombreSoporte.setText(nombreAdmin != null ? nombreAdmin : "Desconocido");
                                            if (fotoAdmin != null && !fotoAdmin.isEmpty()) {
                                                Glide.with(this).load(fotoAdmin).into(profilImage);
                                            }
                                        } else {
                                            Log.e("DETALLE_CHAT", "Documento del admin no existe");
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("DETALLE_CHAT", "Error al obtener admin", e));
                        } else {
                            Log.e("DETALLE_CHAT", "adminId es null");
                        }
                    } else {
                        Log.e("DETALLE_CHAT", "Documento de chat no existe");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DETALLE_CHAT", "Error al obtener documento del chat", e);
                });

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
                        Timestamp timestamp = doc.getTimestamp("timestamp");

                        boolean esMio = currentUser.getUid().equals(remitenteId);
                        mostrarMensaje(contenido, timestamp, esMio);
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

    private void mostrarMensaje(String texto, Timestamp timestamp, boolean esMio) {
        LinearLayout mensajeLayout = new LinearLayout(this);
        mensajeLayout.setOrientation(LinearLayout.VERTICAL);
        mensajeLayout.setPadding(10, 0, 10, 5); // Más espacio alrededor

        TextView mensajeTextView = new TextView(this);
        mensajeTextView.setText(texto);
        mensajeTextView.setTextSize(15f); // Texto más grande
        mensajeTextView.setPadding(30, 20, 30, 20); // Más espacio dentro del cuadro
        mensajeTextView.setMaxWidth(1000); // Permite más ancho

        TextView horaTextView = new TextView(this);
        horaTextView.setTextSize(10f); // Hora también un poco más grande
        horaTextView.setTextColor(Color.DKGRAY);
        horaTextView.setPadding(10, 0, 10, 5);

        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String horaFormateada = sdf.format(timestamp.toDate());
            horaTextView.setText(horaFormateada);
        } else {
            horaTextView.setText("...");
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 10); // Espacio entre mensajes

        if (esMio) {
            mensajeTextView.setBackgroundResource(R.drawable.sent_message_background);
            mensajeTextView.setTextColor(Color.WHITE);
            params.gravity = Gravity.END;
        } else {
            mensajeTextView.setBackgroundResource(R.drawable.received_message_background);
            mensajeTextView.setTextColor(Color.BLACK);
            params.gravity = Gravity.START;
        }

        mensajeTextView.setLayoutParams(params);
        horaTextView.setLayoutParams(params);
        mensajeLayout.setLayoutParams(params);

        mensajeLayout.addView(mensajeTextView);
        mensajeLayout.addView(horaTextView);

        messagesContainer.addView(mensajeLayout);
    }



    private void cargarDatosHotel(String hotelId) {
        db.collection("Hoteles").document(hotelId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String nombre = document.getString("nombre");
                        String departamento = document.getString("departamento");
                        Double precioMin = document.getDouble("precioMin");
                        String urlFoto = document.getString("UrlFotoHotel");

                        nombreHotel.setText(nombre != null ? nombre : "Sin nombre");
                        direccionHotel.setText(departamento != null ? departamento : "Sin ubicación");
                        precioHotel.setText(precioMin != null ? "S/ " + precioMin +"/noche": "S/ -");

                        // Cargar imagen si corresponde
                        if (urlFoto != null && !urlFoto.isEmpty()) {
                            Glide.with(this).load(urlFoto).into(imagenHotel);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al cargar hotel", e));
    }


}
