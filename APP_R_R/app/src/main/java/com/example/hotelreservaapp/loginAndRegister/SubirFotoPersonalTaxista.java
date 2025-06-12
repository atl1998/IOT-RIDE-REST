package com.example.hotelreservaapp.loginAndRegister;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.databinding.ActivitySubirFotoPersonalTaxista2Binding;

import java.io.IOException;

public class SubirFotoPersonalTaxista extends AppCompatActivity {

    private ActivitySubirFotoPersonalTaxista2Binding binding;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    // Datos recibidos de la vista anterior
    private String nombres, apellidos, tipoDocumento, numeroDocumento;
    private String fechaNacimiento, correo, telefono, direccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Inicializar binding
        binding = ActivitySubirFotoPersonalTaxista2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        // Botón para regresar
        binding.btnBack.setOnClickListener(v -> onBackPressed());

        // Botón para abrir galería
        binding.btnAbrirGaleria.setOnClickListener(v -> openGallery());

        // Botón para continuar al registro del vehículo
        binding.btnSubirFotoTaxista.setOnClickListener(v -> {
            if (validarFormulario()) {

                // Obtener el nombre del usuario
                String nombreUsuario = nombres;

                // Mostrar el Toast de validación
                Toast.makeText(SubirFotoPersonalTaxista.this,
                        nombreUsuario + "Ya casi, sube los datos de tu vehículo",
                        Toast.LENGTH_SHORT).show();

                irARegistroVehiculo();
            }
        });
    }

    // Metodo para abrir la galería
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    // Manejo del resultado de la galería
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                binding.imagenSeleccionada.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Validación del formulario
    private boolean validarFormulario() {
        if (imageUri == null) {
            Toast.makeText(this, "Por favor, sube una foto tuya", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Ir a la vista de registro del vehículo
    private void irARegistroVehiculo() {
        Intent intent = new Intent(SubirFotoPersonalTaxista.this, SubirFotoResgistroTaxistaActivity.class);

        // Pasar todos los datos
        intent.putExtra("nombres", nombres);
        intent.putExtra("apellidos", apellidos);
        intent.putExtra("tipoDocumento", tipoDocumento);
        intent.putExtra("numeroDocumento", numeroDocumento);
        intent.putExtra("fechaNacimiento", fechaNacimiento);
        intent.putExtra("correo", correo);
        intent.putExtra("telefono", telefono);
        intent.putExtra("direccion", direccion);
        intent.putExtra("fotoPerfilUri", imageUri.toString());

        startActivity(intent);
    }
}
