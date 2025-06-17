package com.example.hotelreservaapp.loginAndRegister;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.ActivityPostulacionTaxistaBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PostulacionTaxistaActivity extends AppCompatActivity {

    private ActivityPostulacionTaxistaBinding binding;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostulacionTaxistaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Acción para el botón de regresar
        binding.btnBack.setOnClickListener(v -> onBackPressed());

        // Acción para el botón de continuar
        binding.btnContinuar.setOnClickListener(v -> {
            if (validarFormulario()) {
                // Obtener el nombre del usuario
                String nombreUsuario = binding.etNombres.getText().toString().trim();

                // Mostrar el Toast de validación
                Toast.makeText(PostulacionTaxistaActivity.this,
                        nombreUsuario + ", sube una foto tuya",
                        Toast.LENGTH_SHORT).show();

                // Cargar la siguiente vista (SubirFotoResgistroTaxistaActivity)
                cargarVistaSubirFoto();
            }
        });

        // Lógica para manejar los tipos de documento
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

        // Configurar el calendario para la fecha de nacimiento
        configurarCampoFechaNacimiento();
    }

    private void configurarBotonBack() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void configurarCampoDocumento(int inputType, int maxLength, String nuevoHint) {
        binding.etDocumento.setText(""); // limpia el campo al cambiar tipo
        binding.etDocumento.setInputType(inputType);
        binding.etDocumento.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        binding.tilDocumento.setHint(nuevoHint);
    }

    // Metodo para cargar la siguiente vista (SubirFotoResgistroTaxistaActivity)

    private void cargarVistaSubirFoto() {
        // Cambiar a la actividad o vista de subir foto
        Intent intent = new Intent(PostulacionTaxistaActivity.this, SubirFotoPersonalTaxista.class);

        // Recoger los datos generales
        intent.putExtra("nombres", binding.etNombres.getText().toString());
        intent.putExtra("apellidos", binding.etApellidos.getText().toString());

        // Recoger el tipo de documento correctamente desde el ChipGroup
        int checkedId = binding.chipGroupTipoDoc.getCheckedChipId();
        String tipoDocumento = "";
        if (checkedId == R.id.chipDni) {
            tipoDocumento = "DNI";
        } else if (checkedId == R.id.chipCarnet) {
            tipoDocumento = "Carnet";
        } else if (checkedId == R.id.chipPasaporte) {
            tipoDocumento = "Pasaporte";
        }

        intent.putExtra("tipoDocumento", tipoDocumento);
        intent.putExtra("numeroDocumento", binding.etDocumento.getText().toString());
        intent.putExtra("fechaNacimiento", binding.etFechaNacimiento.getText().toString());
        intent.putExtra("correo", binding.etCorreo.getText().toString());
        intent.putExtra("telefono", binding.etTelefono.getText().toString());
        intent.putExtra("direccion", binding.etDomicilio.getText().toString());

        startActivity(intent);
    }

    // Validar todos los campos
    private boolean validarFormulario() {
        boolean valido = true;

        // Recuperar los valores de los campos
        String nombres = binding.etNombres.getText().toString().trim();
        String apellidos = binding.etApellidos.getText().toString().trim();
        String correo = binding.etCorreo.getText().toString().trim();
        String telefono = binding.etTelefono.getText().toString().trim();
        String documento = binding.etDocumento.getText().toString().trim();
        int checkedId = binding.chipGroupTipoDoc.getCheckedChipId();
        String direccion = binding.etDomicilio.getText().toString().trim();
        String fecha = binding.etFechaNacimiento.getText().toString().trim();

        // Validar nombres
        if (nombres.isEmpty()) {
            binding.tilNombres.setError("Campo obligatorio");
            valido = false;
        } else {
            binding.tilNombres.setError(null);
        }

        // Validar apellidos
        if (apellidos.isEmpty()) {
            binding.tilApellidos.setError("Campo obligatorio");
            valido = false;
        } else {
            binding.tilApellidos.setError(null);
        }

        // Validar documento
        if (checkedId == R.id.chipDni && documento.length() != 8) {
            binding.tilDocumento.setError("DNI debe tener 8 dígitos");
            valido = false;
        } else if (checkedId == R.id.chipCarnet && documento.length() < 9) {
            binding.tilDocumento.setError("Carnet inválido");
            valido = false;
        } else if (checkedId == R.id.chipPasaporte && documento.length() < 6) {
            binding.tilDocumento.setError("Pasaporte inválido");
            valido = false;
        } else {
            binding.tilDocumento.setError(null);
        }

        // Validar correo
        if (correo.isEmpty()) {
            binding.tilCorreo.setError("Campo obligatorio");
            valido = false;
        } else if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            binding.tilCorreo.setError("Correo inválido");
            valido = false;
        } else {
            binding.tilCorreo.setError(null);
        }

        // Validar teléfono
        if (telefono.length() != 9) {
            binding.tilTelefono.setError("Teléfono debe tener 9 dígitos");
            valido = false;
        } else {
            binding.tilTelefono.setError(null);
        }

        // Validar domicilio
        if (direccion.isEmpty()) {
            binding.tilDomicilio.setError("Campo obligatorio");
            valido = false;
        } else {
            binding.tilDomicilio.setError(null);
        }

        // Validar fecha
        if (fecha.isEmpty()) {
            binding.tilFechaNacimiento.setError("Selecciona una fecha");
            valido = false;
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                sdf.setLenient(false); // Validación estricta
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
                    if (actual.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
                        edad--; // Aún no cumple años este año
                    }

                    if (edad < 18) {
                        binding.tilFechaNacimiento.setError("Debes tener al menos 18 años");
                        valido = false;
                    } else if (edad > 65) {
                        binding.tilFechaNacimiento.setError("La edad máxima permitida es 65 años");
                        valido = false;
                    } else {
                        binding.tilFechaNacimiento.setError(null); // Todo correcto
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
            // Obtener la fecha actual
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Crear y mostrar el DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    PostulacionTaxistaActivity.this,
                    R.style.CustomDatePickerDialog, // ← Aquí puedes agregar un estilo personalizado si lo deseas
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Formatear la fecha seleccionada
                        String fechaFormateada = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                selectedDay, selectedMonth + 1, selectedYear);
                        binding.etFechaNacimiento.setText(fechaFormateada); // Mostrar la fecha seleccionada
                    },
                    year, month, day
            );

            // Bloquear fechas futuras
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            // Mostrar el DatePickerDialog
            datePickerDialog.show();
        });
    }
}
