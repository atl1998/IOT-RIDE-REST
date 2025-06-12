package com.example.hotelreservaapp.loginAndRegister;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.MainActivity;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.ActivityCrearContrasenaBinding;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;


public class CrearContrasenaActivity extends AppCompatActivity {

    private ActivityCrearContrasenaBinding binding;
    private boolean isTaxista;  // Variable para diferenciar entre taxista y usuario común

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCrearContrasenaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtener si el usuario es taxista o no desde el Intent
        isTaxista = getIntent().getBooleanExtra("IS_TAXISTA", false);

        // Acción para el botón de regresar
        binding.btnBack.setOnClickListener(v -> onBackPressed());

        // Acción para el botón de finalizar registro
        binding.btnFinalizarRegistro.setOnClickListener(v -> {
            String nuevaContrasena = binding.etNuevaContrasena.getText().toString().trim();
            String confirmarContrasena = binding.etConfirmarContrasena.getText().toString().trim();

            // Validación de las contraseñas
            if (validarContraseñas(nuevaContrasena, confirmarContrasena)) {
                // Mostrar el diálogo de registro exitoso dependiendo de si es taxista o usuario común
                if (isTaxista) {
                    mostrarDialogoRegistroExitosoTaxista();  // Para el taxista
                } else {
                    mostrarDialogoRegistroExitosoUsuario();  // Para el usuario común
                }
            } else {
                // Si las contraseñas no coinciden o son inválidas
                Toast.makeText(this, "Las contraseñas no coinciden o son inválidas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Validar que las contraseñas no estén vacías y coincidan
    private boolean validarContraseñas(String nuevaContrasena, String confirmarContrasena) {
        if (TextUtils.isEmpty(nuevaContrasena) || TextUtils.isEmpty(confirmarContrasena)) {
            return false;
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            return false;
        }

        // Aquí puedes agregar validaciones de fuerza de la contraseña si lo deseas
        return true;
    }

    // Mostrar el diálogo de registro exitoso para el taxista
    private void mostrarDialogoRegistroExitosoTaxista() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_registro_exitoso_taxista);  // El diseño de tu diálogo para el taxista

        builder.setCancelable(false);
        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        // Redirigir al inicio después de 2.5 segundos
        new android.os.Handler().postDelayed(() -> {
            dialog.dismiss();
            // Redirigir al inicio o a la pantalla de login
            Intent intent = new Intent(CrearContrasenaActivity.this, InicioActivity.class);
            startActivity(intent);
            finish();
        }, 2500);  // Espera 2.5 segundos antes de redirigir
    }

    // Mostrar el diálogo de registro exitoso para el usuario común
    private void mostrarDialogoRegistroExitosoUsuario() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_registro_exitoso);  // El diseño de tu diálogo para el usuario común

        builder.setCancelable(false);
        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        // Redirigir al inicio después de 2.5 segundos
        new android.os.Handler().postDelayed(() -> {
            dialog.dismiss();
            // Redirigir al inicio o a la pantalla de login
            Intent intent = new Intent(CrearContrasenaActivity.this, InicioActivity.class);
            startActivity(intent);
            finish();
        }, 2500);  // Espera 2.5 segundos antes de redirigir
    }
}
