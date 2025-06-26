package com.example.hotelreservaapp.loginAndRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.AdminHotel.MainActivity;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.SuperAdminMainActivity;
import com.example.hotelreservaapp.cliente.HomeCliente;
import com.example.hotelreservaapp.taxista.TaxistaMain;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class NuevaContrasenaActivity extends AppCompatActivity {

    private TextInputEditText etNuevaContrasena, etConfirmarContrasena;
    private MaterialButton btnCambiarContrasena;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    //waos
    private boolean cambioRealizado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_contrasena);

        etNuevaContrasena = findViewById(R.id.etNuevaContrasena);
        etConfirmarContrasena = findViewById(R.id.etConfirmarContrasena);
        btnCambiarContrasena = findViewById(R.id.btnCambiarContrasena);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Actualizando contraseña...");
        progressDialog.setCancelable(false);

        String correo = getIntent().getStringExtra("correo");
        String uid = getIntent().getStringExtra("uid");
        String tempPass = getIntent().getStringExtra("tempPass");


        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        btnCambiarContrasena.setOnClickListener(v -> cambiarContrasena());
    }

    private void cambiarContrasena() {
        String nueva = etNuevaContrasena.getText().toString().trim();
        String confirmar = etConfirmarContrasena.getText().toString().trim();

        if (TextUtils.isEmpty(nueva) || TextUtils.isEmpty(confirmar)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nueva.equals(confirmar)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!esContrasenaSegura(nueva)) {
            Toast.makeText(this, "La contraseña no cumple con los requisitos de seguridad", Toast.LENGTH_LONG).show();
            return;
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            progressDialog.show();
            user.updatePassword(nueva)
                    .addOnSuccessListener(unused -> {
                        db.collection("usuarios").document(user.getUid())
                                .update("requiereCambioContrasena", false)
                                .addOnSuccessListener(aVoid -> {
                                    db.collection("usuarios").document(user.getUid())
                                            .get()
                                            .addOnSuccessListener(snapshot -> {
                                                progressDialog.dismiss();
                                                //owo
                                                cambioRealizado = true;
                                                String rol = snapshot.getString("rol");
                                                redirigirSegunRol(rol);
                                            });
                                });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Error al actualizar contraseña: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    private boolean esContrasenaSegura(String pass) {
        return pass.length() >= 8 &&
                pass.matches(".*[A-Z].*") &&
                pass.matches(".*\\d.*");
    }

    private void redirigirSegunRol(String rol) {
        Intent intent;
        switch (rol) {
            case "superadmin":
                intent = new Intent(this, SuperAdminMainActivity.class);
                break;
            case "adminHotel":
                intent = new Intent(this, MainActivity.class);
                break;
            case "taxista":
                intent = new Intent(this, TaxistaMain.class);
                break;
            case "cliente":
                intent = new Intent(this, HomeCliente.class);
                break;
            default:
                Toast.makeText(this, "Rol desconocido", Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }



}