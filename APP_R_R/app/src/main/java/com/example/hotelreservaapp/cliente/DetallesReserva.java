package com.example.hotelreservaapp.cliente;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.R;

public class DetallesReserva extends AppCompatActivity {
    private TextView definirHoraLlegada;
    private Boolean horaDefinida;

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

        // Inicializamos el TextView y la variable para saber si la hora ya fue definida
        definirHoraLlegada = findViewById(R.id.definirHoraLlegada);
        horaDefinida = false;

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
                timePickerDialog.show();
            }
        });

    }
}