package com.example.hotelreservaapp.loginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.LogManager;
import com.example.hotelreservaapp.loginAndRegister.LoginActivity;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.ActivityUsuarioCrearContasenaBinding;
import com.example.hotelreservaapp.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsuarioCrearContasena extends AppCompatActivity {

    private ActivityUsuarioCrearContasenaBinding binding;
    private String nombres, apellidos, tipoDocumento, numeroDocumento;
    private String fechaNacimiento, correo, telefono, direccion, fotoPerfilUri;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioCrearContasenaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Recuperar datos del intent anterior
        Intent intent = getIntent();
        nombres = intent.getStringExtra("nombres");
        apellidos = intent.getStringExtra("apellidos");
        tipoDocumento = intent.getStringExtra("tipoDocumento");
        numeroDocumento = intent.getStringExtra("numeroDocumento");
        fechaNacimiento = intent.getStringExtra("fechaNacimiento");
        correo = intent.getStringExtra("correo");
        telefono = intent.getStringExtra("telefono");
        direccion = intent.getStringExtra("direccion");
        fotoPerfilUri = intent.getStringExtra("fotoPerfilUri");

        // Botón volver
        binding.btnBack.setOnClickListener(v -> onBackPressed());

        // Validación en tiempo real
        binding.etNuevaContrasena.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                validarContraseñaEnTiempoReal(s.toString());
            }
        });

        // Botón finalizar registro
        binding.btnFinalizarRegistro.setOnClickListener(v -> {
            String nuevaContrasena = binding.etNuevaContrasena.getText().toString().trim();
            String confirmarContrasena = binding.etConfirmarContrasena.getText().toString().trim();

            if (validarContraseñas(nuevaContrasena, confirmarContrasena)) {
                registrarUsuarioEnFirebase(nuevaContrasena);
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden o son inválidas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validarContraseñas(String nueva, String confirmar) {
        if (TextUtils.isEmpty(nueva) || TextUtils.isEmpty(confirmar)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return nueva.equals(confirmar);
    }

    private void validarContraseñaEnTiempoReal(String contrasena) {
        boolean tiene8 = contrasena.length() >= 8;
        boolean tieneMayus = contrasena.matches(".*[A-Z].*");
        boolean tieneMinus = contrasena.matches(".*[a-z].*");
        boolean tieneNumero = contrasena.matches(".*\\d.*");

        binding.tvIndicador1.setTextColor(getColor(tiene8 ? R.color.green : R.color.grey));
        binding.tvIndicador2.setTextColor(getColor(tieneMayus ? R.color.green : R.color.grey));
        binding.tvIndicador3.setTextColor(getColor(tieneMinus ? R.color.green : R.color.grey));
        binding.tvIndicador4.setTextColor(getColor(tieneNumero ? R.color.green : R.color.grey));
    }

    private void mostrarDialogoRegistroExitoso() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_registro_exitoso);
        builder.setCancelable(false);

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        new android.os.Handler().postDelayed(() -> {
            dialog.dismiss();
            irAInicioCliente();
        }, 3500);
    }

    private void registrarUsuarioEnFirebase(String contrasena) {
        mAuth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        Usuario usuario = new Usuario(
                                nombres,
                                apellidos,
                                "cliente",
                                tipoDocumento,
                                numeroDocumento,
                                fechaNacimiento,
                                correo,
                                telefono,
                                direccion,
                                fotoPerfilUri,
                                true,
                                false
                        );

                        firestore.collection("usuarios")
                                .document(firebaseUser.getUid())
                                .set(usuario)
                                .addOnSuccessListener(unused -> {
                                    String nombreCompleto = nombres + " " + apellidos;
                                    LogManager.registrarLogRegistro(
                                            nombreCompleto,
                                            "Registro de usuario",
                                            "El usuario se registró como cliente con el correo " + correo
                                    );
                                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                    mostrarDialogoRegistroExitoso();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    if (e.getMessage() != null && e.getMessage().contains("email address is already in use")) {
                        Toast.makeText(this, "El correo ya está registrado", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error en el registro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void irAInicioCliente() {
        Intent intent = new Intent(UsuarioCrearContasena.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
