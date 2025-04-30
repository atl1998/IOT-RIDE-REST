package com.example.hotelreservaapp.superadmin;

import android.os.Bundle;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.SuperadminRegistrarAdmHotelActivityBinding;
import com.example.hotelreservaapp.databinding.SuperadminRegistrarTaxistaActivityBinding;
import java.util.Calendar;
import java.util.Locale;


public class RegistrarAdmHotelActivity extends AppCompatActivity {
    private SuperadminRegistrarAdmHotelActivityBinding binding;
    private final Calendar calendar = Calendar.getInstance();

    // 👉 Declaramos el launcher moderno para abrir galería
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    binding.imagePreview.setVisibility(View.VISIBLE);
                    binding.imagePreview.setImageURI(selectedImageUri);
                    binding.btnSubirFoto.setVisibility(View.GONE);   // ocultar "Subir foto"
                    binding.btnEditarFoto.setVisibility(View.VISIBLE); // mostrar "Editar foto"
                    Toast.makeText(this, "Imagen seleccionada correctamente", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SuperadminRegistrarAdmHotelActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configurarBotonBack();
        configurarCampoFechaNacimiento();
        //Modificar pa mas adelante, ya que solo se redirige a la vista anterior owo
        configurarBotonRegistro();
        binding.btnSubirFoto.setOnClickListener(v -> lanzarSelectorDeImagen());
        binding.btnEditarFoto.setOnClickListener(v -> lanzarSelectorDeImagen());

        //Lógica para manejar los tipos de documento
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


    }

    // Botón de retroceso
    private void configurarBotonBack() {
        binding.btnBack.setOnClickListener(v -> finish());
    }
    private void configurarCampoDocumento(int inputType, int maxLength, String nuevoHint) {
        binding.etNumDoc.setText(""); // limpia el campo al cambiar tipo
        binding.etNumDoc.setInputType(inputType);
        binding.etNumDoc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        binding.tilNumDoc.setHint(nuevoHint);
    }

    //Change soon
    private void configurarBotonRegistro() {
        binding.btnRegistrar.setOnClickListener(v ->{
                // Validar campos
                if (!validarFormulario()) return;

                // Aquí iría lógica de registro (guardar en base de datos o backend)
                Toast.makeText(this, "Formulario válido. Registrando...", Toast.LENGTH_SHORT).show();
                finish();
        });
    }

    private boolean validarFormulario() {
        boolean valido = true;

        String nombres = binding.etNombres.getText().toString().trim();
        String apellidos = binding.etApellidos.getText().toString().trim();
        String correo = binding.etCorreo.getText().toString().trim();
        String telefono = binding.etTelefono.getText().toString().trim();
        String documento = binding.etNumDoc.getText().toString().trim();
        int checkedId = binding.chipGroupTipoDoc.getCheckedChipId();
        String direccion = binding.etDomicilio.getText().toString().trim();
        String fecha = binding.etFechaNacimiento.getText().toString().trim();

        if (nombres.isEmpty()) {
            binding.tilNombres.setError("Campo obligatorio");
            valido = false;
        } else {
            binding.tilNombres.setError(null);
        }

        if (apellidos.isEmpty()) {
            binding.tilApellidos.setError("Campo obligatorio");
            valido = false;
        } else {
            binding.tilApellidos.setError(null);
        }
        if (checkedId == R.id.chipDni && documento.length() != 8) {
            binding.tilNumDoc.setError("DNI debe tener 8 dígitos");
            valido = false;
        } else if (checkedId == R.id.chipCarnet && documento.length() < 9) {
            binding.tilNumDoc.setError("Carnet inválido");
            valido = false;
        } else if (checkedId == R.id.chipPasaporte && documento.length() < 6) {
            binding.tilNumDoc.setError("Pasaporte inválido");
            valido = false;
        } else {
            binding.tilNumDoc.setError(null);
        }
        if (correo.isEmpty()) {
            binding.tilCorreo.setError("Campo obligatorio");
            valido = false;
        }else{
            binding.tilCorreo.setError(null);

        }
        if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            binding.tilCorreo.setError("Correo inválido");
            valido = false;
        } else {
            binding.tilCorreo.setError(null);
        }

        if (telefono.length() != 9) {
            binding.tilTelefono.setError("Teléfono debe tener 9 dígitos");
            valido = false;
        } else {
            binding.tilTelefono.setError(null);
        }
        if (direccion.isEmpty()) {
            binding.tilDomicilio.setError("Campo obligatorio");
            valido = false;
        } else {
            binding.tilDomicilio.setError(null);
        }

        if (fecha.isEmpty()) {
            binding.tilFechaNacimiento.setError("Selecciona una fecha");
            valido = false;
        } else {
            binding.tilFechaNacimiento.setError(null);
        }

        return valido;
    }


    // Lógica de campo Fecha de Nacimiento
    private void configurarCampoFechaNacimiento() {
        binding.etFechaNacimiento.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    RegistrarAdmHotelActivity.this,
                    R.style.CustomDatePickerDialog, // ← estilo
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String fechaFormateada = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                selectedDay, selectedMonth + 1, selectedYear);
                        binding.etFechaNacimiento.setText(fechaFormateada);
                    },
                    year, month, day
            );
            //Bloquea fechas futuras
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            datePickerDialog.show();
        });
    }

    // Subir foto desde galería
    private void lanzarSelectorDeImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }
}