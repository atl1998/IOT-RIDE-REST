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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class SubirFotoResgistroTaxistaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ActivitySubirFotoResgistroTaxistaBinding binding;
    private Uri imageUri;

    // Datos recibidos
    private String nombres, apellidos, tipoDocumento, numeroDocumento;
    private String fechaNacimiento, correo, telefono, direccion, fotoPerfilUri;

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubirFotoResgistroTaxistaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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
// Crear documento en blanco para obtener el ID
        firestore.collection("postulacionesTaxistas")
                .add(new PostulacionTaxista()) // temporal
                .addOnSuccessListener(documentReference -> {
                    String uid = documentReference.getId();

                    StorageReference refFoto = storage.getReference().child("fotos_postulaciones/foto_" + uid + ".jpg");
                    StorageReference refPlaca = storage.getReference().child("fotos_postulaciones/placa_" + uid + ".jpg");

                    // Subir foto del rostro
                    refFoto.putFile(Uri.parse(fotoPerfilUri))
                            .addOnSuccessListener(task1 -> refFoto.getDownloadUrl().addOnSuccessListener(uriFoto -> {
                                // Subir foto del vehículo
                                refPlaca.putFile(imageUri)
                                        .addOnSuccessListener(task2 -> refPlaca.getDownloadUrl().addOnSuccessListener(uriPlaca -> {
                                            // Finalmente, actualizamos el documento
                                            guardarPostulacion(uid, uriFoto.toString(), uriPlaca.toString());
                                        }))
                                        .addOnFailureListener(e -> Toast.makeText(this, "Error al subir foto del vehículo", Toast.LENGTH_SHORT).show());
                            }))
                            .addOnFailureListener(e -> Toast.makeText(this, "Error al subir foto del rostro", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al iniciar postulación: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void guardarPostulacion(String uid, String urlFotoPerfil, String urlFotoPlaca) {
        String placa = binding.etPlaca.getText().toString().trim();

        PostulacionTaxista postulacion = new PostulacionTaxista();
        postulacion.setNombres(nombres);
        postulacion.setApellidos(apellidos);
        postulacion.setTipoDocumento(tipoDocumento);
        postulacion.setNumeroDocumento(numeroDocumento);
        postulacion.setFechaNacimiento(fechaNacimiento);
        postulacion.setCorreo(correo);
        postulacion.setTelefono(telefono);
        postulacion.setDireccion(direccion);
        postulacion.setNumeroPlaca(placa);
        postulacion.setUrlFotoPerfil(urlFotoPerfil);
        postulacion.setFotoPlacaURL(urlFotoPlaca);
        postulacion.setEstadoSolicitud("pendiente");

        firestore.collection("postulacionesTaxistas")
                .document(uid)
                .set(postulacion)
                .addOnSuccessListener(unused -> mostrarDialogoRegistroExitosoTaxista(
                        nombres, apellidos, tipoDocumento, numeroDocumento,
                        fechaNacimiento, correo, telefono, direccion,
                        placa, urlFotoPlaca
                ))
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar postulación final: " + e.getMessage(), Toast.LENGTH_LONG).show());
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
