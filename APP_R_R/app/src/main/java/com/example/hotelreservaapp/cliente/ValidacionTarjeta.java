package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.cliente.Habitacion;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.hotelreservaapp.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidacionTarjeta extends AppCompatActivity {

    private MaterialButton btnVolver, btnConfirmarPago;

    private TextInputEditText etNumeroTarjeta,etFechaVencimiento,etCVV,etNombreTitular;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_activity_validacion_tarjeta);

        btnVolver = findViewById(R.id.btnVolver);
        btnConfirmarPago = findViewById(R.id.btnConfirmarPago);

        btnVolver.setOnClickListener(v -> {
            startActivity(new Intent(this, DetallesHotel.class));
        });
        etNumeroTarjeta = findViewById(R.id.etNumeroTarjeta);
        etCVV = findViewById(R.id.etCVV);
        etNombreTitular = findViewById(R.id.etNombreTitular);
        etFechaVencimiento = findViewById(R.id.etFechaVencimiento);

        etFechaVencimiento.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                isFormatting = true;

                String input = s.toString().replaceAll("[^\\d]", "");
                if (input.length() >= 3) {
                    String formatted = input.substring(0, 2) + "/" + input.substring(2, Math.min(4, input.length()));
                    etFechaVencimiento.setText(formatted);
                    etFechaVencimiento.setSelection(formatted.length());
                } else {
                    etFechaVencimiento.setText(input);
                    etFechaVencimiento.setSelection(input.length());
                }

                isFormatting = false;
            }
        });

        btnConfirmarPago.setOnClickListener(v -> confirmarReserva());
    }

    private void confirmarReserva() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔐 Obtener datos ingresados
        String numTarjeta = etNumeroTarjeta.getText().toString().trim();
        String fecha = etFechaVencimiento.getText().toString().trim(); // ejemplo: "11/28"
        String cvv = etCVV.getText().toString().trim();
        String titular = etNombreTitular.getText().toString().trim();

        if (numTarjeta.isEmpty() || fecha.isEmpty() || cvv.isEmpty() || titular.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 🔍 Validar tarjeta en Firestore
        db.collection("banco")
                .whereEqualTo("numTarjeta", numTarjeta)
                .whereEqualTo("CVV", cvv)
                .whereEqualTo(FieldPath.of("MM/AA"), fecha)
                .whereEqualTo("nombreTitular", titular)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // 🎯 Tarjeta válida → continuar con la reserva

                        // Continuar como en tu código actual...

                        // Recuperar datos del intent
                        Intent intent = getIntent();
                        String hotelId = intent.getStringExtra("hotelId");
                        long fechaInicioMillis = intent.getLongExtra("fechaInicio", -1);
                        long fechaFinMillis = intent.getLongExtra("fechaFin", -1);
                        int adultos = intent.getIntExtra("adultos", 0);
                        int ninos = intent.getIntExtra("ninos", 0);
                        ArrayList<Habitacion> habitacionesSeleccionadas = (ArrayList<Habitacion>) intent.getSerializableExtra("habitacionesSeleccionadas");

                        if (habitacionesSeleccionadas == null || habitacionesSeleccionadas.isEmpty()) {
                            Toast.makeText(this, "No hay habitaciones seleccionadas", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Construir lista de habitaciones y actualizar stock
                        List<Map<String, Object>> habitacionesReserva = new ArrayList<>();
                        for (Habitacion hab : habitacionesSeleccionadas) {
                            int nuevaCantidad = hab.getCantDisponible() - hab.getSeleccionadas();

                            db.collection("Hoteles")
                                    .document(hotelId)
                                    .collection("habitaciones")
                                    .document(hab.getIdDocumento())
                                    .update("cantidadDisponible", nuevaCantidad);

                            Map<String, Object> h = new HashMap<>();
                            h.put("habitacionId", hab.getIdDocumento());
                            h.put("nombreHabitacion", hab.getNombre());
                            h.put("cantidad", hab.getSeleccionadas());
                            h.put("precioUnidad", hab.getPrecio());
                            habitacionesReserva.add(h);
                        }

                        // Crear la reserva
                        Map<String, Object> reserva = new HashMap<>();
                        reserva.put("fechaIni", new Timestamp(new Date(fechaInicioMillis)));
                        reserva.put("fechaFin", new Timestamp(new Date(fechaFinMillis)));
                        reserva.put("hotelId", hotelId);
                        reserva.put("personas", adultos + ninos);
                        reserva.put("checkoutSolicitado", false);
                        reserva.put("estado", "En Progreso");
                        reserva.put("habitaciones", habitacionesReserva);
                        reserva.put("fechaReserva", FieldValue.serverTimestamp());

                        String userId = user.getUid();
                        db.collection("usuarios")
                                .document(userId)
                                .collection("Reservas")
                                .add(reserva)
                                .addOnSuccessListener(docRef -> {
                                    Toast.makeText(this, "Reserva confirmada", Toast.LENGTH_SHORT).show();
                                    Intent intent2 = new Intent(this, CreacionReserva.class);
                                    intent2.putExtra("reservaId", docRef.getId());
                                    startActivity(intent2);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firebase", "Error al guardar la reserva", e);
                                    Toast.makeText(this, "Error al registrar la reserva", Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        // ❌ Datos incorrectos
                        Toast.makeText(this, "Datos de tarjeta inválidos", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error en la validación de tarjeta", e);
                    Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show();
                });
    }

}
