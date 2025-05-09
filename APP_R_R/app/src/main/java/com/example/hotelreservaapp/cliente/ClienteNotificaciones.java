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

import com.example.hotelreservaapp.R;

public class ClienteNotificaciones extends AppCompatActivity {

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

        // Primer mensaje
        View mensaje1 = getLayoutInflater().inflate(R.layout.item_notification, layoutMensajes, false);
        TextView texto1 = mensaje1.findViewById(R.id.texto_mensaje);
        texto1.setText("El checkout ha finalizado, por favor dirigirse a la opción de “Detalles” en el hotel seleccionado y buscar en la parte inferior el botón “Procesar pago.”");
        layoutMensajes.addView(mensaje1);

        // Segundo mensaje
        View mensaje2 = getLayoutInflater().inflate(R.layout.item_notification, layoutMensajes, false);
        TextView texto2 = mensaje2.findViewById(R.id.texto_mensaje);
        texto2.setText("El checkout fue solicitado correctamente. Cuando este proceso termine se le notificará por este medio para que pueda realizar su pago.");
        layoutMensajes.addView(mensaje2);


        Button btnVolver = findViewById(R.id.volverPagina);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Esto regresa al Activity anterior
            }
        });

    }
}