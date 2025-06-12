package com.example.hotelreservaapp.loginAndRegister;

import android.app.ProgressDialog;
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

import com.example.hotelreservaapp.AdminHotel.InicioFragment;
import com.example.hotelreservaapp.MainActivity;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.SuperAdminMainActivity;
import com.example.hotelreservaapp.cliente.HomeCliente;
import com.example.hotelreservaapp.model.DetallesTaxista;
import com.example.hotelreservaapp.model.Usuario;
import com.example.hotelreservaapp.taxista.TaxistaMain;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etCorreo, etContrasena;
    MaterialButton btnLogin;
    CheckBox checkRecordar;
    TextView tvOlvidoContrasena, tvRegistrate;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences prefs;
    private static final int RC_SIGN_IN = 1001;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Iniciando sesión...");
        progressDialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        inicializarVistas();
        configurarGoogleSignIn();
        verificarUsuarioLogueado();

        btnLogin.setOnClickListener(v -> loginConCorreo());
        findViewById(R.id.btnGoogle).setOnClickListener(v -> iniciarSesionGoogle());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        tvOlvidoContrasena.setOnClickListener(v -> Toast.makeText(this, "Redirigiendo a recuperación...", Toast.LENGTH_SHORT).show());
        tvRegistrate.setOnClickListener(v -> startActivity(new Intent(this, RegistroActivity.class)));
    }

    private void inicializarVistas() {
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnLogin = findViewById(R.id.btnLogin);
        checkRecordar = findViewById(R.id.checkRecordar);
        tvOlvidoContrasena = findViewById(R.id.tvOlvidoContrasena);
        tvRegistrate = findViewById(R.id.tvRegistrate);

        String correoGuardado = prefs.getString("correo_guardado", "");
        etCorreo.setText(correoGuardado);
        checkRecordar.setChecked(!correoGuardado.isEmpty());
    }

    private void configurarGoogleSignIn() {
        googleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build());
    }

    private void verificarUsuarioLogueado() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("usuarios").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            redirigirSegunRol(document.getString("rol"));
                        }
                    });
        }
    }

    private void loginConCorreo() {
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
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            if (checkRecordar.isChecked()) {
                                prefs.edit().putString("correo_guardado", correo).apply();
                            } else {
                                prefs.edit().remove("correo_guardado").apply();
                            }

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("usuarios").document(user.getUid())
                                    .get()
                                    .addOnSuccessListener(document -> {
                                        progressDialog.dismiss();
                                        if (document.exists()) {
                                            Boolean activo = document.getBoolean("estado");
                                            Boolean requiereCambio = document.getBoolean("requiereCambioContrasena");

                                            if (activo != null && !activo) {
                                                Toast.makeText(this, "Tu cuenta está inactiva. Contacta al administrador.", Toast.LENGTH_SHORT).show();
                                                firebaseAuth.signOut();
                                                return;
                                            }

                                            if (requiereCambio != null && requiereCambio) {
                                                Intent intent = new Intent(this, NuevaContrasenaActivity.class);
                                                startActivity(intent);
                                                finish();
                                                return;
                                            }
                                            redirigirSegunRol(document.getString("rol"));
                                        } else {
                                            Toast.makeText(this, "No se encontró el perfil del usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> progressDialog.dismiss());
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void iniciarSesionGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                autenticarConFirebase(account.getIdToken(), account.getDisplayName(), account.getEmail());
            } catch (ApiException e) {
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void autenticarConFirebase(String idToken, String displayName, String email) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        progressDialog.show();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("usuarios").document(user.getUid())
                                .get()
                                .addOnSuccessListener(document -> {
                                    if (!document.exists()) {
                                        String[] partes = displayName.trim().split("\\s+");
                                        String nombre = "", apellido = "";
                                        if (partes.length >= 4) {
                                            nombre = partes[0] + " " + partes[1];
                                            apellido = partes[2] + " " + partes[3];
                                        } else if (partes.length == 3) {
                                            nombre = partes[0];
                                            apellido = partes[1] + " " + partes[2];
                                        } else if (partes.length == 2) {
                                            nombre = partes[0];
                                            apellido = partes[1];
                                        } else if (partes.length == 1) {
                                            nombre = partes[0];
                                        }

                                        Usuario nuevoUsuario = new Usuario(
                                                nombre,
                                                apellido,
                                                "cliente", // Por defecto
                                                "",
                                                "",
                                                "",
                                                email,
                                                "",
                                                "",
                                                "",
                                                true,
                                                false
                                        );
                                        db.collection("usuarios").document(user.getUid()).set(nuevoUsuario)
                                                .addOnSuccessListener(aVoid -> {
                                                    progressDialog.dismiss();
                                                    redirigirSegunRol("cliente");
                                                });
                                    } else {
                                        Boolean activo = document.getBoolean("estado");
                                        if (activo != null && activo) {
                                            progressDialog.dismiss();
                                            redirigirSegunRol(document.getString("rol"));
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(this, "Tu cuenta está inactiva. Contacta al administrador.", Toast.LENGTH_SHORT).show();
                                            firebaseAuth.signOut(); // Opcional: desloguear al usuario si no está activo
                                        }                                    }
                                });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Falló la autenticación con Google", Toast.LENGTH_SHORT).show();
                    }
                });
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
                return;
        }
        startActivity(intent);
        finish();
    }
}
