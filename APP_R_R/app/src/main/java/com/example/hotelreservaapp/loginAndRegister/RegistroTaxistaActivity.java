package com.example.hotelreservaapp.loginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.model.Taxista;
import com.example.hotelreservaapp.taxista.TaxistaMain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistroTaxistaActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar FirebaseAuth y FirebaseFirestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Recuperar todos los datos pasados desde la actividad de CrearContraseñaTaxista
        Intent intent = getIntent();
        String nombres = intent.getStringExtra("nombres");
        String apellidos = intent.getStringExtra("apellidos");
        String tipoDocumento = intent.getStringExtra("tipoDocumento");
        String numeroDocumento = intent.getStringExtra("numeroDocumento");
        String fechaNacimiento = intent.getStringExtra("fechaNacimiento");
        String correo = intent.getStringExtra("correo");
        String telefono = intent.getStringExtra("telefono");
        String direccion = intent.getStringExtra("direccion");
        String placa = intent.getStringExtra("placa");
        String fotoVehiculo = intent.getStringExtra("fotoVehiculo");
        String nuevaContrasena = intent.getStringExtra("nuevaContrasena");

        // Registrar el taxista en Firebase
        registrarTaxistaEnFirebase(nombres, apellidos, tipoDocumento, numeroDocumento, fechaNacimiento, correo, telefono, direccion, placa, fotoVehiculo, nuevaContrasena);
    }

    // Metodo para registrar el taxista en Firebase
    private void registrarTaxistaEnFirebase(String nombres, String apellidos, String tipoDocumento, String numeroDocumento,
                                            String fechaNacimiento, String correo, String telefono, String direccion,
                                            String placa, String fotoVehiculo, String nuevaContrasena) {

        // Creamos un objeto Taxista con los datos del taxista
        Taxista taxista = new Taxista(nombres, apellidos, tipoDocumento, numeroDocumento, fechaNacimiento,
                correo, telefono, direccion, null, true, false, placa, fotoVehiculo);

        // Registrar el taxista en Firebase Authentication y Firestore
        mAuth.createUserWithEmailAndPassword(correo, nuevaContrasena)
                .addOnSuccessListener(authResult -> {
                    // Si el registro es exitoso en Firebase Auth, guardamos los datos en Firestore
                    String userId = mAuth.getCurrentUser().getUid();

                    firestore.collection("taxistas")
                            .document(userId)
                            .set(taxista)
                            .addOnSuccessListener(aVoid -> {
                                // Registro exitoso en Firestore
                                Toast.makeText(this, "Registro exitoso como taxista!", Toast.LENGTH_SHORT).show();
                                // Redirigir al inicio o a la pantalla de login
                                startActivity(new Intent(RegistroTaxistaActivity.this, LoginActivity.class)); // Redirigir a la pantalla principal
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Si ocurre un error al guardar en Firestore
                                Toast.makeText(this, "Error al guardar los datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Si ocurre un error al registrar en Firebase Auth
                    Toast.makeText(this, "Error al registrar el taxista: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
