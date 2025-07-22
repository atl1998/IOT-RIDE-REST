package com.example.hotelreservaapp.cliente;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.hotelreservaapp.Objetos.Notificaciones;
import com.example.hotelreservaapp.Objetos.NotificacionesStorageHelper;
import com.example.hotelreservaapp.Objetos.NotificationManagerNoAPP;
import com.example.hotelreservaapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService{
    private static final String CHANNEL_ID = "ChannelRideAndRest";
    private static final String CHANNEL_ID_ADMIN = "ChannelRideAndRestAdmin";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM", "Nuevo token: " + token);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("usuarios").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String rol = documentSnapshot.getString("rol");
                            if ("cliente".equals(rol) || "adminHotel".equals(rol)) {
                                Map<String, Object> data = new HashMap<>();
                                data.put("fcmToken", token);
                                db.collection("usuarios").document(uid)
                                        .update(data)
                                        .addOnSuccessListener(aVoid -> Log.d("FCM", "Token guardado en Firestore para usuario con rol: " + rol))
                                        .addOnFailureListener(e -> Log.w("FCM", "Error guardando token", e));
                            } else {
                                Log.d("FCM", "Rol desconocido, no se guarda token: " + rol);
                            }
                        } else {
                            Log.d("FCM", "Documento de usuario no existe");
                        }
                    })
                    .addOnFailureListener(e -> Log.w("FCM", "Error obteniendo rol del usuario", e));
        } else {
            Log.d("FCM", "No hay usuario logueado, no se guarda token");
        }
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        // Obtiene los datos del payload
        Map<String, String> data = remoteMessage.getData();

        String tipo = data.get("tipo"); // Por ejemplo: "02"
        String titulo = data.get("titulo"); // Por ejemplo: "Checkout Finalizado"
        String tituloAmigable = data.get("tituloAmigable"); // Por ejemplo: "¡Checkout finalizado correctamente!"
        String mensaje = data.get("mensaje"); // Por ejemplo: "El checkout ha finalizado..."
        String mensajeExtra = data.get("mensajeExtra"); // Por ejemplo: "En ese apartado verás un resumen..."
        String tipoAlerta = data.get("tipoAlerta");
        long fecha = System.currentTimeMillis();

        Log.d("FCM", "Tipo: " + tipo);
        Log.d("FCM", "Titulo: " + titulo);
        Log.d("FCM", "Titulo amigable: " + tituloAmigable);
        Log.d("FCM", "Mensaje: " + mensaje);
        Log.d("FCM", "Mensaje extra: " + mensajeExtra);
        Log.d("FCM", "tipoAlerta: " + tipoAlerta);

        if ("admin".equals(tipoAlerta)) {
            // Solo si explícitamente es "admin", hacemos lógica especial
            // Solo si es notificación para admin, consultamos usuario actual
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Log.w("FCM", "No hay usuario logueado, omitiendo notificación admin");
                return;
            }

            String uid = user.getUid();
            FirebaseFirestore.getInstance().collection("usuarios").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String rol = documentSnapshot.getString("rol");
                            if ("adminHotel".equals(rol)) {
                                String ContentTitle=tituloAmigable;
                                String ContentText=mensaje;

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID_ADMIN)
                                        .setSmallIcon(R.drawable.logo_r_r_2)
                                        .setContentTitle(ContentTitle)
                                        .setContentText(ContentText)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setAutoCancel(true);

                                if ("51".equals(tipo)) {
                                    Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.checkout_sucessfull);
                                    builder.setLargeIcon(largeIconBitmap);
                                }
                                NotificationManagerCompat notificationManagerCompat  = NotificationManagerCompat.from(this);
                                if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                    Log.d("FCM", "Mostrando notificación ahora");
                                    notificationManagerCompat.notify(1001, builder.build());
                                }
                            } else {
                                Log.d("FCM", "El usuario no es admin, ignorando");
                            }
                        }
                    });
        } else {
            Intent intent = new Intent(this, ClienteNotificaciones.class);
            intent.putExtra("Case", tipo);
            String ContentTitle=tituloAmigable;
            String ContentText=mensaje;

            Context context = getApplicationContext();

            // Guardar la notificación en archivo
            NotificacionesStorageHelper storageHelper = new NotificacionesStorageHelper(context);

            Notificaciones[] notificacionesGuardadas = storageHelper.leerArchivoNotificacionesDesdeSubcarpeta();

            NotificationManagerNoAPP notificationManagerNoAPP = new NotificationManagerNoAPP();
            if (notificacionesGuardadas != null && notificacionesGuardadas.length > 0) {
                for (Notificaciones n : notificacionesGuardadas) {
                    notificationManagerNoAPP.getListaNotificaciones().add(n);
                }
            }

            notificationManagerNoAPP.agregarNotificacion(tipo, titulo, tituloAmigable, mensaje, mensajeExtra, fecha);

            Notificaciones[] arregloParaGuardar = notificationManagerNoAPP.getListaNotificaciones()
                    .toArray(new Notificaciones[0]);
            storageHelper.guardarArchivoNotificacionesEnSubcarpeta(arregloParaGuardar);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo_r_r_2)
                    .setContentTitle(ContentTitle)
                    .setContentText(ContentText)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            if ("02".equals(tipo)) {
                Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.checkout_sucessfull);
                builder.setLargeIcon(largeIconBitmap);
            } else if ("03".equals(tipo)) {
                Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.taxi);
                builder.setLargeIcon(largeIconBitmap);
            } else if ("04".equals(tipo)) {
                Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.taxi_buscando);
                builder.setLargeIcon(largeIconBitmap);
            } else if ("05".equals(tipo)) {
                Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.taxi_aceptado);
                builder.setLargeIcon(largeIconBitmap);
            } else if ("06".equals(tipo)) {
                Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.taxi_rechazado);
                builder.setLargeIcon(largeIconBitmap);
            } else if ("07".equals(tipo)) {
                Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.taxi_esperando);
                builder.setLargeIcon(largeIconBitmap);
            } else if ("08".equals(tipo)) {
                Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.taxi_encurso);
                builder.setLargeIcon(largeIconBitmap);
            } else if ("09".equals(tipo)) {
                Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.taxi_destino);
                builder.setLargeIcon(largeIconBitmap);
            }
            NotificationManagerCompat notificationManagerCompat  = NotificationManagerCompat.from(this);
            if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManagerCompat.notify(1, builder.build());
            }
        }
    }

}
