package com.example.hotelreservaapp.taxista;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.taxista.CaptureActivityPortrait;
import com.example.hotelreservaapp.taxista.fragments.TaxiInicioFragment;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.firebase.firestore.FirebaseFirestore;

public class QrLecturaActivity extends AppCompatActivity {

    private MaterialButton btnLeerQR, btnVolver;
    private String serviceId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_qr_lectura);

        btnLeerQR = findViewById(R.id.btnLeerQR);
        btnVolver = findViewById(R.id.btnVolver);

        // Recibimos el ID del documento de Firestore desde la Activity anterior
        serviceId = getIntent().getStringExtra("serviceId");

        btnLeerQR.setOnClickListener(v -> iniciarEscaneo());
        btnVolver.setOnClickListener(v -> {
            // Volvemos a la lista sin cambiar nada
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void iniciarEscaneo() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Escanea el código QR del pasajero");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // Solo si realmente leyó algo, procedemos
                actualizarEstadoFinalizado(result.getContents());
            } else {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void actualizarEstadoFinalizado(String qr) {
        // Aquí podrías validar el valor de `qr` si quieres (p.ej. equals(serviceId))
        FirebaseFirestore.getInstance()
                .collection("servicios_taxi")
                .document(serviceId)
                .update("estado", "Finalizado")
                .addOnSuccessListener(unused -> mostrarDialogoConfirmacion())
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Error al finalizar. Intenta de nuevo.",
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDatePickerDialog);
        View dialogView = getLayoutInflater()
                .inflate(R.layout.taxista_dialog_qr_confirmado, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        MaterialButton btnVolverInicio = dialogView.findViewById(R.id.btnVolverInicio);
        btnVolverInicio.setOnClickListener(v -> {
            dialog.dismiss();
            // Limpiamos todo el stack y volvemos al Fragmento de inicio
            Intent intent = new Intent(this, TaxistaMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }
}
