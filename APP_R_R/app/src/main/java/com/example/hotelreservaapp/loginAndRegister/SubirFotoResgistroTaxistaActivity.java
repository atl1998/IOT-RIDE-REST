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

import com.example.hotelreservaapp.databinding.ActivitySubirFotoResgistroTaxistaBinding;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;

public class SubirFotoResgistroTaxistaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;  // Solo manejaremos este request ahora

    private ImageView imagenSeleccionada;
    private MaterialButton btnSeleccionarGaleria, btnRegistrarVehiculo;
    private Uri imageUri;
    private String nombres, apellidos, tipoDocumento, numeroDocumento, fechaNacimiento, correo, telefono, direccion;
    private ActivitySubirFotoResgistroTaxistaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubirFotoResgistroTaxistaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                // Obtener el nombre del usuario
                String nombreUsuario = nombres.trim(); // Usar el nombre ya recuperado

                // Mostrar el Toast de validación
                Toast.makeText(SubirFotoResgistroTaxistaActivity.this,
                        nombreUsuario + ", Crea una contraseña para finalizar el registro",
                        Toast.LENGTH_SHORT).show();

                // Cargar la siguiente vista (CrearContraseñaActivity)
                cargarVistaCrearContraseña();
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

    // Metodo para cargar la vista de creación de contraseña (pasar a la siguiente actividad)
    private void cargarVistaCrearContraseña() {
        // Cambiar a la actividad de creación de contraseña
        Intent intent = new Intent(SubirFotoResgistroTaxistaActivity.this, CrearContrasenaTaxistaActivity.class);

        // Recuperar los datos del vehículo (como placa y foto) y pasar a la siguiente actividad
        intent.putExtra("placa", binding.etPlaca.getText().toString());

        // Pasar también los datos generales para mantener el flujo
        intent.putExtra("nombres", nombres);
        intent.putExtra("apellidos", apellidos);
        intent.putExtra("tipoDocumento", tipoDocumento);
        intent.putExtra("numeroDocumento", numeroDocumento);
        intent.putExtra("fechaNacimiento", fechaNacimiento);
        intent.putExtra("correo", correo);
        intent.putExtra("telefono", telefono);
        intent.putExtra("direccion", direccion);
        intent.putExtra("fotoVehiculo", imageUri.toString()); // Guardamos la URI de la foto
        startActivity(intent);
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
