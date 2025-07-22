package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

public class CreacionReserva extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_creacion_reserva);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnHistorial = findViewById(R.id.btn_historial_reservas);
        btnHistorial.setOnClickListener(v -> {
            startActivity(new Intent(this, HistorialEventos.class));
            finish();
        });

        String reservaId = getIntent().getStringExtra("reservaId");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (reservaId == null || user == null) {
            Toast.makeText(this, "Datos insuficientes para mostrar reserva", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .document(user.getUid())
                .collection("Reservas")
                .document(reservaId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Toast.makeText(this, "Reserva no encontrada", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Obtener datos
                    String estado = documentSnapshot.getString("estado");
                    Long personas = documentSnapshot.getLong("personas");
                    Timestamp fechaIni = documentSnapshot.getTimestamp("fechaIni");
                    Timestamp fechaFin = documentSnapshot.getTimestamp("fechaFin");
                    Map<String, Object> habitacion = (Map<String, Object>) documentSnapshot.get("habitacion");
                    String hotelId = documentSnapshot.getString("hotelId");

                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                    if (fechaIni != null && fechaFin != null)
                        ((TextView) findViewById(R.id.valorFecha)).setText(sdf.format(fechaIni.toDate()) + " - " + sdf.format(fechaFin.toDate()));

                    if (personas != null && habitacion != null)
                        ((TextView) findViewById(R.id.valorPersonas)).setText(personas + " Personas");

                    ((TextView) findViewById(R.id.status)).setText(estado != null ? estado : "Sin estado");

                    if (habitacion != null) {
                        String nombreHab = (String) habitacion.get("nombreHabitacion");
                        Double precioUnitario = habitacion.get("precioUnidad") instanceof Number ? ((Number) habitacion.get("precioUnidad")).doubleValue() : 0.0;
                        int cantidad = habitacion.get("cantidad") instanceof Number ? ((Number) habitacion.get("cantidad")).intValue() : 1;
                        double totalHab = precioUnitario * cantidad;

                        ((TextView) findViewById(R.id.valorHabitacion)).setText(nombreHab != null ? nombreHab : "Habitación");
                        ((TextView) findViewById(R.id.valorPrecioHabitacion)).setText("S/. " + (int) totalHab);
                        ((TextView) findViewById(R.id.valorPrecioTotal)).setText("S/. " + (int) totalHab);
                    }

                    ((TextView) findViewById(R.id.valorServiciosExtras)).setText("S/. 0");
                    ((TextView) findViewById(R.id.valorServicioTaxi)).setText("Gratis");

                    // Cargar datos del hotel
                    if (hotelId != null) {
                        db.collection("Hoteles")
                                .document(hotelId)
                                .get()
                                .addOnSuccessListener(hotelDoc -> {
                                    if (!hotelDoc.exists()) return;

                                    String nombreHotel = hotelDoc.getString("nombre");
                                    String ubicacion = hotelDoc.getString("direccion");
                                    Double valoracion = hotelDoc.getDouble("valoracion");
                                    String urlFoto = hotelDoc.getString("UrlFotoHotel");

                                    ((TextView) findViewById(R.id.nombreHotel)).setText(nombreHotel != null ? nombreHotel : "Hotel");
                                    ((TextView) findViewById(R.id.ubicacion)).setText("\uD83D\uDCCD " + (ubicacion != null ? ubicacion : "Ubicación"));
                                    ((TextView) findViewById(R.id.valoracion)).setText(valoracion != null ? String.valueOf(valoracion) : "0.0");

                                    ImageView imageHotel = findViewById(R.id.imageHotel);
                                    Glide.with(this)
                                            .load(urlFoto)
                                            .placeholder(R.drawable.hotel1)
                                            .into(imageHotel);
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar hotel", Toast.LENGTH_SHORT).show());
                    }

                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar la reserva", Toast.LENGTH_SHORT).show());
    }
}
