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
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

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
            finish();
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

        String userId = user.getUid();

        // 🔍 Validar tarjeta en Firestore
        db.collection("banco")
                .whereEqualTo("numTarjeta", numTarjeta)
                .whereEqualTo("CVV", cvv)
                .whereEqualTo(FieldPath.of("MM/AA"), fecha)
                .whereEqualTo("nombreTitular", titular)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {


                    Intent intent1 = getIntent();
                    String hotelId1 = intent1.getStringExtra("hotelId");
                    db.collection("Hoteles")
                            .document(hotelId1)
                            .get()
                            .addOnSuccessListener(hotelDoc -> {
                                if (hotelDoc.exists()) {
                                    String adminId = hotelDoc.getString("idAdminHotel"); // 👈 asegúrate de que esté bien escrito en Firestore

                                    if (adminId != null && !adminId.isEmpty()) {
                                        Map<String, Object> chatData = new HashMap<>();
                                        chatData.put("idUsuario", userId);
                                        chatData.put("idAdminHotel", adminId);
                                        chatData.put("idHotel",hotelId1);

                                        db.collection("chats")
                                                .add(chatData)
                                                .addOnSuccessListener(chatRef -> {
                                                    Log.d("Firestore", "Chat creado con ID: " + chatRef.getId());

                                                    // ✅ Crear primer mensaje automático
                                                    Map<String, Object> primerMensaje = new HashMap<>();
                                                    primerMensaje.put("contenido", "Hola, he realizado una reserva. ¿Podrías ayudarme con más información?");
                                                    primerMensaje.put("remitenteId", userId);
                                                    primerMensaje.put("timestamp", FieldValue.serverTimestamp());

                                                    db.collection("chats")
                                                            .document(chatRef.getId())
                                                            .collection("mensajes")
                                                            .add(primerMensaje)
                                                            .addOnSuccessListener(msgRef -> {
                                                                Log.d("Firestore", "Mensaje inicial enviado");
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Log.e("Firestore", "Error al enviar mensaje inicial", e);
                                                            });
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("Firestore", "Error creando chat", e);
                                                });
                                    } else {
                                        Log.w("Firestore", "El hotel no tiene un adminId válido");
                                    }
                                } else {
                                    Log.w("Firestore", "Hotel no encontrado");
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "No se pudo obtener hotel", e);
                            });


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
                        Habitacion habitacionSeleccionada = (Habitacion) intent.getSerializableExtra("habitacionSeleccionada");

                        if (habitacionSeleccionada == null) {
                            Toast.makeText(this, "No hay habitaciones seleccionadas", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int nuevaCantidad = habitacionSeleccionada.getCantDisponible() - habitacionSeleccionada.getSeleccionadas();

                        db.collection("Hoteles")
                                .document(hotelId)
                                .collection("habitaciones")
                                .document(habitacionSeleccionada.getIdDocumento())
                                .update("cantidadDisponible", nuevaCantidad);

                        List<Map<String, Object>> habitacionesReserva = new ArrayList<>();

                        Map<String, Object> h = new HashMap<>();
                        h.put("habitacionId", habitacionSeleccionada.getIdDocumento());
                        h.put("nombreHabitacion", habitacionSeleccionada.getNombre());
                        h.put("cantidad", habitacionSeleccionada.getSeleccionadas());
                        h.put("precioUnidad", habitacionSeleccionada.getPrecio());

                        habitacionesReserva.add(h);


                        // 🟢 AQUÍ CALCULAS el total del pago:
                        double pagoTotal = habitacionSeleccionada.getSeleccionadas() * habitacionSeleccionada.getPrecio();


                        // Crear la reserva
                        Map<String, Object> reserva = new HashMap<>();
                        reserva.put("fechaIni", new Timestamp(new Date(fechaInicioMillis)));
                        reserva.put("fechaFin", new Timestamp(new Date(fechaFinMillis)));
                        reserva.put("hotelId", hotelId);
                        reserva.put("personas", adultos + ninos);
                        reserva.put("tipoHab",habitacionSeleccionada.getNombre());

                        Map<String, Object> habitacionMap = new HashMap<>();
                        habitacionMap.put("nombreHabitacion", habitacionSeleccionada.getNombre());
                        habitacionMap.put("precioUnidad", habitacionSeleccionada.getPrecio());
                        habitacionMap.put("cantidad", habitacionSeleccionada.getSeleccionadas());

                        reserva.put("habitacion", habitacionMap);

                        reserva.put("estado", "En Progreso"); // o "En Progreso" si aún no ha concluido
                        reserva.put("CheckInHora", "No especificado");
                        reserva.put("CheckOutHora", "No especificado"); // Método auxiliar abajo
                        reserva.put("checkoutSolicitado", false); // si aplica
                        reserva.put("solicitarTaxista", "No Solicitado");


                        db.collection("usuarios")
                                .document(userId)
                                .collection("Reservas")
                                .add(reserva)
                                .addOnSuccessListener(docRef -> {
                                    Toast.makeText(this, "Reserva confirmada", Toast.LENGTH_SHORT).show();


                                    Map<String, Object> reservaSimplificadav2 = new HashMap<>();
                                    reservaSimplificadav2.put("fechainiciocheckin", new Timestamp(new Date(fechaInicioMillis)));
                                    reservaSimplificadav2.put("idreserva", docRef.getId()); // Puedes usar docRef.getId() si deseas

                                    Map<String, Object> pago = new HashMap<>();
                                    pago.put("PrecioHabitacion", pagoTotal);
                                    pago.put("CargosPorDanhos", 0); // si hay lógica para calcularlo, cámbialo
                                    pago.put("ServiciosExtras", 0); // si corresponde
                                    pago.put("PrecioTotal", pagoTotal); // sumar otros campos si aplica

                                    db.collection("usuarios")
                                            .document(userId)
                                            .collection("Reservas")
                                            .document(docRef.getId())
                                            .collection("PagosRealizados")
                                            .document("Pago")
                                            .set(pago)
                                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Pago guardado"))
                                            .addOnFailureListener(e -> Log.e("Firestore", "Error guardando pago", e));


                                    reservaSimplificadav2.put("idusuario", userId);
                                    reservaSimplificadav2.put("pagohabitacion", pagoTotal);

                                    db.collection("reservaas")
                                            .document(hotelId)
                                            .get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                List<Map<String, Object>> listaExistente;

                                                if (documentSnapshot.exists() && documentSnapshot.contains("listareservas")) {
                                                    // Ya hay una lista, la obtenemos y le agregamos la nueva reserva
                                                    listaExistente = (List<Map<String, Object>>) documentSnapshot.get("listareservas");
                                                } else {
                                                    // No hay lista, creamos una nueva
                                                    listaExistente = new ArrayList<>();
                                                }

                                                listaExistente.add(reservaSimplificadav2);

                                                // Subimos la lista actualizada
                                                Map<String, Object> dataFinal = new HashMap<>();
                                                dataFinal.put("listareservas", listaExistente);

                                                db.collection("reservaas")
                                                        .document(hotelId)
                                                        .set(dataFinal)
                                                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Reserva agregada como índice " + (listaExistente.size() - 1)))
                                                        .addOnFailureListener(e -> Log.e("Firestore", "Error guardando la reserva", e));
                                            })
                                            .addOnFailureListener(e -> Log.e("Firestore", "Error obteniendo el documento", e));

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
