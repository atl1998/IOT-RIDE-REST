package com.example.hotelreservaapp.loginAndRegister;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.SuperAdminMainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etCorreo, etContrasena;
    MaterialButton btnLogin;
    CheckBox checkRecordar;
    TextView tvOlvidoContrasena, tvRegistrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Uso de shared preferences pal modo Oscuro :D
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean modoOscuro = prefs.getBoolean("modo_oscuro", false);
        if (modoOscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (prefs.getBoolean("logueado", false)) {
            startActivity(new Intent(this, SuperAdminMainActivity.class));
            finish();
            return;
        }

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

            // 🔐 Validación simulada (hardcoded)
            if (correo.equals("niloadmin@pucp.edu.pe") && contrasena.equals("123456")) {
                Toast.makeText(LoginActivity.this, "¡Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show();

                // ✅ Guardar sesión con SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("correo", correo);
                editor.putBoolean("logueado", true);
                editor.apply();

                // Ir al menú
                startActivity(new Intent(this, SuperAdminMainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
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
