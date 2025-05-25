package com.example.hotelreservaapp.cliente;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetallesReserva extends AppCompatActivity {
    private TextView definirHoraLlegada, HoraDeSalida, FechaCheckOut;
    private Boolean horaDefinida;
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
        HoraDeSalida = findViewById(R.id.HoraDeSalida);
        FechaCheckOut = findViewById(R.id.fechaCheckOut);

        // 1. Cargar lista guardada (si existe)
        NotificacionesStorageHelper storageHelper = new NotificacionesStorageHelper(this);
        Notificaciones[] notificacionesGuardadas = storageHelper.leerArchivoNotificacionesDesdeSubcarpeta();

        // 2. Crear o cargar NotificationManager con la lista actual
        NotificationManagerNoAPP notificationManagerNoAPP = new NotificationManagerNoAPP();
        if (notificacionesGuardadas != null && notificacionesGuardadas.length > 0) {
            for (Notificaciones n : notificacionesGuardadas) {
                Tipo = n.getTipo();
                notificationManagerNoAPP.getListaNotificaciones().add(n);
                if ("02".equals(n.getTipo().trim())) {
                    long timestamp = n.getFecha(); // Tu valor long de fecha

                    Date date = new Date(timestamp);
                    // Formato para "23 de mayo"
                    SimpleDateFormat sdfDiaMes = new SimpleDateFormat("d 'de' MMMM", new Locale("es", "ES"));
                    // Formato para "14:30"
                    SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm", new Locale("es", "ES"));
                    String diaMes = sdfDiaMes.format(date); // Ej: "23 de mayo"
                    String hora = sdfHora.format(date);     // Ej: "14:30"
                    HoraDeSalida.setText("Finalizado a las " + hora);
                    FechaCheckOut.setText(diaMes+":");
                }
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
        SharedPreferences sharedPreferences = getSharedPreferences("ReservaPrefs", MODE_PRIVATE);
        String horaGuardada = sharedPreferences.getString("horaLlegada", null);

        if (horaGuardada != null) {
            // Si hay una hora guardada, mostrarla y deshabilitar el TextView
            definirHoraLlegada.setText("Hora de llegada: " + horaGuardada);
            horaDefinida = true;
            definirHoraLlegada.setClickable(false);
            definirHoraLlegada.setTextColor(Color.parseColor("#646464"));
        }

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

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("horaLlegada", horaSeleccionada); // Guardamos la hora en SharedPreferences
                                    editor.apply();

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
        // Ahora accedemos al botón btnProcesarPago dentro del nuevo modal
        Button btnProcesarPago = findViewById(R.id.btnProcesarPago);
        btnProcesarPago.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProcesarPago.class);
            startActivity(intent);
        });
    }
}