package com.example.hotelreservaapp.cliente;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.Objetos.Notificaciones;
import com.example.hotelreservaapp.Objetos.NotificacionesStorageHelper;
import com.example.hotelreservaapp.Objetos.NotificationManager;
import com.example.hotelreservaapp.R;

public class ClienteNotificaciones extends AppCompatActivity {
    private NotificationManager notificationManager; // tu clase para manejar la lista
    private NotificacionesStorageHelper storageHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cliente_notificaciones);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        LinearLayout layoutMensajes = findViewById(R.id.layoutMensajes);


        // Instanciamos el helper para leer el archivo
        NotificacionesStorageHelper storageHelper = new NotificacionesStorageHelper(this);
        Notificaciones[] notificacionesGuardadas = storageHelper.leerArchivoNotificacionesDesdeSubcarpeta();

        if (notificacionesGuardadas != null && notificacionesGuardadas.length > 0) {
            for (Notificaciones n : notificacionesGuardadas) {
                // Inflar la vista de item_notification
                View mensajeView = getLayoutInflater().inflate(R.layout.item_notification, layoutMensajes, false);
                TextView title = mensajeView.findViewById(R.id.texto_title);
                TextView texto = mensajeView.findViewById(R.id.texto_mensaje);

                // Setear los textos desde el objeto Notificaciones
                title.setText(n.getTitulo());
                texto.setText(n.getMensaje());

                layoutMensajes.addView(mensajeView);
            }
        }


        Button btnVolver = findViewById(R.id.volverPagina);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Esto regresa al Activity anterior
            }
        });

    }
}

/*
case "02":
                    View mensaje2 = getLayoutInflater().inflate(R.layout.item_notification, layoutMensajes, false);
                    TextView title2 = mensaje2.findViewById(R.id.texto_title);
                    TextView texto2 = mensaje2.findViewById(R.id.texto_mensaje);
                    title2.setText("Checkout Finalizado");
                    texto2.setText("El checkout ha finalizado, por favor dirigirse a la opción de “Detalles” en el hotel seleccionado y buscar en la parte inferior el botón “Procesar pago.”");
                    layoutMensajes.addView(mensaje2);
                    break;
*/