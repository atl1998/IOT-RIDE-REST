package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class PagoConTarjeta extends AppCompatActivity {
    private TextInputLayout tilCardNumber, tilCardHolder, tilExpiry, tilCVV;
    private TextInputEditText etCardNumber, etCardHolder, etExpiry, etCVV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pago_con_tarjeta);

        // Iniciar los campos
        tilCardNumber = findViewById(R.id.tilCardNumber);
        tilCardHolder = findViewById(R.id.tilCardHolder);
        tilExpiry = findViewById(R.id.tilExpiry);
        tilCVV = findViewById(R.id.tilCVV);

        etCardNumber = findViewById(R.id.etCardNumber);
        etCardHolder = findViewById(R.id.etCardHolder);
        etExpiry = findViewById(R.id.etExpiry);
        etCVV = findViewById(R.id.etCVV);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button btnPagar = findViewById(R.id.btnPagar);
        btnPagar.setOnClickListener(v -> {
            // Validar los campos antes de proceder con el pago
            if (isValidForm()) {
                // Si las validaciones son correctas, proceder al siguiente Activity
                Intent intent = new Intent(this, PagoConfirmado.class);
                startActivity(intent);
            }
        });
    }
    // Método para validar los campos
    private boolean isValidForm() {
        boolean valid = true;

        // Validar número de tarjeta
        String cardNumber = etCardNumber.getText().toString().trim();
        if (TextUtils.isEmpty(cardNumber) || !cardNumber.matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}")) {
            tilCardNumber.setError("Número de tarjeta inválido");
            valid = false;
        } else {
            tilCardNumber.setError(null);  // Quitar el error
        }

        // Validar nombre del titular
        String cardHolder = etCardHolder.getText().toString().trim();
        if (TextUtils.isEmpty(cardHolder) || !cardHolder.matches("[a-zA-Z\\s]+")) {
            tilCardHolder.setError("Nombre inválido");
            valid = false;
        } else {
            tilCardHolder.setError(null);  // Quitar el error
        }

        // Validar fecha de vencimiento (MM/YY)
        String expiry = etExpiry.getText().toString().trim();
        if (TextUtils.isEmpty(expiry) || !expiry.matches("\\d{2}/\\d{2}")) {
            tilExpiry.setError("Fecha inválida");
            valid = false;
        } else {
            tilExpiry.setError(null);  // Quitar el error
        }

        // Validar CVV (tres dígitos)
        String cvv = etCVV.getText().toString().trim();
        if (TextUtils.isEmpty(cvv) || !cvv.matches("\\d{3}")) {
            tilCVV.setError("CVV inválido");
            valid = false;
        } else {
            tilCVV.setError(null);  // Quitar el error
        }

        return valid;
    }
}