package com.example.hotelreservaapp.superadmin;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.example.hotelreservaapp.LogManager;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.SuperadminRegistrarAdmHotelActivityBinding;
import com.example.hotelreservaapp.model.Notificacion;
import com.example.hotelreservaapp.model.Usuario;
import com.example.hotelreservaapp.room.AppDatabase;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;


public class RegistrarAdmHotelActivity extends AppCompatActivity {
    private SuperadminRegistrarAdmHotelActivityBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
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

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

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
        binding.btnRegistrar.setOnClickListener(v -> {
            if (!validarFormulario()) return;

            String uidSuperadmin = FirebaseAuth.getInstance().getCurrentUser().getUid();  // 👉 Guardar UID antes de crear usuario

            // 1. Obtener datos del formulario
            String nombres = binding.etNombres.getText().toString().trim();
            String apellidos = binding.etApellidos.getText().toString().trim();
            String correo = binding.etCorreo.getText().toString().trim();
            String telefono = binding.etTelefono.getText().toString().trim();
            String documento = binding.etNumDoc.getText().toString().trim();
            String direccion = binding.etDomicilio.getText().toString().trim();
            String fechaNacimiento = binding.etFechaNacimiento.getText().toString().trim();

            String tipoDocumento;
            int checkedId = binding.chipGroupTipoDoc.getCheckedChipId();
            if (checkedId == R.id.chipDni) tipoDocumento = "DNI";
            else if (checkedId == R.id.chipCarnet) tipoDocumento = "Carné de Extranjería";
            else tipoDocumento = "Pasaporte";

            // 2. Contraseña temporal aleatoria
            String contrasenaTemporal = UUID.randomUUID().toString().substring(0, 10);

            // 3. Firebase
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            mAuth.createUserWithEmailAndPassword(correo, contrasenaTemporal)
                    .addOnSuccessListener(authResult -> {
                        String uid = authResult.getUser().getUid();

                        Usuario usuario = new Usuario(
                                nombres,
                                apellidos,
                                "adminHotel",
                                tipoDocumento,
                                documento,
                                fechaNacimiento,
                                correo,
                                telefono,
                                direccion,
                                "", // urlFotoPerfil
                                true,
                                true
                        );

                        firestore.collection("usuarios").document(uid)
                                .set(usuario)
                                .addOnSuccessListener(unused -> {
                                    String nombreCompleto = nombres + " " + apellidos;
                                    firestore.collection("usuarios").document(uidSuperadmin).get()
                                            .addOnSuccessListener(doc -> {
                                                if (doc.exists()) {
                                                    String nombreSuperadmin = doc.getString("nombre");
                                                    String apellidoSuperadmin = doc.getString("apellido");
                                                    String nombreCompletoSuperadmin = nombreSuperadmin + " " + apellidoSuperadmin;

                                                    LogManager.registrarLogRegistro(
                                                            nombreCompletoSuperadmin,
                                                            "Registro de administrador de hotel",
                                                            "El administrador de hotel " + nombreCompleto + " fue registrado en la plataforma"
                                                    );
                                                }
                                            });

                                    // 4. Notificación local
                                    Notificacion nueva = new Notificacion("Nuevo admin de hotel registrado",
                                            "Se ha registrado correctamente un nuevo administrador de hotel.",
                                            System.currentTimeMillis(), false);
                                    AppDatabase.getInstance(this).notificacionDao().insertar(nueva);

                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ChannelRideAndRest")
                                            .setSmallIcon(R.drawable.icon_notification)
                                            .setContentTitle(nueva.titulo)
                                            .setContentText(nueva.mensaje)
                                            .setPriority(NotificationCompat.PRIORITY_HIGH);

                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                                            == PackageManager.PERMISSION_GRANTED) {
                                        notificationManager.notify(1, builder.build());
                                    }

                                    // 5. Abrir cliente de correo para enviar acceso
                                    try {
                                        String uriText = "mailto:" + Uri.encode(correo) +
                                                "?subject=" + Uri.encode("Acceso a la plataforma HotelReservaApp") +
                                                "&body=" + Uri.encode(
                                                "Hola " + nombres + " " + apellidos +",\n\n" +
                                                        "Has sido registrado como administrador de hotel.\n\n" +
                                                        "📧 Correo: " + correo + "\n" +
                                                        "🔐 Contraseña temporal: " + contrasenaTemporal + "\n\n" +
                                                        "Por favor, inicia sesión en la app y cambia tu contraseña.\n\n" +
                                                        "Atentamente,\nEquipo de Ride&Rest");

                                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                                        intent.setData(Uri.parse(uriText));
                                        startActivity(Intent.createChooser(intent, "Enviar correo con..."));
                                    } catch (android.content.ActivityNotFoundException ex) {
                                        Toast.makeText(this, "No se encontró una app de correo.", Toast.LENGTH_SHORT).show();
                                    }
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(this, "Administrador registrado correctamente", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al guardar en Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al registrar usuario: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
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
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    RegistrarAdmHotelActivity.this,
                    R.style.CustomDatePickerDialog,  // si tienes estilo personalizado
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String fechaFormateada = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                selectedDay, selectedMonth + 1, selectedYear);
                        binding.etFechaNacimiento.setText(fechaFormateada);
                    },
                    year, month, day
            );

            // Limitar a fechas hasta hoy
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