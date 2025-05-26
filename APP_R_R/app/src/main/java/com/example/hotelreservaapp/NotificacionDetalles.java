package com.example.hotelreservaapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.Objetos.Notificaciones;
import com.example.hotelreservaapp.Objetos.NotificacionesStorageHelper;
import com.example.hotelreservaapp.Objetos.NotificationManagerNoAPP;

public class NotificacionDetalles extends AppCompatActivity {
    private TextView headerNotification, ContentNotification, ContenidoExtra;
    private ImageView imagen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notificacion_detalles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imagen = findViewById(R.id.ImageNotificacion);
        headerNotification = findViewById(R.id.headerNotification);
        ContentNotification = findViewById(R.id.ContentNotification);
        ContenidoExtra = findViewById(R.id.ContenidoExtra);

        Notificaciones notificacion = (Notificaciones) getIntent().getSerializableExtra("notificacion");

        if (notificacion != null) {
            // Solo cargar si notification es != null y si is leido es false xq si es true no vale la pena leerlo
            if (!notificacion.isLeido()){
                // 1. Cargar lista guardada (si existe)
                NotificacionesStorageHelper storageHelper = new NotificacionesStorageHelper(this);
                Notificaciones[] notificacionesGuardadas = storageHelper.leerArchivoNotificacionesDesdeSubcarpeta();

                // 2. Crear o cargar NotificationManager con la lista actual
                NotificationManagerNoAPP notificationManagerNoAPP = new NotificationManagerNoAPP();
                if (notificacionesGuardadas != null && notificacionesGuardadas.length > 0) {
                    for (Notificaciones n : notificacionesGuardadas) {
                        if (notificacion.getId()==n.getId()) {
                            n.setLeido(true);
                        }
                        notificationManagerNoAPP.getListaNotificaciones().add(n);
                    }
                }
                // 4. Guardar la lista actualizada
                Notificaciones[] arregloParaGuardar = notificationManagerNoAPP.getListaNotificaciones()
                        .toArray(new Notificaciones[0]);
                storageHelper.guardarArchivoNotificacionesEnSubcarpeta(arregloParaGuardar);
            }

            headerNotification.setText(notificacion.getTituloAmigable());
            ContentNotification.setText(notificacion.getMensaje());
            ContenidoExtra.setText(notificacion.getMensajeExtra());
            if ("01".equals(notificacion.getTipo())){
                imagen.setImageResource(R.drawable.checkout_image_v2);
            } else if ("02".equals(notificacion.getTipo())) {
                imagen.setImageResource(R.drawable.checkout_sucessfull);
            } else if ("03".equals(notificacion.getTipo())) {
                imagen.setImageResource(R.drawable.taxi);
            }
        }
    }
}