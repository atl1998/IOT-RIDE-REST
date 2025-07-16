package com.example.hotelreservaapp.loginAndRegister;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.example.hotelreservaapp.AdminHotel.MainActivity;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.SuperAdminMainActivity;
import com.example.hotelreservaapp.cliente.HomeCliente;
import com.example.hotelreservaapp.taxista.TaxistaMain;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private static final String TAG = "SplashDebug";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        Log.d(TAG, "Splash iniciado");

        // Retrasar para mostrar el splash (3 segundos)
        new android.os.Handler().postDelayed(this::verificarSesion, 3000);
    }

    private void verificarSesion() {
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d(TAG, "Usuario: " + user);

        if (user != null) {
            Log.d(TAG, "Usuario autenticado detectado: " + user.getUid());

            firestore.collection("usuarios")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            Log.d(TAG, "Documento Firestore encontrado para el usuario");
                            Boolean estado = doc.getBoolean("estado");
                            Boolean requiereCambio = doc.getBoolean("requiereCambioContrasena");
                            Log.d(TAG, "Estado: " + estado + ", RequiereCambio: " + requiereCambio);

                            if (estado != null && estado) {
                                if (requiereCambio != null && requiereCambio) {
                                    Log.d(TAG, "Requiere cambiar contraseña, redirigiendo...");

                                    Intent intent = new Intent(this, NuevaContrasenaActivity.class);
                                    intent.putExtra("uid", user.getUid());
                                    startActivity(intent);
                                    finish();
                                }else {
                                    String rol = doc.getString("rol");
                                    Log.d(TAG, "Rol del usuario: " + rol);

                                    redirigirSegunRol(rol);
                                }
                            } else {
                                Log.d(TAG, "Usuario inhabilitado en base de datos. Cerrando sesión...");

                                mAuth.signOut(); // <--- Muy importante
                                irALogin();
                            }
                        } else {
                            Log.d(TAG, "No se encontró el documento del usuario en Firestore.");

                            mAuth.signOut();
                            irALogin();
                        }
                    })
                    .addOnFailureListener(e -> irALogin());
        } else {
            Log.d(TAG, "No hay usuario autenticado (getCurrentUser() == null)");

            irALogin();
        }
    }


    private void irALogin() {
        Intent intent = new Intent(SplashActivity.this, InicioActivity.class);
        startActivity(intent);
        finish();
    }

    private void redirigirSegunRol(String rol) {
        Intent intent;
        switch (rol != null ? rol : "") {
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
                intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

}
