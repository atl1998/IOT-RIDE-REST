package com.example.hotelreservaapp.loginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.model.PostulacionTaxista;
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

        // Registrar el taxista en Firebase
        enviarPostulacionTaxista(nombres, apellidos, tipoDocumento, numeroDocumento, fechaNacimiento,
                correo, telefono, direccion, placa, fotoVehiculo);    }

    // Metodo para postulación en Firebase
    private void enviarPostulacionTaxista(String nombres, String apellidos, String tipoDocumento,
                                          String numeroDocumento, String fechaNacimiento, String correo,
                                          String telefono, String direccion, String numeroPlaca, String fotoPlacaURL) {

        // Objeto PostulacionTaxista con los datos recibidos
        PostulacionTaxista postulacion = new PostulacionTaxista(
                nombres, apellidos, tipoDocumento, numeroDocumento, fechaNacimiento,
                correo, telefono, direccion, numeroPlaca, fotoPlacaURL,
                "pendiente" // Estado inicial de la postulación
        );

        // Guardar la postulación en la colección "postulacionesTaxistas"
        firestore.collection("postulacionesTaxistas")
                .add(postulacion) // Usamos .add() para que Firestore genere un ID de documento único
                .addOnSuccessListener(documentReference -> {
                    // Postulación guardada exitosamente
                    Toast.makeText(this, "¡Postulación enviada con éxito! Espera la revisión.", Toast.LENGTH_LONG).show();


                    // Redirigir a una pantalla de confirmación o al login general (si aplica)
                    // No se redirige a TaxistaMain directamente porque aún no es un taxista aprobado
                    startActivity(new Intent(RegistroTaxistaActivity.this, LoginActivity.class));
                    finish(); // Finaliza esta actividad para que el usuario no pueda volver atrás
                })
                .addOnFailureListener(e -> {
                    // Si ocurre un error al guardar en Firestore
                    Toast.makeText(this, "Error al enviar la postulación: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
