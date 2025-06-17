package com.example.hotelreservaapp.superadmin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.SuperadminDetalleSolicitudActivityBinding;
import com.example.hotelreservaapp.model.DetallesTaxista;
import com.example.hotelreservaapp.model.Notificacion;
import com.example.hotelreservaapp.model.PostulacionTaxista;
import com.example.hotelreservaapp.model.SolicitudTaxista;
import com.example.hotelreservaapp.model.Usuario;
import com.example.hotelreservaapp.room.AppDatabase;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetalleSolicitudActivity extends AppCompatActivity  {
    private SuperadminDetalleSolicitudActivityBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private PostulacionTaxista solicitudActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SuperadminDetalleSolicitudActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Botón de volver
        binding.btnBack.setOnClickListener(v -> finish());

        // Obtener la solicitud enviada desde el intent
        solicitudActual = (PostulacionTaxista) getIntent().getSerializableExtra("solicitud");

        if (solicitudActual != null) {
            // Setear campos visibles en pantalla
            binding.etNombre.setText(solicitudActual.getNombres());
            binding.etApellido.setText(solicitudActual.getApellidos());
            binding.etTipoDoc.setText(solicitudActual.getTipoDocumento());
            binding.etNumDoc.setText(solicitudActual.getNumeroDocumento());
            binding.etFechaNacimiento.setText(solicitudActual.getFechaNacimiento());
            binding.etTelefono.setText(solicitudActual.getTelefono());
            binding.etDomicilio.setText(solicitudActual.getDireccion());
            binding.etCorreo.setText(solicitudActual.getCorreo());
            binding.etPlaca.setText(solicitudActual.getNumeroPlaca());

            // --- Carga de imágenes con Glide (CORREGIDO para URLs) ---
            // Ya no necesitas 'file:///android_asset/' ni bloques try-catch para IOException.
            // Glide maneja URLs nulas/vacías y fallos de carga con .error().
            Glide.with(this)
                    .load(solicitudActual.getUrlFotoPerfil()) // Carga la URL directamente
                    .placeholder(R.drawable.default_user_icon) // Imagen mientras carga
                    .error(R.drawable.default_user_icon) // Imagen si la URL es null/inválida/falla
                    .circleCrop()
                    .into(binding.imageFotoUsuario);

            Glide.with(this)
                    .load(solicitudActual.getFotoPlacaURL()) // Carga la URL directamente
                    .placeholder(R.drawable.placa_demo) // Imagen mientras carga
                    .error(R.drawable.placa_demo) // Imagen si la URL es null/inválida/falla
                    .into(binding.imageFotoPlaca);
        } else {
            Log.e("DetalleSolicitud", "NO SE RECIBIÓ LA SOLICITUD. Finalizando actividad.");
            Toast.makeText(this, "Error al cargar detalles de la solicitud.", Toast.LENGTH_LONG).show();
            finish(); // Cierra la actividad si no hay datos para mostrar
            return; // Salir del método onCreate
        }

        binding.btnHabilitar.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("¿Habilitar taxista?")
                    .setMessage("Esta acción permitirá al taxista usar la aplicación y creará su cuenta en el sistema.")
                    .setPositiveButton("Sí, Habilitar", (dialog, which) -> {
                        habilitarTaxista(solicitudActual);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        binding.btnRechazar.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("¿Rechazar solicitud?")
                    .setMessage("Esta acción cambiará el estado de la solicitud a 'rechazada'.")
                    .setPositiveButton("Sí, Rechazar", (dialog, which) -> {
                        rechazarSolicitud(solicitudActual);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }
    private void habilitarTaxista(PostulacionTaxista postulacion) {
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Habilitando taxista...");
        pDialog.setCancelable(false);
        pDialog.show();

        String email = postulacion.getCorreo();
        final String contrasenaTemporal = UUID.randomUUID().toString().substring(0, 12);

        firebaseAuth.createUserWithEmailAndPassword(email, contrasenaTemporal)
                .addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        FirebaseUser firebaseUser = authTask.getResult().getUser();
                        if (firebaseUser != null) {
                            String userUid = firebaseUser.getUid();

                            // 2. Crear objeto DetallesTaxista
                            // IMPORTANTE: Asegúrate de que el constructor de DetallesTaxista coincida con esto.
                            // Si tu clase DetallesTaxista tiene más campos obligatorios, inicialízalos aquí
                            // (pueden ser null o valores por defecto si no vienen de PostulacionTaxista).
                            DetallesTaxista driverDetails = new DetallesTaxista(
                                    // Los campos disponibles de la postulación
                                    postulacion.getNumeroPlaca(),
                                    postulacion.getFotoPlacaURL()
                                    /*
                                    // Ejemplo si DetallesTaxista tiene más campos:
                                    null, // numeroLicencia
                                    null, // tipoLicencia
                                    null, // fechaVencimientoLicencia
                                    postulacion.getNumeroPlaca(),
                                    postulacion.getFotoPlacaURL(),
                                    null, // modeloVehiculo
                                    null, // colorVehiculo
                                    0,    // anioVehiculo
                                    "activo", // estadoConductor
                                    0.0   // calificacionPromedio
                                    */
                            );

                            // 3. Crear objeto Usuario
                            // El constructor de Usuario DEBE coincidir con estos parámetros (orden y tipo).
                            Usuario nuevoUsuario = new Usuario(
                                    postulacion.getNombres(),
                                    postulacion.getApellidos(),
                                    "taxista", // Rol
                                    postulacion.getTipoDocumento(),
                                    postulacion.getNumeroDocumento(),
                                    postulacion.getFechaNacimiento(),
                                    postulacion.getCorreo(),
                                    postulacion.getTelefono(),
                                    postulacion.getDireccion(),
                                    postulacion.getUrlFotoPerfil(),
                                    true, // activo
                                    true  // requiereCambioContrasena
                            );

                            nuevoUsuario.setDriverDetails(driverDetails); // Asigna los detalles del taxista

                            // 4. Guardar el nuevo usuario en la colección "usuarios" usando el UID de Auth como ID de documento
                            WriteBatch batch = firestore.batch();
                            DocumentReference usuarioDocRef = firestore.collection("usuarios").document(userUid);
                            batch.set(usuarioDocRef, nuevoUsuario);

                            // 5. Actualizar la postulación original a "aprobado" y vincular con el UID
                            DocumentReference postulacionDocRef = firestore.collection("postulacionesTaxistas").document(solicitudActual.getId());
                            Map<String, Object> postulacionUpdates = new HashMap<>();
                            postulacionUpdates.put("estadoSolicitud", "aprobado");
                            postulacionUpdates.put("fechaRevision", FieldValue.serverTimestamp());
                            postulacionUpdates.put("idUsuarioAsignado", userUid); // Vincula la postulación con el UID del usuario creado
                            batch.update(postulacionDocRef, postulacionUpdates);

                            // Ejecutar el batch
                            batch.commit()
                                    .addOnSuccessListener(aVoid -> {
                                        pDialog.dismissWithAnimation();
                                        mostrarSweetDialogExito("Taxista habilitado exitosamente", "habilitado");

                                        // 6. Notificación local
                                        Notificacion nueva = new Notificacion("Nuevo taxista habilitado",
                                                "Se ha habilitado correctamente un nuevo taxista.",
                                                System.currentTimeMillis(), false);
                                        // Usa el hilo secundario para Room para evitar bloqueo de UI
                                        if (AppDatabase.getInstance(this) != null && AppDatabase.getInstance(this).notificacionDao() != null) {
                                            new Thread(() -> AppDatabase.getInstance(DetalleSolicitudActivity.this).notificacionDao().insertar(nueva)).start();
                                        }

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

                                        // 7. Abrir cliente de correo para enviar acceso
                                        try {
                                            String uriText = "mailto:" + Uri.encode(email) +
                                                    "?subject=" + Uri.encode("Acceso a la plataforma HotelReservaApp") +
                                                    "&body=" + Uri.encode(
                                                    "Hola " + postulacion.getNombres() + " " + postulacion.getApellidos() + ",\n\n" +
                                                            "¡Felicidades! Has sido habilitado como taxista en HotelReservaApp.\n\n" +
                                                            "Tus datos de acceso son:\n" +
                                                            "📧 Correo: " + email + "\n" +
                                                            "🔐 Contraseña temporal: " + contrasenaTemporal + "\n\n" +
                                                            "Por favor, inicia sesión en la app y cambia tu contraseña para mayor seguridad.\n\n" +
                                                            "Atentamente,\nEquipo de HotelReservaApp");

                                            Intent intentEmail = new Intent(Intent.ACTION_SENDTO);
                                            intentEmail.setData(Uri.parse(uriText));
                                            startActivity(Intent.createChooser(intentEmail, "Enviar correo con..."));
                                        } catch (android.content.ActivityNotFoundException ex) {
                                            Toast.makeText(this, "No se encontró una aplicación de correo.", Toast.LENGTH_SHORT).show();
                                            Log.e("DetalleSolicitud", "No email app found: " + ex.getMessage());
                                        }

                                        regresarALista("habilitado");
                                    })
                                    .addOnFailureListener(e -> {
                                        pDialog.dismissWithAnimation();
                                        mostrarSweetDialogError("Error al guardar datos del taxista o postulación: " + e.getMessage(), "error_habilitar");
                                        Log.e("DetalleSolicitud", "Error en batch de habilitación: ", e);
                                        // IMPORTANTE: Si falla el batch (Firestore), elimina el usuario de Auth para evitar cuentas huérfanas.
                                        if (firebaseUser != null) {
                                            firebaseUser.delete().addOnFailureListener(deleteEx -> Log.e("DetalleSolicitud", "Error al borrar usuario de Auth tras fallo de batch: ", deleteEx));
                                        }
                                    });
                        } else {
                            pDialog.dismissWithAnimation();
                            mostrarSweetDialogError("Error: UID de Firebase Auth nulo al crear usuario.", "error_habilitar");
                            Log.e("DetalleSolicitud", "UID de Firebase Auth nulo al crear usuario.");
                        }
                    } else {
                        pDialog.dismissWithAnimation();
                        mostrarSweetDialogError("Error al registrar usuario en Auth: " + authTask.getException().getMessage(), "error_habilitar");
                        Log.e("DetalleSolicitud", "Error al crear usuario en Auth: ", authTask.getException());
                    }
                });
    }

    private void rechazarSolicitud(PostulacionTaxista postulacion) {
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Rechazando solicitud...");
        pDialog.setCancelable(false);
        pDialog.show();

        // Referencia al documento de la postulación
        // Aquí es donde el campo 'id' de PostulacionTaxista es crucial.
        DocumentReference postulacionDocRef = firestore.collection("postulacionesTaxistas").document(solicitudActual.getId());

        // Actualizar el estado de la solicitud a "rechazado"
        Map<String, Object> updates = new HashMap<>();
        updates.put("estadoSolicitud", "rechazado");
        updates.put("fechaRevision", FieldValue.serverTimestamp());
        // Opcional: añadir una nota de rechazo por parte del administrador
        // updates.put("notasAdministrador", "Rechazado por incumplimiento de requisitos.");

        postulacionDocRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    pDialog.dismissWithAnimation();
                    mostrarSweetDialogExito("Solicitud rechazada exitosamente", "rechazado");
                    regresarALista("rechazado"); // Regresa a la lista de solicitudes
                })
                .addOnFailureListener(e -> {
                    pDialog.dismissWithAnimation();
                    mostrarSweetDialogError("Error al rechazar solicitud: " + e.getMessage(), "error_rechazar");
                    Log.e("DetalleSolicitud", "Error al rechazar solicitud: ", e);
                });
    }
    private void mostrarSweetDialogExito(String mensaje, String accion) {
        SweetAlertDialog successDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        successDialog.setTitleText("¡Éxito!");
        successDialog.setContentText(mensaje);
        successDialog.setConfirmText("OK");
        successDialog.setConfirmClickListener(sweetAlertDialog -> {
            sweetAlertDialog.dismissWithAnimation();
            regresarALista(accion);
        });
        successDialog.show();
    }

    private void mostrarSweetDialogError(String mensaje, String accion) {
        SweetAlertDialog errorDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        errorDialog.setTitleText("Acción realizada");
        errorDialog.setContentText(mensaje);
        errorDialog.setConfirmText("OK");
        errorDialog.setConfirmClickListener(sweetAlertDialog -> {
            sweetAlertDialog.dismissWithAnimation();
            regresarALista(accion);
        });
        errorDialog.show();
    }
    private void regresarALista(String accion) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("accion", accion);
        setResult(RESULT_OK, resultIntent);
        finish();  // Regresa inmediatamente a la lista
    }

}