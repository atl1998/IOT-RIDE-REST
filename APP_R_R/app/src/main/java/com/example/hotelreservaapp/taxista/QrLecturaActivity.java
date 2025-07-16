package com.example.hotelreservaapp.taxista;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.taxista.CaptureActivityPortrait;
import com.example.hotelreservaapp.taxista.TaxistaMain;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.firebase.firestore.FirebaseFirestore;

public class QrLecturaActivity extends AppCompatActivity {

    private MaterialButton btnLeerQR, btnVolver;
    private String nombreCliente, telefonoCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_qr_lectura);

        btnLeerQR = findViewById(R.id.btnLeerQR);
        btnVolver = findViewById(R.id.btnVolver);

        nombreCliente = getIntent().getStringExtra("nombre");
        telefonoCliente = getIntent().getStringExtra("telefono");

        btnLeerQR.setOnClickListener(v -> iniciarEscaneo());
        btnVolver.setOnClickListener(v -> volverAlInicio());
    }

    private void iniciarEscaneo() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Escanea el código QR del pasajero");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(CaptureActivityPortrait.class); // opcional si deseas forzar portrait
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                confirmarFinalizacion(result.getContents());
            } else {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void confirmarFinalizacion(String codigo) {
        // Validación opcional: puedes revisar el formato del QR escaneado

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("servicios_taxi")
                .whereEqualTo("nombreCliente", nombreCliente)
                .whereEqualTo("telefonoCliente", telefonoCliente)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        querySnapshot.getDocuments().get(0).getReference()
                                .update("estado", "Finalizado")
                                .addOnSuccessListener(unused -> mostrarDialogoConfirmacion());
                    } else {
                        Toast.makeText(this, "No se encontró el servicio en Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDatePickerDialog);
        View dialogView = getLayoutInflater().inflate(R.layout.taxista_dialog_qr_confirmado, null);
        builder.setView(dialogView);

        MaterialButton btnVolverInicio = dialogView.findViewById(R.id.btnVolverInicio);
        AlertDialog dialog = builder.create();

        btnVolverInicio.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, TaxistaMain.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
