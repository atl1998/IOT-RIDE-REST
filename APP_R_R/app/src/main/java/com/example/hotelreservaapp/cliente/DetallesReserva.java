package com.example.hotelreservaapp.cliente;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.Objetos.Notificaciones;
import com.example.hotelreservaapp.Objetos.NotificacionesStorageHelper;
import com.example.hotelreservaapp.Objetos.NotificationManagerNoAPP;
import com.example.hotelreservaapp.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DetallesReserva extends AppCompatActivity {
    private TextView definirHoraLlegada, HoraDeSalida, nombreHotel, status, valoracion,
            ubicacion, valorFecha, valorPersonas, valorHabitacion, valorContacto, fechaCheckIn, fechaCheckOut;
    private Boolean horaDefinida;
    private Button btnServicio;
    private HistorialItem historialItem;
    ImageView imageHotel;
    private String idReserva;
    private String userId;

    private String Tipo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_detalles_reserva);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Inicialización de vistas
        valorFecha = findViewById(R.id.valorFecha);
        valorPersonas = findViewById(R.id.valorPersonas);
        nombreHotel = findViewById(R.id.nombreHotel);
        status = findViewById(R.id.status);
        valoracion = findViewById(R.id.valoracion);
        ubicacion = findViewById(R.id.ubicacion);
        valorHabitacion = findViewById(R.id.valorHabitacion);
        valorContacto = findViewById(R.id.valorContacto);
        fechaCheckIn = findViewById(R.id.fechaCheckIn);
        fechaCheckOut = findViewById(R.id.fechaCheckOut);
        imageHotel = findViewById(R.id.imageHotel);
        HoraDeSalida = findViewById(R.id.HoraDeSalida);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            userId = currentUser.getUid();
        }

        idReserva = getIntent().getStringExtra("idReserva");
        cargarReserva(idReserva);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonNavigationView);
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true); // Desactiva el estado de selección
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.inicioCliente) {
                startActivity(new Intent(this, HomeCliente.class));
                return true;
            } else if (id == R.id.chat_cliente) {
                startActivity(new Intent(this, ClienteChat.class));
                return true;
            } else if (id == R.id.historialCliente) {
                startActivity(new Intent(this, HistorialEventos.class));
                return true;
            } else if (id == R.id.perfilCliente) {
                startActivity(new Intent(this, PerfilCliente.class));
                return true;
            }
            return false;
        });
        btnServicio = findViewById(R.id.btnProcesarPago);

        btnServicio.setEnabled(false);
        btnServicio.setAlpha(0.5f);


        // 1. Cargar lista guardada (si existe)
        NotificacionesStorageHelper storageHelper = new NotificacionesStorageHelper(this);
        Notificaciones[] notificacionesGuardadas = storageHelper.leerArchivoNotificacionesDesdeSubcarpeta();

        // 2. Crear o cargar NotificationManager con la lista actual
        NotificationManagerNoAPP notificationManagerNoAPP = new NotificationManagerNoAPP();
        if (notificacionesGuardadas != null && notificacionesGuardadas.length > 0) {
            for (Notificaciones n : notificacionesGuardadas) {
                Tipo = n.getTipo();
                notificationManagerNoAPP.getListaNotificaciones().add(n);
                /*
                if ("02".equals(n.getTipo().trim())) {
                    String fechaBonita = n.getFechaBonita(); // Tu valor long de fecha
                    String hora = n.getHoraBonita();     // Ej: "14:30"
                    HoraDeSalida.setText("Finalizado a las " + hora);
                }
                */
            }
        }

        MaterialButton btnNotificaciones = findViewById(R.id.notificaciones_cliente);
        btnNotificaciones.setOnClickListener(v -> {
            Intent intent = new Intent(this, ClienteNotificaciones.class);
            startActivity(intent);
        });

        // Inicializamos el TextView y la variable para saber si la hora ya fue definida
        definirHoraLlegada = findViewById(R.id.definirHoraLlegada);
        horaDefinida = false;

        // Recuperar la hora guardada de SharedPreferences

        /*
        SharedPreferences sharedPreferences = getSharedPreferences("ReservaPrefs", MODE_PRIVATE);
        //String horaGuardada = sharedPreferences.getString("horaLlegada", null);
        Boolean SolicitarCheckout = sharedPreferences.getBoolean("SolicitarCheckout", false);

        if(SolicitarCheckout){
            btnServicio.setEnabled(true);
            btnServicio.setAlpha(1f);
        }
        */


        /*
        if (horaGuardada != null) {
            // Si hay una hora guardada, mostrarla y deshabilitar el TextView
            definirHoraLlegada.setText("Hora de llegada: " + horaGuardada);
            horaDefinida = true;
            definirHoraLlegada.setClickable(false);
            definirHoraLlegada.setTextColor(Color.parseColor("#646464"));
        }
         */

        definirHoraLlegada.setOnClickListener(v -> {
            if (!horaDefinida) {
                // Crear un TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        (view, hourOfDay, minute) -> {
                            // Validar que la hora esté dentro del rango permitido
                            if (hourOfDay >= 2 && hourOfDay <= 4) {
                                // Validar minutos entre 0 y 59
                                if (minute >= 0 && minute <= 59) {
                                    // Mostrar la hora seleccionada
                                    String horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute);
                                    definirHoraLlegada.setText("Hora de llegada: " + horaSeleccionada);
                                    /*
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("horaLlegada", horaSeleccionada); // Guardamos la hora en SharedPreferences
                                    editor.apply();
                                    */
                                    // Guardar en Firebase
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                                    // Guardar la hora en el documento de la reserva
                                    db.collection("usuarios")
                                            .document(userId)
                                            .collection("Reservas")
                                            .document(idReserva)
                                            .update("CheckInHora", horaSeleccionada)
                                            .addOnSuccessListener(aVoid -> Log.d("Firebase", "Hora guardada correctamente"))
                                            .addOnFailureListener(e -> Log.e("Firebase", "Error al guardar la hora", e));
                                    // Cambiar el estado a "hora definida"
                                    horaDefinida = true;
                                    // Deshabilitar el TextView para evitar más cambios
                                    definirHoraLlegada.setClickable(false);
                                    definirHoraLlegada.setTextColor(Color.parseColor("#646464"));
                                }
                            } else {
                                // Si la hora está fuera del rango 2-4
                                Toast.makeText(getApplicationContext(), "Selecciona una hora entre las 2:00 y las 4:00", Toast.LENGTH_SHORT).show();
                            }
                        }, 2, 0, true); // Configura el TimePickerDialog para empezar con las 2:00

                // Mostrar el TimePickerDialog
                if (timePickerDialog.getWindow() != null) {
                    timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
                }
                timePickerDialog.show();
            }
        });
        btnServicio.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProcesarPago.class);
            historialItem.setFechaFin(null);
            historialItem.setFechaIni(null);
            intent.putExtra("HistorialItem", historialItem); // usuario es un objeto de tu clase
            startActivity(intent);
        });
    }


    private void cargarReserva(String idReserva) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios")
                .document(userId)
                .collection("Reservas")
                .document(idReserva)
                .get()
                .addOnSuccessListener(reservaDoc -> {
                    if (reservaDoc.exists()) {
                        String hotelId = reservaDoc.getString("hotelId");
                        String estado = reservaDoc.getString("estado");
                        Timestamp fechaInicioTS = reservaDoc.getTimestamp("fechaIni");
                        Timestamp fechaFinTS = reservaDoc.getTimestamp("fechaFin");
                        String checkInHora = reservaDoc.getString("CheckInHora");
                        String checkOutHora = reservaDoc.getString("CheckOutHora");
                        String solicitarTaxista = reservaDoc.getString("solicitarTaxista");
                        Boolean checkoutSolicitado = reservaDoc.getBoolean("checkoutSolicitado");
                        Boolean checkoutEnable = !(checkoutSolicitado);
                        Double personasDouble = reservaDoc.getDouble("personas");
                        int personas = personasDouble != null ? personasDouble.intValue() : 0;
                        String tipoHabitacion = reservaDoc.getString("tipoHab");

                        // Ahora puedes obtener datos del hotel
                        db.collection("Hoteles")
                                .document(hotelId)
                                .get()
                                .addOnSuccessListener(hotelDoc -> {
                                    if (hotelDoc.exists()) {
                                        String nombreHotelDoc = hotelDoc.getString("nombre");
                                        String ubicacionDoc = hotelDoc.getString("ubicacion");
                                        Boolean servicioTaxi = hotelDoc.getBoolean("servicioTaxi");
                                        Double valoracionDoc = hotelDoc.getDouble("valoracion");
                                        String contacto = hotelDoc.getString("contacto");
                                        int imagen = R.drawable.hotel1;
                                        if ("hotel2".equals(hotelDoc.getId())) {
                                            imagen = R.drawable.hotel2;
                                        }
                                        historialItem = new HistorialItem(idReserva, estado, nombreHotelDoc, "📍 " + ubicacionDoc, imagen, solicitarTaxista, checkoutEnable, servicioTaxi, fechaInicioTS, fechaFinTS);
                                        historialItem.setPersonas(personas);
                                        historialItem.setValoracion(valoracionDoc);
                                        historialItem.setContacto(contacto);
                                        historialItem.setTipoHab(tipoHabitacion);
                                        // Validación para CheckInHora
                                        if (checkInHora != null && !checkInHora.trim().isEmpty() && !checkInHora.equals("No especificado")) {
                                            historialItem.setCheckInHora(checkInHora);
                                            definirHoraLlegada.setText("Hora de llegada: " + checkInHora);
                                            horaDefinida = true;
                                            definirHoraLlegada.setClickable(false);
                                            definirHoraLlegada.setTextColor(Color.parseColor("#646464"));
                                        } else if (Objects.equals(checkInHora, "No especificado")) {
                                            historialItem.setCheckInHora(checkInHora);
                                            definirHoraLlegada.setText(checkInHora);
                                            horaDefinida = true;
                                            definirHoraLlegada.setClickable(false);
                                            definirHoraLlegada.setTextColor(Color.parseColor("#646464"));
                                        }

                                        // Validación para CheckOutHora
                                        if (checkOutHora != null && !checkOutHora.trim().isEmpty() && checkoutSolicitado) {
                                            historialItem.setCheckOutHora(checkOutHora);
                                            HoraDeSalida.setText("Finalizado a las " + checkOutHora);
                                            btnServicio.setEnabled(true);
                                            btnServicio.setAlpha(1f);
                                        }

                                        valorFecha.setText(historialItem.getRangoFechasBonito());
                                        valorPersonas.setText(historialItem.getPersonas() + " Personas");                                        nombreHotel.setText(historialItem.getNombreHotel());
                                        status.setText(historialItem.getEstado());
                                        valoracion.setText(String.valueOf(historialItem.getValoracion()));
                                        ubicacion.setText(historialItem.getUbicacion());
                                        valorHabitacion.setText(historialItem.getTipoHab());
                                        valorContacto.setText(historialItem.getContacto());
                                        fechaCheckIn.setText(historialItem.getFechaIniBonito());
                                        fechaCheckOut.setText(historialItem.getFechaFinBonito());
                                        imageHotel.setImageResource(imagen);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al cargar reservas", e));
    }
}