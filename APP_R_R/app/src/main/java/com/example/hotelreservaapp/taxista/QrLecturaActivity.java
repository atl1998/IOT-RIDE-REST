package com.example.hotelreservaapp.taxista;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;

public class QrLecturaActivity extends AppCompatActivity {

    private MaterialButton btnLeerQR;
    private MaterialButton btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_qr_lectura);

        btnLeerQR = findViewById(R.id.btnLeerQR);
        btnVolver = findViewById(R.id.btnVolver);

        btnLeerQR.setOnClickListener(v -> mostrarDialogoConfirmacion());
        btnVolver.setOnClickListener(v -> volverAlInicio());
    }

    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDatePickerDialog);
        View dialogView = getLayoutInflater().inflate(R.layout.taxista_dialog_qr_confirmado, null);
        builder.setView(dialogView);

        MaterialButton btnVolverInicio = dialogView.findViewById(R.id.btnVolverInicio);
        AlertDialog dialog = builder.create();

        btnVolverInicio.setOnClickListener(v -> {
            dialog.dismiss();

            // Prepara Intent con extra para indicar viaje concluido
            Intent intent = new Intent(this, TaxistaMain.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("notificacion_viaje_concluido", true);
            intent.putExtra("nombre_usuario", "Nombre Cliente");   // reemplaza con real si tienes
            intent.putExtra("ubicacion", "Ubicación Destino");     // reemplaza con real si tienes
            startActivity(intent);
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private void volverAlInicio() {
        Intent intent = new Intent(this, TaxistaMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
