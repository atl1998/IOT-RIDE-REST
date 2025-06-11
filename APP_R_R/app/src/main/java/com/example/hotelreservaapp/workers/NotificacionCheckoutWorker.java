package com.example.hotelreservaapp.workers;

import com.example.hotelreservaapp.Objetos.NotificationManagerNoAPP;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.hotelreservaapp.Objetos.Notificaciones;
import com.example.hotelreservaapp.Objetos.NotificacionesStorageHelper;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.cliente.ClienteNotificaciones;

public class NotificacionCheckoutWorker extends Worker {
    private static final String CHANNEL_ID = "ChannelRideAndRest";

    public NotificacionCheckoutWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        // Datos fijos en el worker
        String tipo = "02";
        String titulo = "Checkout Finalizado";
        String tituloAmigable = "¡Checkout finalizado correctamente!";
        String mensaje = "El checkout ha finalizado, por favor dirigirse a la opción de “Detalles” en el hotel seleccionado y buscar en la parte inferior el botón “Revisar el pago realizado”.";
        String mensajeExtra = "En dicho apartado se encuentra un resumen del pago realizado antes de la reserva, además también se le indica los cobros extras que se ha realizado y también si se cuenta con Servicio de Taxi o no.";
        long fecha = System.currentTimeMillis();

        // 2 notificacion
        String tipo2 = "03";
        String titulo2 = "Servicio de Taxi";
        String tituloAmigable2 = "¡Tu hotel cuenta con servicio de Taxi!";
        String mensaje2 = "Su hotel cuenta con servicio de taxi, si desea solicitar dicho servicio por favor dirigirse a “Revisar el pago realizado” en detalles de tu hotel reservado, y presionar el botón “Solicitar Servicio de Taxi”.";
        String mensajeExtra2 = "";
        long fecha2 = System.currentTimeMillis();

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
        notificationManagerNoAPP.agregarNotificacion(tipo2, titulo2, tituloAmigable2, mensaje2, mensajeExtra2, fecha2);


        Notificaciones[] arregloParaGuardar = notificationManagerNoAPP.getListaNotificaciones()
                .toArray(new Notificaciones[0]);
        storageHelper.guardarArchivoNotificacionesEnSubcarpeta(arregloParaGuardar);

        // CREO QUE INNECESARIO
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("ReservaPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("SolicitarCheckout", true);
        editor.apply();

        // Crear intent hacia ClienteNotificaciones
        Intent intent = new Intent(getApplicationContext(), ClienteNotificaciones.class);
        intent.putExtra("Case", "02");
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            return Result.success(); // Salimos silenciosamente
        }

        // Crear la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_r_r_2)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.checkout_sucessfull))
                .setContentTitle("¡Checkout fue finalizado correctamente!")
                .setContentText("El checkout ha finalizado, por favor dirígete a la sección de notificaciones.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(2, builder.build());

        PendingIntent secondPendingIntent = PendingIntent.getActivity(getApplicationContext(), 2, intent, PendingIntent.FLAG_IMMUTABLE);

        // Segunda notificación (por ejemplo, resumen del pago)
        NotificationCompat.Builder secondBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_r_r_2)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.taxi))
                .setContentTitle("¡Tu hotel cuenta con servicio de Taxi!")
                .setContentText("Su hotel cuenta con servicio de taxi, si desea solicitar dicho servicio por favor dirigirse a “Revisar el pago realizado” en detalles de tu hotel reservado, y presionar el botón “Solicitar Servicio de Taxi”.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(secondPendingIntent)
                .setAutoCancel(true);

        // Mostrar la segunda notificación con otro ID (por ejemplo, 3)
        notificationManager.notify(3, secondBuilder.build());
        return Result.success();
    }
}
