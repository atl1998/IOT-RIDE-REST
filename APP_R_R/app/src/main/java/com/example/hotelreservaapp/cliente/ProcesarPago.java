package com.example.hotelreservaapp.cliente;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class ProcesarPago extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_procesar_pago);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonNavigationView);
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
        Button btnrealizarpago = findViewById(R.id.btn_realizar_pago);
        btnrealizarpago.setEnabled(false);
        btnrealizarpago.setAlpha(0.5f);


        // Recuperar la hora guardada de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ReservaPrefs", MODE_PRIVATE);
        Boolean ServicioTaxi = sharedPreferences.getBoolean("ServicioTaxi", false);

        if(ServicioTaxi){
            btnrealizarpago.setEnabled(true);
            btnrealizarpago.setAlpha(1f);
        }

        MaterialButton btnNotificaciones = findViewById(R.id.notificaciones_cliente);
        btnNotificaciones.setOnClickListener(v -> {
            Intent intent = new Intent(this, ClienteNotificaciones.class);
            startActivity(intent);
        });
        btnrealizarpago.setOnClickListener(v -> {
            mostrarDialogoTaxista();
        });
    }
    private void mostrarDialogoTaxista() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.cliente_dialog_taxista);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Obtener la vista del diálogo
        View modalView = dialog.findViewById(android.R.id.content);

        // Ahora puedes acceder al botón dentro del diálogo
        Button btn_aceptar = modalView.findViewById(R.id.btn_aceptar);
        btn_aceptar.setOnClickListener(v -> {
            // Cierra el modal actual
            dialog.dismiss();

            // Quitar el blur si es Android 12 o superior
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getWindow().getDecorView().setRenderEffect(null);
            }

            // Guardar en SharedPreferences que se solicitó el taxi
            SharedPreferences sharedPreferences = getSharedPreferences("ReservaPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("ServicioTaxi", true);
            editor.apply();

            // Lanzar la siguiente actividad
            Intent intent = new Intent(ProcesarPago.this, HistorialEventos.class);
            startActivity(intent);

            // Mostrar mensaje al usuario
            Toast.makeText(ProcesarPago.this, "¡Servicio solicitado correctamente!", Toast.LENGTH_SHORT).show();
        });
    }
}