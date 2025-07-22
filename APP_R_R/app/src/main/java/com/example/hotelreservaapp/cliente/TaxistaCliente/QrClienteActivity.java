package com.example.hotelreservaapp.cliente.TaxistaCliente;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.UUID;

public class QrClienteActivity extends AppCompatActivity {

    private ImageView ivQrCode;
    private MaterialButton btnHeTerminado;
    private String serviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_cliente);

        ivQrCode         = findViewById(R.id.ivQrCode);
        btnHeTerminado   = findViewById(R.id.btnHeTerminado);
        serviceId        = getIntent().getStringExtra("serviceId");

        // Generar un código único para el viaje
        String codigo = UUID.randomUUID().toString().substring(0, 8);
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            ivQrCode.setImageBitmap(
                    encoder.encodeBitmap(codigo, BarcodeFormat.QR_CODE, 400, 400)
            );
        } catch (WriterException e) {
            Toast.makeText(this, "Error generando QR", Toast.LENGTH_SHORT).show();
        }

        btnHeTerminado.setOnClickListener(v -> {
            // Actualizar estado a FINALIZADO en Firestore
            FirebaseFirestore.getInstance()
                    .collection("servicios_taxi")
                    .document(serviceId)
                    .update("estado", "Finalizado")
                    .addOnSuccessListener(u -> {
                        new AlertDialog.Builder(QrClienteActivity.this)
                                .setTitle("¡Viaje finalizado!")
                                .setMessage("Gracias por usar Ride & Rest")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    finishAffinity();
                                })
                                .show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(
                                QrClienteActivity.this,
                                "No se pudo finalizar el viaje",
                                Toast.LENGTH_SHORT
                        ).show();
                    });
        });
    }
}
