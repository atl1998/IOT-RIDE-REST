package com.example.hotelreservaapp.loginAndRegister;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.databinding.ActivitySubirFotoResgistroTaxistaBinding;
import com.example.hotelreservaapp.model.PostulacionTaxista;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

public class SubirFotoResgistroTaxistaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;  // Solo manejaremos este request ahora

    private ImageView imagenSeleccionada;
    private MaterialButton btnSeleccionarGaleria, btnRegistrarVehiculo;
    private Uri imageUri;
    private String nombres, apellidos, tipoDocumento, numeroDocumento, fechaNacimiento, correo, telefono, direccion;
    private ActivitySubirFotoResgistroTaxistaBinding binding;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubirFotoResgistroTaxistaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar Firebase
        firestore = FirebaseFirestore.getInstance();

        // Recuperar los datos generales del taxista
        Intent intent = getIntent();
        nombres = intent.getStringExtra("nombres");
        apellidos = intent.getStringExtra("apellidos");
        tipoDocumento = intent.getStringExtra("tipoDocumento");
        numeroDocumento = intent.getStringExtra("numeroDocumento");
        fechaNacimiento = intent.getStringExtra("fechaNacimiento");
        correo = intent.getStringExtra("correo");
        telefono = intent.getStringExtra("telefono");
        direccion = intent.getStringExtra("direccion");

        // Inicializar vistas a través de binding
        imagenSeleccionada = binding.imagenSeleccionada;
        btnSeleccionarGaleria = binding.btnSeleccionarGaleria;
        btnRegistrarVehiculo = binding.btnRegistrarVehiculo;

        // Acción para el botón de regresar
        ImageButton btnBack = binding.btnBack;
        btnBack.setOnClickListener(v -> onBackPressed());

        // Acción para seleccionar una imagen de la galería
        btnSeleccionarGaleria.setOnClickListener(v -> openGallery());

        // Acción para registrar el vehículo y pasar a la siguiente vista (CrearContraseñaActivity)
        btnRegistrarVehiculo.setOnClickListener(v -> {
            if (validarFormulario()) {
                enviarPostulacionTaxista(nombres, apellidos, tipoDocumento, numeroDocumento, fechaNacimiento,
                        correo, telefono, direccion, binding.etPlaca.getText().toString(),
                        imageUri != null ? imageUri.toString() : null);
            }
        });
    }

    // Metodo para abrir la galería
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    // Manejar el resultado de la galería
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Si se seleccionó una imagen de la galería
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imagenSeleccionada.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para enviar la postulación a Firestore
    private void enviarPostulacionTaxista(String nombres, String apellidos, String tipoDocumento,
                                          String numeroDocumento, String fechaNacimiento, String correo,
                                          String telefono, String direccion, String numeroPlaca, String fotoPlacaURL) {

        // Mostrar un Toast de carga
        Toast.makeText(this, "Enviando postulación...", Toast.LENGTH_LONG).show();

        // Objeto PostulacionTaxista con todos los datos
        PostulacionTaxista postulacion = new PostulacionTaxista(
                nombres, apellidos, tipoDocumento, numeroDocumento, fechaNacimiento,
                correo, telefono, direccion, numeroPlaca, fotoPlacaURL,
                "pendiente" // Estado inicial de la postulación
        );

        // Guardar la postulación en la colección "postulacionesTaxistas"
        firestore.collection("postulacionesTaxistas")
                .add(postulacion) // Usamos .add() para que Firestore genere un ID de documento único
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "¡Postulación enviada con éxito! Espera la revisión.", Toast.LENGTH_LONG).show();
                    // Redirigir al LoginActivity ya que la postulación ha finalizado aquí
                    startActivity(new Intent(SubirFotoResgistroTaxistaActivity.this, LoginActivity.class));
                    finish(); // Finaliza esta actividad
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al enviar la postulación: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private boolean validarFormulario() {
        boolean valido = true;

        // Validación de la placa (tamaño y formato, ejemplo: 1234ABC)
        String placa = binding.etPlaca.getText().toString().trim();
        if (placa.isEmpty() || !placa.matches("[A-Za-z0-9]+")) {
            binding.tilPlaca.setError("Placa inválida. Solo letras y números.");
            valido = false;
        } else {
            binding.tilPlaca.setError(null);
        }

        // Validar si se ha seleccionado una foto
        if (imageUri == null) {
            Toast.makeText(this, "Por favor, sube una foto del vehículo", Toast.LENGTH_SHORT).show();
            valido = false;
        }

        return valido;
    }
}
