package com.example.hotelreservaapp.loginAndRegister;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;

public class SubirFotoResgistroTaxistaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private ImageView imagenSeleccionada;
    private MaterialButton btnAbrirCamara, btnRegistrarVehiculo, btnSeleccionarGaleria;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_foto_resgistro_taxista);

        // Inicializar vistas
        imagenSeleccionada = findViewById(R.id.imagenSeleccionada);
        btnAbrirCamara = findViewById(R.id.btnAbrirCamara);
        btnRegistrarVehiculo = findViewById(R.id.btnRegistrarVehiculo);
        btnSeleccionarGaleria = findViewById(R.id.btnSeleccionarGaleria);

        // Acción para el botón de regresar
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Acción para abrir la cámara y tomar una foto
        btnAbrirCamara.setOnClickListener(v -> openCamera());

        // Acción para seleccionar una imagen de la galería
        btnSeleccionarGaleria.setOnClickListener(v -> openGallery());

        // Acción para registrar el vehículo y pasar a la siguiente vista (CrearContraseñaActivity)
        btnRegistrarVehiculo.setOnClickListener(v -> {
            // Lógica para registrar el vehículo (solo pasamos a la siguiente vista)
            cargarVistaCrearContraseña();
        });
    }

    // Metodo para abrir la cámara
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
            Toast.makeText(this, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    // Metodo para abrir la galería
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    // Manejar el resultado de la cámara o la galería
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
        // Si se tomó una foto con la cámara
        else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imagenSeleccionada.setImageBitmap(imageBitmap);
        }
    }

    // Metodo para cargar la vista de creación de contraseña (pasar a la siguiente actividad)
    private void cargarVistaCrearContraseña() {
        // Cambiar a la actividad de creación de contraseña
        Intent intent = new Intent(SubirFotoResgistroTaxistaActivity.this, CrearContrasenaActivity.class);
        startActivity(intent);
    }
}
