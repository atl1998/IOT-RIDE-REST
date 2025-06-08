package com.example.hotelreservaapp.loginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificarCorreoActivity extends AppCompatActivity {

    private TextView tvCorreo;
    private MaterialButton btnReenviarCorreo, btnAbrirAppCorreo;
    private FirebaseUser usuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_correo);

        // Inicializar vistas
        tvCorreo = findViewById(R.id.tvCorreo);
        btnReenviarCorreo = findViewById(R.id.btnReenviarCorreo);
        btnAbrirAppCorreo = findViewById(R.id.btnAbrirAppCorreo);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Obtener usuario actual
        usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioActual != null) {
            tvCorreo.setText("Correo: " + usuarioActual.getEmail());
        }

        // Botón atrás
        btnBack.setOnClickListener(v -> finish());

        // Botón Reenviar correo
        btnReenviarCorreo.setOnClickListener(v -> {
            if (usuarioActual != null) {
                usuarioActual.sendEmailVerification()
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(this, "Correo enviado correctamente", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error al reenviar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        // Botón abrir app de correo (si es posible)
        btnAbrirAppCorreo.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No se pudo abrir la app de correo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
