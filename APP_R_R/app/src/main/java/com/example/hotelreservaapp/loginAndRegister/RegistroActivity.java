package com.example.hotelreservaapp.loginAndRegister;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class RegistroActivity extends AppCompatActivity {

    TextInputEditText etNombres, etApellidos, etDocumento, etFechaNacimiento,
            etCorreo, etTelefono, etDomicilio, etContrasena;
    Spinner spinnerDocumento;
    MaterialButton btnContinuar;
    ProgressBar pbSeguridad;
    TextView tvNivelSeguridad, tvEjemploDocumento;

    // Subir foto
    ImageView imgSeleccionada;
    MaterialButton btnCamara, btnRegistrarFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cargarVistaFormulario();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void cargarVistaFormulario() {
        setContentView(R.layout.activity_registro);

        ImageButton btnBack = findViewById(R.id.btnBack);
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etDocumento = findViewById(R.id.etDocumento);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etCorreo = findViewById(R.id.etCorreo);
        etTelefono = findViewById(R.id.etTelefono);
        etDomicilio = findViewById(R.id.etDomicilio);
        etContrasena = findViewById(R.id.etContrasena);
        spinnerDocumento = findViewById(R.id.spinnerDocumento);
        btnContinuar = findViewById(R.id.btnContinuar);
        pbSeguridad = findViewById(R.id.pbSeguridad);
        tvNivelSeguridad = findViewById(R.id.tvNivelSeguridad);
        tvEjemploDocumento = findViewById(R.id.tvEjemploDocumento);

        btnBack.setOnClickListener(v -> finish());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.tipos_documento,
                R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerDocumento.setAdapter(adapter);

        // Actualizar ejemplo de documento
        spinnerDocumento.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                String tipo = spinnerDocumento.getSelectedItem().toString();
                switch (tipo) {
                    case "DNI":
                        tvEjemploDocumento.setText("Ejemplo: 12345678");
                        break;
                    case "Carné de Extranjería":
                        tvEjemploDocumento.setText("Ejemplo: 001234567");
                        break;
                    case "Pasaporte peruano":
                        tvEjemploDocumento.setText("Ejemplo: AB123456");
                        break;
                }
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        etFechaNacimiento.setOnClickListener(v -> mostrarCalendario());

        etContrasena.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                evaluarSeguridadContrasena(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnContinuar.setOnClickListener(v -> {
            if (formularioEsValido()) {
                cargarVistaSubirFoto();
            } else {
                mostrarErroresFormulario();
            }
        });
    }

    private void cargarVistaSubirFoto() {
        setContentView(R.layout.activity_subir_foto);

        imgSeleccionada = findViewById(R.id.imagenSeleccionada);
        btnCamara = findViewById(R.id.btnAbrirCamara);
        btnRegistrarFinal = findViewById(R.id.btnRegistrarFinal);
        ImageButton btnBackFoto = findViewById(R.id.btnBack);

        btnBackFoto.setOnClickListener(v -> cargarVistaFormulario());

        btnCamara.setOnClickListener(v ->
                Toast.makeText(this, "Abrir cámara (a implementar)", Toast.LENGTH_SHORT).show());

        btnRegistrarFinal.setOnClickListener(v -> mostrarDialogoRegistroExitoso());
    }

    private void mostrarDialogoRegistroExitoso() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_registro_exitoso);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        new Handler().postDelayed(() -> {
            dialog.dismiss();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }, 2500);
    }

    private void mostrarCalendario() {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fecha = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    etFechaNacimiento.setText(fecha);
                },
                anio, mes, dia
        );
        datePicker.show();
    }

    private boolean formularioEsValido() {
        String correo = etCorreo.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String tipoDoc = spinnerDocumento.getSelectedItem().toString();
        String numDoc = etDocumento.getText().toString().trim();

        return
                !etNombres.getText().toString().trim().isEmpty() &&
                        !etApellidos.getText().toString().trim().isEmpty() &&
                        validarNumeroDocumento(tipoDoc, numDoc) &&
                        !etFechaNacimiento.getText().toString().trim().isEmpty() &&
                        !correo.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(correo).matches() &&
                        !telefono.isEmpty() && telefono.length() >= 9 &&
                        !etDomicilio.getText().toString().trim().isEmpty() &&
                        !contrasena.isEmpty() && evaluarNivelContrasena(contrasena) >= 50;
    }

    private boolean validarNumeroDocumento(String tipo, String valor) {
        switch (tipo) {
            case "DNI":
                return valor.matches("^\\d{8}$");
            case "Carné de Extranjería":
                return valor.matches("^(001|002|003|004|005|006|007|008|009)\\d{6}$");
            case "Pasaporte peruano":
                return valor.matches("^[A-Z]{2}\\d{6}$");
            default:
                return false;
        }
    }

    private void mostrarErroresFormulario() {
        String tipoDoc = spinnerDocumento.getSelectedItem().toString();
        String numDoc = etDocumento.getText().toString().trim();

        if (etNombres.getText().toString().trim().isEmpty())
            etNombres.setError("Ingresa tus nombres");
        if (etApellidos.getText().toString().trim().isEmpty())
            etApellidos.setError("Ingresa tus apellidos");
        if (!validarNumeroDocumento(tipoDoc, numDoc))
            etDocumento.setError("Formato inválido para " + tipoDoc);
        if (etFechaNacimiento.getText().toString().trim().isEmpty())
            etFechaNacimiento.setError("Selecciona tu fecha de nacimiento");

        String correo = etCorreo.getText().toString().trim();
        if (correo.isEmpty())
            etCorreo.setError("Ingresa tu correo");
        else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches())
            etCorreo.setError("Correo no válido");

        String telefono = etTelefono.getText().toString().trim();
        if (telefono.isEmpty())
            etTelefono.setError("Ingresa tu teléfono");
        else if (telefono.length() < 9)
            etTelefono.setError("Debe tener mínimo 9 dígitos");

        if (etDomicilio.getText().toString().trim().isEmpty())
            etDomicilio.setError("Ingresa tu domicilio");

        String contrasena = etContrasena.getText().toString().trim();
        if (contrasena.isEmpty())
            etContrasena.setError("Ingresa tu contraseña");
        else if (evaluarNivelContrasena(contrasena) < 50)
            etContrasena.setError("Contraseña no segura");
    }

    private void evaluarSeguridadContrasena(String password) {
        int nivel = evaluarNivelContrasena(password);
        pbSeguridad.setProgress(nivel);

        if (nivel < 50) {
            tvNivelSeguridad.setText("Débil");
            tvNivelSeguridad.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            pbSeguridad.setProgressTintList(getColorStateList(android.R.color.holo_red_light));
        } else if (nivel < 75) {
            tvNivelSeguridad.setText("Media");
            tvNivelSeguridad.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            pbSeguridad.setProgressTintList(getColorStateList(android.R.color.holo_orange_light));
        } else {
            tvNivelSeguridad.setText("Fuerte");
            tvNivelSeguridad.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            pbSeguridad.setProgressTintList(getColorStateList(android.R.color.holo_green_light));
        }
    }

    private int evaluarNivelContrasena(String password) {
        int nivel = 0;
        if (password.length() >= 8) nivel += 25;
        if (password.matches(".*[0-9].*")) nivel += 25;
        if (password.matches(".*[A-Z].*")) nivel += 25;
        if (password.matches(".*[!@#$%^&*()_+=~`?<>\\[\\]{}].*")) nivel += 25;
        return nivel;
    }
}
