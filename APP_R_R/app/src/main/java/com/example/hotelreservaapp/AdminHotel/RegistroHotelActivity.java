package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegistroHotelActivity extends AppCompatActivity {
    TextInputEditText etNombre, etDescripcion, etDepartamento, etProvincia, etDistrito, etDireccion;

    MaterialButton btnContinuar1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cargarVistaDatos();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }





    /// vistas
    private void cargarVistaDatos() {
        setContentView(R.layout.adminhotel_activity_registro_hotel);

        ImageButton btnBack = findViewById(R.id.btnBack);
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etDepartamento = findViewById(R.id.etDepartamento);
        etProvincia = findViewById(R.id.etProvincia);
        etDistrito = findViewById(R.id.etDistrito);
        etDireccion = findViewById(R.id.etDireccion);

        btnBack.setOnClickListener(v -> finish());

        btnContinuar1 = findViewById(R.id.btnContinuar1);
        btnContinuar1.setOnClickListener(v -> {
            if (DatosEsValido()) {
                cargarVistaSubirFoto();
            } else {
                //mostrarErroresFormulario();
            }
        });
    }

    private void CargarVistaSubirFoto() {
        setContentView(R.layout.adminhotel_activity_subir_foto);

    }


    /// Validación de formularios
    private boolean DatosEsValido() {
        return
                !etNombre.getText().toString().trim().isEmpty() &&
                        !etDescripcion.getText().toString().trim().isEmpty() &&
                        !etDireccion.getText().toString().trim().isEmpty();

    }
}