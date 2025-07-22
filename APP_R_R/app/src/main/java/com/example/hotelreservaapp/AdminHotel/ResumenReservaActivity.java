package com.example.hotelreservaapp.AdminHotel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.AdminHotel.Model.ReservaInicio;
import com.example.hotelreservaapp.LogManager;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.AdminhotelActivityResumenreservaBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class ResumenReservaActivity extends AppCompatActivity {

    public static final String EXTRA_RESERVA = "extra_reserva";
    private static final String TAG = "FirebaseFunctionsHelper";
    private AdminhotelActivityResumenreservaBinding binding;
    private FirebaseFirestore db;
    private String uid;
    private ReservaInicio reserva;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AdminhotelActivityResumenreservaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1) Instancias básicas
        db  = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 2) Recuperar reserva
        reserva = (ReservaInicio) getIntent().getSerializableExtra(EXTRA_RESERVA);
        if (reserva == null) {
            Toast.makeText(this, "Error al cargar datos de la reserva", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // luego de binding = …
        binding.btnRefresh.setOnClickListener(v -> {
            // Opcional: mostrar un indicador de carga
            Toast.makeText(this, "Actualizando datos…", Toast.LENGTH_SHORT).show();
            refreshAll();
        });

        // 3) Botones
        binding.backBottom.setOnClickListener(v -> onBackPressed());
        binding.btnEditarPago.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditarPagoActivity.class);
            intent.putExtra(EXTRA_RESERVA, reserva);
            startActivity(intent);
        });

        binding.btnConfirmarCheckout.setOnClickListener(v -> {
            // 1) Leer los valores actuales de la UI
            String usuarioId = reserva.getIdUsuario();
            String nombre    = binding.valorNombre.getText().toString();
            db.collection("usuarios")
                    .document(reserva.getIdUsuario())
                    .collection("Reservas")
                    .document(reserva.getIdreserva())
                    .collection("PagosRealizados")
                    .document("Pago")
                    .get()
                    .addOnSuccessListener(pagoSnap -> {
                        if (!pagoSnap.exists()) return;
                        double precioHab = pagoSnap.getDouble("PrecioHabitacion");
                        double servExt   = pagoSnap.getDouble("ServiciosExtras");
                        double cargos    = pagoSnap.getDouble("CargosPorDanhos");

                        // 2) Preparar los datos del reporte
                        Map<String, Object> reporte = new HashMap<>();
                        reporte.put("usuarioId", usuarioId);
                        reporte.put("nombre", nombre);
                        reporte.put("totalGastado", precioHab + servExt + cargos);
                        reporte.put("creadoEn", FieldValue.serverTimestamp());

                        // 3) Guardar en ReporteUsuarios con ID = reservaId
                        String reservaId = reserva.getIdreserva();
                        db.collection("reporteCostosUsuario")
                                .document(reservaId)
                                .set(reporte)
                                .addOnSuccessListener(a ->
                                        Toast.makeText(this, "Reporte creado", Toast.LENGTH_SHORT).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error al crear reporte: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show()
                                );

                        //Obteniendo FCM Token

                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("usuarios").document(usuarioId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String fcmToken = documentSnapshot.getString("fcmToken");
                                        if (fcmToken != null) {
                                            Log.d("Firestore", "FCM Token del usuario: " + fcmToken);
                                            // Aquí puedes usar el token para notificación

                                            // Enviar checkout finalizado
                                            FirebaseFunctionsHelper functionsHelper = new FirebaseFunctionsHelper();
                                            //EnviarNotiTaxi
                                            functionsHelper.enviarNotificacionTaxi(fcmToken);
                                            functionsHelper.enviarNotificacionChecjout(fcmToken, new okhttp3.Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {
                                                    runOnUiThread(() -> {
                                                        Log.e(TAG, "❌ Error enviando notificación", e);
                                                        Toast.makeText(ResumenReservaActivity.this, "Error al enviar", Toast.LENGTH_SHORT).show();
                                                    });
                                                }

                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {
                                                    runOnUiThread(() -> {
                                                        if (response.isSuccessful()) {
                                                            Toast.makeText(ResumenReservaActivity.this, "✅ Notificación enviada", Toast.LENGTH_SHORT).show();

                                                            //Necesito que coloques la hora en la base de datos para que pueda ver el registro de pago, pasa la
                                                            //ID de la reserva y el ID del usuario tmb, quitar lo hardcodeado

                                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                            String horaActual = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

                                                            Map<String, Object> updates = new HashMap<>();
                                                            updates.put("CheckOutHora", horaActual);

                                                            db.collection("usuarios")
                                                                    .document(usuarioId)
                                                                    .collection("Reservas")
                                                                    .document(reserva.getIdreserva())
                                                                    .update(updates)
                                                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Reserva actualizada correctamente"))
                                                                    .addOnFailureListener(e -> Log.e("Firestore", "Error al actualizar reserva", e));
                                                            Log.d(TAG, "Notificación enviada correctamente");
                                                        } else {
                                                            Toast.makeText(ResumenReservaActivity.this, "Error al enviar: código " + response.code(), Toast.LENGTH_SHORT).show();
                                                            Log.e(TAG, "Error HTTP: " + response.code());
                                                        }
                                                    });
                                                }
                                            });

                                        } else {
                                            Log.w("Firestore", "⚠️ El campo fcmToken no está presente");
                                        }
                                    } else {
                                        Log.e("Firestore", "❌ No se encontró el documento del usuario");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "❌ Error al obtener el documento", e);
                                });

                        db.collection("usuarios").document(uid).get()
                                .addOnSuccessListener(doc -> {
                                    if (doc.exists()) {
                                        String nombre1 = doc.getString("nombre");
                                        String apellido1 = doc.getString("apellido");
                                        String nombreCompleto = nombre1 + apellido1;
                                        LogManager.registrarLogRegistro(
                                                nombreCompleto,
                                                "Checkout validado",
                                                "Se ha validado el checkout para el cliente "
                                        );                              }
                                }).addOnFailureListener(e -> Log.e("ResumenReserva", "Error cargando usuario", e));
                    }).addOnFailureListener(e -> Log.e("ResumenReserva", "Error cargando pago", e));
            finish();
        });

        // 4) Primera carga de datos
        refreshAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cada vez que vuelve a primer plano, recargar datos
        refreshAll();
    }

    /** Orquesta todas las lecturas/creaciones de Firestore */
    private void refreshAll() {
        ensureCostosDocument();
        loadImagenUsuario();
        loadDatosUsuario();
        loadDatosReserva();
        loadPago();
    }

    /** Crea el documento de costos si no existía */
    private void ensureCostosDocument() {
        String reservaId = reserva.getIdreserva();
        DocumentReference costosRef = db.collection("costos").document(reservaId);

        Log.d("ResumenReserva", "Verificando costos/" + reservaId);
        costosRef.get().addOnSuccessListener(costSnap -> {
            if (costSnap.exists()) {
                Log.d("ResumenReserva", "Ya existe costos/" + reservaId);
                return;
            }
            Log.d("ResumenReserva", "No existe costos/" + reservaId + ", creando…");

            // Obtener idHotel del usuario
            db.collection("usuarios").document(uid)
                    .get().addOnSuccessListener(userSnap -> {
                        if (!userSnap.exists()) {
                            Log.e("ResumenReserva", "Usuario adminhotel no encontrado");
                            return;
                        }
                        String idHotel = userSnap.getString("idHotel");
                        if (idHotel == null) {
                            Log.e("ResumenReserva", "Usuario sin idHotel asignado");
                            return;
                        }
                        Log.d("ResumenReserva", "idHotel=" + idHotel);

                        // Leer servicios con precio > 0
                        CollectionReference servsRef = db
                                .collection("Hoteles")
                                .document(idHotel)
                                .collection("servicios");

                        servsRef.whereGreaterThan("precio", 0)
                                .get()
                                .addOnSuccessListener(query -> {
                                    Log.d("ResumenReserva", "Servicios encontrados: " + query.size());
                                    List<Map<String,Object>> lista = new ArrayList<>();
                                    for (DocumentSnapshot s : query) {
                                        String nombre = s.getString("nombre");
                                        if (nombre != null) {
                                            Map<String,Object> item = new HashMap<>();
                                            item.put("nombre", nombre);
                                            item.put("costo", 0);
                                            lista.add(item);
                                        }
                                    }
                                    // Añadir cargo por daños al final
                                    Map<String,Object> danos = new HashMap<>();
                                    danos.put("nombre", "Costo por daños");
                                    danos.put("costo", 0);
                                    lista.add(danos);

                                    Map<String,Object> datos = new HashMap<>();
                                    datos.put("servicios", lista);
                                    datos.put("creadoEn", FieldValue.serverTimestamp());

                                    costosRef.set(datos)
                                            .addOnSuccessListener(a -> Log.d("ResumenReserva", "Costos iniciales creados"))
                                            .addOnFailureListener(e -> Log.e("ResumenReserva", "Error creando costos", e));
                                })
                                .addOnFailureListener(e -> Log.e("ResumenReserva", "Error leyendo servicios", e));
                    }).addOnFailureListener(e -> Log.e("ResumenReserva", "Error leyendo usuario", e));
        }).addOnFailureListener(e -> Log.e("ResumenReserva", "Error verificando costos", e));
    }

    /** Carga la foto del usuario que reservó */
    private void loadImagenUsuario() {
        String url = reserva.getUrlFoto();
        if (url != null && !url.isEmpty()) {
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.default_user_icon)
                    .error(R.drawable.default_user_icon)
                    .into(binding.ivProfileImage);
        } else {
            binding.ivProfileImage.setImageResource(R.drawable.default_user_icon);
        }
    }

    /** Carga datos del usuario (nombre, DNI, teléfono…) */
    private void loadDatosUsuario() {
        db.collection("usuarios")
                .document(reserva.getIdUsuario())
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;
                    binding.valorNombre.setText(doc.getString("nombre") + " " + doc.getString("apellido"));
                    binding.valorDni.setText(doc.getString("numeroDocumento"));
                    binding.valorNacimiento.setText(doc.getString("fechaNacimiento"));
                    binding.valorCorreo.setText(doc.getString("correo"));
                    binding.valorTelefono.setText(doc.getString("telefono"));
                })
                .addOnFailureListener(e -> Log.e("ResumenReserva", "Error cargando usuario", e));
    }

    /** Carga datos de la reserva en sí (fechas, personas, habitación) */
    private void loadDatosReserva() {
        db.collection("usuarios")
                .document(reserva.getIdUsuario())
                .collection("Reservas")
                .document(reserva.getIdreserva())
                .get()
                .addOnSuccessListener(resDoc -> {
                    if (!resDoc.exists()) return;
                    Date ini = resDoc.getDate("fechaIni");
                    Date fin = resDoc.getDate("fechaFin");
                    SimpleDateFormat f1 = new SimpleDateFormat("d MMM", new Locale("es","ES"));
                    SimpleDateFormat f2 = new SimpleDateFormat("d MMM yyyy", new Locale("es","ES"));
                    binding.valorFecha.setText(f1.format(ini) + " – " + f2.format(fin));
                    binding.valorHabitacion.setText(resDoc.getString("tipoHab"));
                    binding.valorPersonas.setText(resDoc.getLong("personas") + " personas");
                })
                .addOnFailureListener(e -> Log.e("ResumenReserva", "Error cargando reserva", e));
    }

    /** Carga los datos de pago y actualiza los TextViews correspondientes */
    private void loadPago() {
        db.collection("usuarios")
                .document(reserva.getIdUsuario())
                .collection("Reservas")
                .document(reserva.getIdreserva())
                .collection("PagosRealizados")
                .document("Pago")
                .get()
                .addOnSuccessListener(pagoSnap -> {
                    if (!pagoSnap.exists()) return;
                    double precioHab = pagoSnap.getDouble("PrecioHabitacion");
                    double servExt   = pagoSnap.getDouble("ServiciosExtras");
                    double cargos    = pagoSnap.getDouble("CargosPorDanhos");
                    binding.valorPrecioHabitacion.setText("S/. " + precioHab);
                    binding.valorServiciosExtras .setText("S/. " + servExt);
                    binding.valorCargos          .setText("S/. " + cargos);
                    binding.valorPrecioTotal     .setText("S/. " + (precioHab + servExt + cargos));
                })
                .addOnFailureListener(e -> Log.e("ResumenReserva", "Error cargando pago", e));
    }

    // Helper para quitar “S/. ” y parsear a double
    private double parseMonto(String texto) {
        try {
            // Remover todo lo que no sea dígito o punto
            String limpio = texto.replaceAll("[^0-9.]", "");
            return Double.parseDouble(limpio);
        } catch (Exception e) {
            return 0.0;
        }
    }


}
