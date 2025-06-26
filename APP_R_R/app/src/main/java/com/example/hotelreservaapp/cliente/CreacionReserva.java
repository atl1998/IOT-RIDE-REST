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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreacionReserva extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_creacion_reserva);


        Button btnHistorial = findViewById(R.id.btn_historial_reservas);
        btnHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistorialEventos.class); // Asegúrate de que este sea el nombre correcto del Activity
            startActivity(intent);
            finish(); // Opcional: si quieres cerrar esta actividad
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String reservaId = getIntent().getStringExtra("reservaId");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (reservaId != null && user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("usuarios")
                    .document(user.getUid())
                    .collection("Reservas")
                    .document(reservaId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String estado = documentSnapshot.getString("estado");
                            Long personas = documentSnapshot.getLong("personas");
                            Timestamp fechaIni = documentSnapshot.getTimestamp("fechaIni");
                            Timestamp fechaFin = documentSnapshot.getTimestamp("fechaFin");
                            List<Map<String, Object>> habitaciones = (List<Map<String, Object>>) documentSnapshot.get("habitaciones");
                            String hotelId = documentSnapshot.getString("hotelId");

                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                            ((TextView)findViewById(R.id.valorFecha)).setText(sdf.format(fechaIni.toDate()) + " - " + sdf.format(fechaFin.toDate()));
                            ((TextView)findViewById(R.id.valorPersonas)).setText(personas + " Pers. (" + habitaciones.size() + " Hab.)");
                            ((TextView)findViewById(R.id.status)).setText(estado);

                            if (habitaciones != null && !habitaciones.isEmpty()) {
                                Map<String, Object> hab = habitaciones.get(0);
                                String nombreHab = (String) hab.get("nombreHabitacion");
                                ((TextView)findViewById(R.id.valorHabitacion)).setText(nombreHab);

                                double precioUnitario = (double) hab.get("precioUnidad");
                                int cantidad = ((Long) hab.get("cantidad")).intValue();
                                double totalHab = precioUnitario * cantidad;

                                ((TextView)findViewById(R.id.valorPrecioHabitacion)).setText("S/. " + (int)totalHab);
                                ((TextView)findViewById(R.id.valorPrecioTotal)).setText("S/. " + (int)totalHab);
                            }

                            ((TextView)findViewById(R.id.valorServiciosExtras)).setText("S/. 0");
                            ((TextView)findViewById(R.id.valorServicioTaxi)).setText("Gratis");

                            // Datos del hotel
                            if (hotelId != null) {
                                db.collection("Hoteles")
                                        .document(hotelId)
                                        .get()
                                        .addOnSuccessListener(hotelDoc -> {
                                            String nombreHotel = hotelDoc.getString("nombre");
                                            String ubicacion = hotelDoc.getString("direccion");
                                            Double valoracion = hotelDoc.getDouble("valoracion");
                                            String urlFoto = hotelDoc.getString("UrlFotoHotel");

                                            ((TextView)findViewById(R.id.nombreHotel)).setText(nombreHotel);
                                            ((TextView)findViewById(R.id.ubicacion)).setText("\uD83D\uDCCD " + ubicacion);
                                            ((TextView)findViewById(R.id.valoracion)).setText(String.valueOf(valoracion));

                                            ImageView imageHotel = findViewById(R.id.imageHotel);
                                            Glide.with(this)
                                                    .load(urlFoto)
                                                    .placeholder(R.drawable.hotel1)
                                                    .into(imageHotel);
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al cargar la reserva", Toast.LENGTH_SHORT).show()
                    );
        }
    }
}
