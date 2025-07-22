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
import com.example.hotelreservaapp.LogManager;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetalleSolicitudActivity extends AppCompatActivity  {
    private SuperadminDetalleSolicitudActivityBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private PostulacionTaxista solicitudActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SuperadminDetalleSolicitudActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

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

        //Datos superadmin
        String uidSuperadmin = firebaseAuth.getCurrentUser() != null
                ? firebaseAuth.getCurrentUser().getUid()
                : null;

        firebaseAuth.createUserWithEmailAndPassword(email, contrasenaTemporal)
                .addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        FirebaseUser firebaseUser = authTask.getResult().getUser();
                        if (firebaseUser != null) {
                            String userUid = firebaseUser.getUid();

                            StorageReference origenPerfil = storage.getReference().child("fotos_postulaciones/foto_" + postulacion.getId() + ".jpg");
                            StorageReference origenPlaca = storage.getReference().child("fotos_postulaciones/placa_" + postulacion.getId() + ".jpg");
                            StorageReference destinoPerfil = storage.getReference().child("fotos_taxistas/" + userUid + "/foto_perfil.jpg");
                            StorageReference destinoPlaca = storage.getReference().child("fotos_taxistas/" + userUid + "/foto_placa.jpg");

                            origenPerfil.getBytes(5 * 1024 * 1024).addOnSuccessListener(bytesPerfil -> {
                                destinoPerfil.putBytes(bytesPerfil).addOnSuccessListener(task1 -> {
                                    destinoPerfil.getDownloadUrl().addOnSuccessListener(downloadUriPerfil -> {

                                        origenPlaca.getBytes(5 * 1024 * 1024).addOnSuccessListener(bytesPlaca -> {
                                            destinoPlaca.putBytes(bytesPlaca).addOnSuccessListener(task2 -> {
                                                destinoPlaca.getDownloadUrl().addOnSuccessListener(downloadUriPlaca -> {

                                                    DetallesTaxista driverDetails = new DetallesTaxista(
                                                            postulacion.getNumeroPlaca(),
                                                            downloadUriPlaca.toString()
                                                    );

                                                    Usuario nuevoUsuario = new Usuario(
                                                            postulacion.getNombres(),
                                                            postulacion.getApellidos(),
                                                            "taxista",
                                                            postulacion.getTipoDocumento(),
                                                            postulacion.getNumeroDocumento(),
                                                            postulacion.getFechaNacimiento(),
                                                            postulacion.getCorreo(),
                                                            postulacion.getTelefono(),
                                                            postulacion.getDireccion(),
                                                            downloadUriPerfil.toString(),
                                                            true,
                                                            true
                                                    );
                                                    nuevoUsuario.setDriverDetails(driverDetails);

                                                    WriteBatch batch = firestore.batch();
                                                    DocumentReference usuarioDocRef = firestore.collection("usuarios").document(userUid);
                                                    batch.set(usuarioDocRef, nuevoUsuario);

                                                    DocumentReference postulacionDocRef = firestore.collection("postulacionesTaxistas").document(postulacion.getId());
                                                    Map<String, Object> postulacionUpdates = new HashMap<>();
                                                    postulacionUpdates.put("estadoSolicitud", "aprobado");
                                                    postulacionUpdates.put("fechaRevision", FieldValue.serverTimestamp());
                                                    postulacionUpdates.put("idUsuarioAsignado", userUid);
                                                    batch.update(postulacionDocRef, postulacionUpdates);

                                                    batch.commit().addOnSuccessListener(aVoid -> {
                                                        pDialog.dismissWithAnimation();
                                                        mostrarSweetDialogExito("Taxista habilitado exitosamente", "habilitado");

                                                        firestore.collection("usuarios").document(uidSuperadmin).get()
                                                                .addOnSuccessListener(doc -> {
                                                                    if (doc.exists()) {
                                                                        String nombreSuperadmin = doc.getString("nombre");
                                                                        String apellidoSuperadmin = doc.getString("apellido");
                                                                        String nombreCompletoSuperadmin = nombreSuperadmin + " " + apellidoSuperadmin;

                                                                        String nombreTaxista = postulacion.getNombres() + " " + postulacion.getApellidos();

                                                                        LogManager.registrarLogRegistro(
                                                                                nombreCompletoSuperadmin,
                                                                                "Aprobación de solicitud",
                                                                                "La solicitud del taxista " + nombreTaxista + " ha sido aprobada"
                                                                        );
                                                                    }
                                                                });

                                                        Notificacion nueva = new Notificacion(
                                                                "Nuevo taxista habilitado",
                                                                "Se ha habilitado correctamente un nuevo taxista.",
                                                                System.currentTimeMillis(), false
                                                        );
                                                        new Thread(() -> AppDatabase.getInstance(DetalleSolicitudActivity.this)
                                                                .notificacionDao().insertar(nueva)).start();

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

                                                    }).addOnFailureListener(e -> {
                                                        pDialog.dismissWithAnimation();
                                                        mostrarSweetDialogError("Error al guardar datos del taxista o postulación: " + e.getMessage(), "error_habilitar");
                                                        Log.e("DetalleSolicitud", "Error en batch de habilitación: ", e);
                                                        firebaseUser.delete().addOnFailureListener(deleteEx ->
                                                                Log.e("DetalleSolicitud", "Error al borrar usuario de Auth tras fallo de batch: ", deleteEx));
                                                    });

                                                }).addOnFailureListener(e -> {
                                                    pDialog.dismissWithAnimation();
                                                    mostrarSweetDialogError("Error al obtener URL de la placa", "error_habilitar");
                                                });
                                            }).addOnFailureListener(e -> {
                                                pDialog.dismissWithAnimation();
                                                mostrarSweetDialogError("Error al subir la foto de la placa", "error_habilitar");
                                            });
                                        }).addOnFailureListener(e -> {
                                            pDialog.dismissWithAnimation();
                                            mostrarSweetDialogError("No se encontró la imagen de la placa en Storage", "error_habilitar");
                                        });

                                    }).addOnFailureListener(e -> {
                                        pDialog.dismissWithAnimation();
                                        mostrarSweetDialogError("Error al obtener URL de la foto de perfil", "error_habilitar");
                                    });
                                }).addOnFailureListener(e -> {
                                    pDialog.dismissWithAnimation();
                                    mostrarSweetDialogError("Error al subir la foto de perfil", "error_habilitar");
                                });
                            }).addOnFailureListener(e -> {
                                pDialog.dismissWithAnimation();
                                mostrarSweetDialogError("No se encontró la imagen de perfil en Storage", "error_habilitar");
                            });
                        }
                    } else {
                        pDialog.dismissWithAnimation();
                        mostrarSweetDialogError("Error al crear usuario: " + authTask.getException().getMessage(), "error_habilitar");
                    }
                });
    }



    private void rechazarSolicitud(PostulacionTaxista postulacion) {
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Rechazando solicitud...");
        pDialog.setCancelable(false);
        pDialog.show();

        String uidSuperadmin = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

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
                    // 🔥 Recuperar nombre y apellido del superadmin para el log
                    firestore.collection("usuarios").document(uidSuperadmin).get()
                            .addOnSuccessListener(doc -> {
                                if (doc.exists()) {
                                    String nombreSuperadmin = doc.getString("nombre");
                                    String apellidoSuperadmin = doc.getString("apellido");
                                    String nombreCompletoSuperadmin = (nombreSuperadmin != null ? nombreSuperadmin : "") + " " + (apellidoSuperadmin != null ? apellidoSuperadmin : "");

                                    String nombreTaxista = postulacion.getNombres() + " " + postulacion.getApellidos();

                                    LogManager.registrarLogRegistro(
                                            nombreCompletoSuperadmin,
                                            "Rechazo de solicitud",
                                            "La solicitud del taxista " + nombreTaxista + " ha sido rechazada"
                                    );
                                }
                                mostrarSweetDialogExito("Solicitud rechazada exitosamente", "rechazado");
                                regresarALista("rechazado"); // Regresa a la lista de solicitudes
                            });

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