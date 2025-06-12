package com.example.hotelreservaapp.taxista;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class VerDatosVehiculoTaxistaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String PREFS_NAME = "vehiculo_prefs";
    private static final String KEY_PLACA = "placa";
    private static final String KEY_IMAGEN_URI = "imagen_uri";

    private ImageView imagenSeleccionada;
    private TextInputEditText etPlaca;
    private ImageView btnEditarPlaca;
    private MaterialButton btnBack, btnSeleccionarImagen, btnActualizarVehiculo;

    private Uri imagenUriSeleccionada; // Para guardar la URI de la imagen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_ver_datos_vehiculo_activity);

        // Referencias a la vista
        imagenSeleccionada = findViewById(R.id.imagenSeleccionada);
        etPlaca = findViewById(R.id.etPlaca);
        btnEditarPlaca = findViewById(R.id.btnEditarPlaca);
        btnBack = findViewById(R.id.btnBack);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnActualizarVehiculo = findViewById(R.id.btnActualizarVehiculo);

        // Volver
        btnBack.setOnClickListener(v -> onBackPressed());

        // Habilitar edición de placa
        btnEditarPlaca.setOnClickListener(v -> {
            etPlaca.setEnabled(true);
            etPlaca.requestFocus();
        });

        // Abrir galería
        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());

        // Guardar datos localmente
        btnActualizarVehiculo.setOnClickListener(v -> {
            guardarDatosEnLocalStorage();
        });

        // Cargar datos guardados
        cargarDatosGuardados();
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imagenUriSeleccionada = data.getData();
            imagenSeleccionada.setImageURI(imagenUriSeleccionada);
        }
    }

    private void guardarDatosEnLocalStorage() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Guardar placa
        String placa = etPlaca.getText().toString().trim();
        editor.putString(KEY_PLACA, placa);

        // Guardar URI como string si se seleccionó imagen
        if (imagenUriSeleccionada != null) {
            editor.putString(KEY_IMAGEN_URI, imagenUriSeleccionada.toString());
        }

        editor.apply();
    }

    private void cargarDatosGuardados() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Cargar placa
        String placa = prefs.getString(KEY_PLACA, null);
        if (placa != null) {
            etPlaca.setText(placa);
        }

        // Cargar imagen
        String uriString = prefs.getString(KEY_IMAGEN_URI, null);
        if (uriString != null) {
            Uri imageUri = Uri.parse(uriString);
            imagenSeleccionada.setImageURI(imageUri);
            imagenUriSeleccionada = imageUri;
        }
    }
}
