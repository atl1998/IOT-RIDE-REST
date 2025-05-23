package com.example.hotelreservaapp.cliente;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.loginAndRegister.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class HistorialEventos extends AppCompatActivity {
    String channelId = "ChannelRideAndRest"; // En cualquier otra Activity

    private Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_historial_eventos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.historialCliente); // Para que aparezca q esta seleccionado ese
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.inicioCliente) {
                startActivity(new Intent(this, HomeCliente.class));
                return true;
            } else if (id == R.id.chat_cliente) {
                startActivity(new Intent(this, ClienteChat.class));
                return true;
            } else if (id == R.id.historialCliente) {
                // startActivity(new Intent(this, HistorialEventos.class));
                return true;
            } else if (id == R.id.perfilCliente) {
                // startActivity(new Intent(this, PerfilCliente.class));
                return true;
            }
            return false;
        });

        MaterialButton btnNotificaciones = findViewById(R.id.notificaciones_cliente);
        btnNotificaciones.setOnClickListener(v -> {
            Intent intent = new Intent(HistorialEventos.this, ClienteNotificaciones.class);
            startActivity(intent);
        });
        CardView cardView = findViewById(R.id.card_view);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acciones que quieres realizar cuando se haga clic
                Intent intent = new Intent(HistorialEventos.this, DetallesReserva.class);
                startActivity(intent);
            }
        });

        btnCheckout = findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar modal
                mostrarDialogoCheckout();
            }
        });

    }

    private void mostrarDialogoCheckout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.cliente_dialog_checkout);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Obtener la vista del diálogo
        View modalView = dialog.findViewById(android.R.id.content);

        // Ahora puedes acceder al botón dentro del diálogo
        Button btnContinuar = modalView.findViewById(R.id.btn_continue);
        btnContinuar.setOnClickListener(v -> {
            // Cierra el modal actual
            dialog.dismiss();

            // Quitar el blur cuando se cierra el modal
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getWindow().getDecorView().setRenderEffect(null);
            }

            // Llamar al método para abrir el nuevo modal "cliente_consumosextras"
            mostrarClienteConsumoExtras();
        });
    }
    // Método para mostrar el nuevo modal
    private void mostrarClienteConsumoExtras() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.cliente_consumosextras);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        // Ahora accedemos al botón btn_solicitar_checkout dentro del nuevo modal
        Button btnSolicitarCheckout = dialog.findViewById(R.id.btn_solicitar_checkout);
        btnSolicitarCheckout.setOnClickListener(v -> {
            // Cerrar el modal
            dialog.dismiss();
            // Deshabilitamos el botón original
            if (btnCheckout != null) {
                btnCheckout.setEnabled(false);
                btnCheckout.setAlpha(0.5f);  // Establecer la opacidad al 50% (0.0f - completamente transparente, 1.0f - completamente opaco)
            }
            // Aquí iría la validación real, por ahora mostramos mensaje:
            Toast.makeText(HistorialEventos.this, "¡Solicitud registrada correctamente!", Toast.LENGTH_SHORT).show();
            LanzarNotificacionSolicitarCheckout();
        });
    }

    public void LanzarNotificacionSolicitarCheckout() {
        //Si se clickea en la notificacion del checkout te redirigirá a el apartado de notificaciones de la aplicacion
        Intent intent = new Intent(HistorialEventos.this, ClienteNotificaciones.class);
        //Pero esto no se va a lanzar hasta que se clickee en las notificaciones por eso se quedara pendiente
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo_r_r_2)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.checkout_image_v2))
                .setContentTitle("¡Se ha realizado el checkout correctamente!")
                .setContentText("Porfavor estar pendiente a las notificaciones, ya que por este medio se le notificará cuando se haya terminado el proceso.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
        }
    }



}