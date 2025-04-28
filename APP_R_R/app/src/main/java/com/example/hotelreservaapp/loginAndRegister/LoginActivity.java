package com.example.hotelreservaapp.loginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etCorreo, etContrasena;
    MaterialButton btnLogin;
    CheckBox checkRecordar;
    TextView tvOlvidoContrasena, tvRegistrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Referencias
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnLogin = findViewById(R.id.btnLogin);
        checkRecordar = findViewById(R.id.checkRecordar);
        tvOlvidoContrasena = findViewById(R.id.tvOlvidoContrasena);
        tvRegistrate = findViewById(R.id.tvRegistrate);


        View btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish()); // vuelve a la pantalla anterior


        // Acción botón login
        btnLogin.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();

            if (TextUtils.isEmpty(correo)) {
                etCorreo.setError("Ingresa tu correo");
                return;
            }

            if (TextUtils.isEmpty(contrasena)) {
                etContrasena.setError("Ingresa tu contraseña");
                return;
            }

            // Aquí iría la validación real, por ahora mostramos mensaje:
            Toast.makeText(LoginActivity.this, "¡Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show();

            // Ejemplo: ir al menú de cliente tras login
            // startActivity(new Intent(this, MenuClienteActivity.class));
        });

        // Acción: Olvidaste contraseña
        tvOlvidoContrasena.setOnClickListener(v -> {
            Toast.makeText(this, "Redirigiendo a recuperación de contraseña...", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, RecuperarContrasenaActivity.class));
        });

        // Acción: Registrarse
        tvRegistrate.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroActivity.class));
        });
        
        
    }
}
