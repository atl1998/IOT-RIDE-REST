package com.example.hotelreservaapp.loginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.ActivityCrearContrasenaTaxistaBinding;

public class CrearContrasenaTaxistaActivity extends AppCompatActivity {

    private ActivityCrearContrasenaTaxistaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCrearContrasenaTaxistaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Recuperar todos los datos pasados desde la actividad anterior
        Intent intent = getIntent();
        String nombres = intent.getStringExtra("nombres");
        String apellidos = intent.getStringExtra("apellidos");
        String tipoDocumento = intent.getStringExtra("tipoDocumento");
        String numeroDocumento = intent.getStringExtra("numeroDocumento");
        String fechaNacimiento = intent.getStringExtra("fechaNacimiento");
        String correo = intent.getStringExtra("correo");
        String telefono = intent.getStringExtra("telefono");
        String direccion = intent.getStringExtra("direccion");
        String placa = intent.getStringExtra("placa");
        String fotoVehiculo = intent.getStringExtra("fotoVehiculo");

        // Acción para el botón de regresar
        binding.btnBack.setOnClickListener(v -> onBackPressed());

        // Añadir TextWatcher para validar la contraseña en tiempo real
        binding.etNuevaContrasena.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validarContraseñaEnTiempoReal(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Acción para el botón de finalizar registro
        binding.btnFinalizarRegistro.setOnClickListener(v -> {
            String nuevaContrasena = binding.etNuevaContrasena.getText().toString().trim();
            String confirmarContrasena = binding.etConfirmarContrasena.getText().toString().trim();

            // Validación de las contraseñas
            if (validarContraseñas(nuevaContrasena, confirmarContrasena)) {
                // Mostrar el diálogo de registro exitoso para el taxista
                mostrarDialogoRegistroExitosoTaxista(nombres, apellidos, tipoDocumento, numeroDocumento, fechaNacimiento, correo, telefono, direccion, placa, fotoVehiculo, nuevaContrasena);
            } else {
                // Si las contraseñas no coinciden o son inválidas
                Toast.makeText(this, "Las contraseñas no coinciden o son inválidas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Validar que las contraseñas no estén vacías y coincidan
    private boolean validarContraseñas(String nuevaContrasena, String confirmarContrasena) {
        if (nuevaContrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Validar los 4 requisitos de seguridad de la contraseña en tiempo real
    private void validarContraseñaEnTiempoReal(String contrasena) {
        boolean tieneMinimo8Caracteres = contrasena.length() >= 8;
        boolean tieneLetraMayuscula = contrasena.matches(".*[A-Z].*");
        boolean tieneLetraMinuscula = contrasena.matches(".*[a-z].*");
        boolean tieneNumero = contrasena.matches(".*\\d.*");

        // Cambiar el color del texto y los indicadores de seguridad según el estado de los requisitos
        if (tieneMinimo8Caracteres) {
            binding.tvIndicador1.setTextColor(getResources().getColor(R.color.green));
        } else {
            binding.tvIndicador1.setTextColor(getResources().getColor(R.color.grey));
        }

        if (tieneLetraMayuscula) {
            binding.tvIndicador2.setTextColor(getResources().getColor(R.color.green));
        } else {
            binding.tvIndicador2.setTextColor(getResources().getColor(R.color.grey));
        }

        if (tieneLetraMinuscula) {
            binding.tvIndicador3.setTextColor(getResources().getColor(R.color.green));
        } else {
            binding.tvIndicador3.setTextColor(getResources().getColor(R.color.grey));
        }

        if (tieneNumero) {
            binding.tvIndicador4.setTextColor(getResources().getColor(R.color.green));
        } else {
            binding.tvIndicador4.setTextColor(getResources().getColor(R.color.grey));
        }
    }

    // Mostrar el diálogo de registro exitoso para el taxista
    private void mostrarDialogoRegistroExitosoTaxista(String nombres, String apellidos, String tipoDocumento, String numeroDocumento,
                                                      String fechaNacimiento, String correo, String telefono, String direccion,
                                                      String placa, String fotoVehiculo, String nuevaContrasena) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_registro_exitoso_taxista);  // El diseño de tu diálogo para el taxista

        builder.setCancelable(false);
        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        // Redirigir al inicio después de 2.5 segundos
        new android.os.Handler().postDelayed(() -> {
            dialog.dismiss();
            // Aquí, pasamos todos los datos necesarios para el registro en Firebase
            cargarVistaRegistroTaxista(nombres, apellidos, tipoDocumento, numeroDocumento, fechaNacimiento, correo, telefono, direccion, placa, fotoVehiculo, nuevaContrasena);
        }, 2500);  // Espera 2.5 segundos antes de redirigir
    }

    // Metodo para cargar la siguiente vista (RegistroTaxistaActivity)
    private void cargarVistaRegistroTaxista(String nombres, String apellidos, String tipoDocumento, String numeroDocumento,
                                            String fechaNacimiento, String correo, String telefono, String direccion,
                                            String placa, String fotoVehiculo, String nuevaContrasena) {
        // Cambiar a la actividad de RegistroTaxistaActivity
        Intent intent = new Intent(CrearContrasenaTaxistaActivity.this, RegistroTaxistaActivity.class);

        // Pasar todos los datos a la siguiente actividad
        intent.putExtra("nombres", nombres);
        intent.putExtra("apellidos", apellidos);
        intent.putExtra("tipoDocumento", tipoDocumento);
        intent.putExtra("numeroDocumento", numeroDocumento);
        intent.putExtra("fechaNacimiento", fechaNacimiento);
        intent.putExtra("correo", correo);
        intent.putExtra("telefono", telefono);
        intent.putExtra("direccion", direccion);
        intent.putExtra("placa", placa);
        intent.putExtra("fotoVehiculo", fotoVehiculo);
        intent.putExtra("nuevaContrasena", nuevaContrasena); // Pasar también la contraseña

        // Iniciar la actividad de registro final
        startActivity(intent);
    }
}
