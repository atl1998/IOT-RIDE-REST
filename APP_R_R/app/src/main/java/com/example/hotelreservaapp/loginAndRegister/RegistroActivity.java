package com.example.hotelreservaapp.loginAndRegister;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.ActivityRegistroBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegistroActivity extends AppCompatActivity {

    private ActivityRegistroBinding binding;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Botón atrás
        binding.btnBack.setOnClickListener(v -> finish());

        // Acción continuar
        binding.btnContinuar.setOnClickListener(v -> {
            if (validarFormulario()) {
                String nombreUsuario = binding.etNombres.getText().toString().trim();
                Toast.makeText(RegistroActivity.this,
                        nombreUsuario + ", sube una foto tuya",
                        Toast.LENGTH_SHORT).show();
                cargarVistaSubirFoto();
            }
        });

        configurarCampoDocumento(InputType.TYPE_CLASS_NUMBER, 8, "Número de DNI");
        binding.chipGroupTipoDoc.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipDni) {
                configurarCampoDocumento(InputType.TYPE_CLASS_NUMBER, 8, "Número de DNI");
            } else if (checkedId == R.id.chipCarnet) {
                configurarCampoDocumento(InputType.TYPE_CLASS_NUMBER, 12, "Número de carnet");
            } else if (checkedId == R.id.chipPasaporte) {
                configurarCampoDocumento(InputType.TYPE_CLASS_TEXT, 12, "Número de pasaporte");
            }
        });

        configurarCampoFechaNacimiento();
    }

    private void configurarCampoDocumento(int inputType, int maxLength, String nuevoHint) {
        binding.etDocumento.setText("");
        binding.etDocumento.setInputType(inputType);
        binding.etDocumento.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        binding.tilDocumento.setHint(nuevoHint);
    }

    private void cargarVistaSubirFoto() {
        Intent intent = new Intent(RegistroActivity.this,UsuarioSubirFoto.class);
        intent.putExtra("nombres", binding.etNombres.getText().toString());
        intent.putExtra("apellidos", binding.etApellidos.getText().toString());

        int checkedId = binding.chipGroupTipoDoc.getCheckedChipId();
        String tipoDocumento = "";
        if (checkedId == R.id.chipDni) tipoDocumento = "DNI";
        else if (checkedId == R.id.chipCarnet) tipoDocumento = "Carnet";
        else if (checkedId == R.id.chipPasaporte) tipoDocumento = "Pasaporte";

        intent.putExtra("tipoDocumento", tipoDocumento);
        intent.putExtra("numeroDocumento", binding.etDocumento.getText().toString());
        intent.putExtra("fechaNacimiento", binding.etFechaNacimiento.getText().toString());
        intent.putExtra("correo", binding.etCorreo.getText().toString());
        intent.putExtra("telefono", binding.etTelefono.getText().toString());
        intent.putExtra("direccion", binding.etDomicilio.getText().toString());

        startActivity(intent);
    }

    private boolean validarFormulario() {
        boolean valido = true;

        String nombres = binding.etNombres.getText().toString().trim();
        String apellidos = binding.etApellidos.getText().toString().trim();
        String correo = binding.etCorreo.getText().toString().trim();
        String telefono = binding.etTelefono.getText().toString().trim();
        String documento = binding.etDocumento.getText().toString().trim();
        int checkedId = binding.chipGroupTipoDoc.getCheckedChipId();
        String direccion = binding.etDomicilio.getText().toString().trim();
        String fecha = binding.etFechaNacimiento.getText().toString().trim();

        if (nombres.isEmpty()) {
            binding.tilNombres.setError("Campo obligatorio");
            valido = false;
        } else binding.tilNombres.setError(null);

        if (apellidos.isEmpty()) {
            binding.tilApellidos.setError("Campo obligatorio");
            valido = false;
        } else binding.tilApellidos.setError(null);

        if (checkedId == R.id.chipDni && documento.length() != 8) {
            binding.tilDocumento.setError("DNI debe tener 8 dígitos");
            valido = false;
        } else if (checkedId == R.id.chipCarnet && documento.length() < 9) {
            binding.tilDocumento.setError("Carnet inválido");
            valido = false;
        } else if (checkedId == R.id.chipPasaporte && documento.length() < 6) {
            binding.tilDocumento.setError("Pasaporte inválido");
            valido = false;
        } else binding.tilDocumento.setError(null);

        if (correo.isEmpty()) {
            binding.tilCorreo.setError("Campo obligatorio");
            valido = false;
        } else if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            binding.tilCorreo.setError("Correo inválido");
            valido = false;
        } else binding.tilCorreo.setError(null);

        if (telefono.length() != 9) {
            binding.tilTelefono.setError("Teléfono debe tener 9 dígitos");
            valido = false;
        } else binding.tilTelefono.setError(null);

        if (direccion.isEmpty()) {
            binding.tilDomicilio.setError("Campo obligatorio");
            valido = false;
        } else binding.tilDomicilio.setError(null);

        if (fecha.isEmpty()) {
            binding.tilFechaNacimiento.setError("Selecciona una fecha");
            valido = false;
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                sdf.setLenient(false);
                Date fechaNacimiento = sdf.parse(fecha);
                Date hoy = new Date();

                if (fechaNacimiento.after(hoy)) {
                    binding.tilFechaNacimiento.setError("La fecha no puede ser en el futuro");
                    valido = false;
                } else {
                    Calendar nacimiento = Calendar.getInstance();
                    nacimiento.setTime(fechaNacimiento);
                    Calendar actual = Calendar.getInstance();

                    int edad = actual.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR);
                    if (actual.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) edad--;

                    if (edad < 18) {
                        binding.tilFechaNacimiento.setError("Debes tener al menos 18 años");
                        valido = false;
                    } else if (edad > 65) {
                        binding.tilFechaNacimiento.setError("Edad máxima permitida es 65 años");
                        valido = false;
                    } else {
                        binding.tilFechaNacimiento.setError(null);
                    }
                }

            } catch (Exception e) {
                binding.tilFechaNacimiento.setError("Fecha inválida");
                valido = false;
            }
        }

        return valido;
    }

    private void configurarCampoFechaNacimiento() {
        binding.etFechaNacimiento.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    RegistroActivity.this,
                    R.style.CustomDatePickerDialog,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String fechaFormateada = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                selectedDay, selectedMonth + 1, selectedYear);
                        binding.etFechaNacimiento.setText(fechaFormateada);
                    },
                    year, month, day
            );

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }
}
