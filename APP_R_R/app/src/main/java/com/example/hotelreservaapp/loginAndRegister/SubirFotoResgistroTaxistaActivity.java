package com.example.hotelreservaapp.loginAndRegister;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.ActivitySubirFotoResgistroTaxistaBinding;
import com.example.hotelreservaapp.model.PostulacionTaxista;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

public class SubirFotoResgistroTaxistaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ActivitySubirFotoResgistroTaxistaBinding binding;
    private Uri imageUri;

    // Datos recibidos
    private String nombres, apellidos, tipoDocumento, numeroDocumento;
    private String fechaNacimiento, correo, telefono, direccion, fotoPerfilUri;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubirFotoResgistroTaxistaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();

        // Recuperar datos del intent
        Intent intent = getIntent();
        nombres = intent.getStringExtra("nombres");
        apellidos = intent.getStringExtra("apellidos");
        tipoDocumento = intent.getStringExtra("tipoDocumento");
        numeroDocumento = intent.getStringExtra("numeroDocumento");
        fechaNacimiento = intent.getStringExtra("fechaNacimiento");
        correo = intent.getStringExtra("correo");
        telefono = intent.getStringExtra("telefono");
        direccion = intent.getStringExtra("direccion");
        fotoPerfilUri = intent.getStringExtra("fotoPerfilUri"); // <- ✅ Foto del rostro

        // Botón volver
        binding.btnBack.setOnClickListener(v -> onBackPressed());

        // Abrir galería
        binding.btnSeleccionarGaleria.setOnClickListener(v -> openGallery());

        // Registrar postulación
        binding.btnRegistrarVehiculo.setOnClickListener(v -> {
            if (validarFormulario()) {
                enviarPostulacionTaxista();
            }
        });
    }

    // Abrir galería para seleccionar foto
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    // Resultado de la selección de imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                binding.imagenSeleccionada.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Validar campos antes de enviar
    private boolean validarFormulario() {
        boolean valido = true;

        String placa = binding.etPlaca.getText().toString().trim();
        if (placa.isEmpty() || !placa.matches("[A-Za-z0-9]+")) {
            binding.tilPlaca.setError("Placa inválida. Solo letras y números.");
            valido = false;
        } else {
            binding.tilPlaca.setError(null);
        }

        if (imageUri == null) {
            Toast.makeText(this, "Por favor, sube una foto del vehículo", Toast.LENGTH_SHORT).show();
            valido = false;
        }

        return valido;
    }

    // Crear objeto y enviarlo a Firestore
    private void enviarPostulacionTaxista() {
        Toast.makeText(this, "Enviando postulación...", Toast.LENGTH_SHORT).show();

        PostulacionTaxista postulacion = new PostulacionTaxista();
        postulacion.setNombres(nombres);
        postulacion.setApellidos(apellidos);
        postulacion.setTipoDocumento(tipoDocumento);
        postulacion.setNumeroDocumento(numeroDocumento);
        postulacion.setFechaNacimiento(fechaNacimiento);
        postulacion.setCorreo(correo);
        postulacion.setTelefono(telefono);
        postulacion.setDireccion(direccion);
        postulacion.setNumeroPlaca(binding.etPlaca.getText().toString().trim());
        postulacion.setFotoPlacaURL(imageUri.toString()); // Foto vehículo
        postulacion.setUrlFotoPerfil(fotoPerfilUri);       // Foto rostro
        postulacion.setEstadoSolicitud("pendiente");

        firestore.collection("postulacionesTaxistas")
                .add(postulacion)
                .addOnSuccessListener(documentReference -> {
                    mostrarDialogoRegistroExitosoTaxista(
                            nombres, apellidos, tipoDocumento, numeroDocumento,
                            fechaNacimiento, correo, telefono, direccion,
                            binding.etPlaca.getText().toString().trim(),
                            imageUri.toString()
                    );
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al enviar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void mostrarDialogoRegistroExitosoTaxista(String nombres, String apellidos, String tipoDocumento, String numeroDocumento,
                                                      String fechaNacimiento, String correo, String telefono, String direccion,
                                                      String placa, String fotoVehiculo) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_registro_exitoso_taxista);
        builder.setCancelable(false);

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        new android.os.Handler().postDelayed(() -> {
            dialog.dismiss();
            // Redirigir al login o pantalla inicial
            startActivity(new Intent(SubirFotoResgistroTaxistaActivity.this, InicioActivity.class));
            finish();
        }, 3500); // 2.5 segundos
    }

}
