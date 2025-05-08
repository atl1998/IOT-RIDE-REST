package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class NotificacionesActivity extends AppCompatActivity {

    private RecyclerView rvNotificaciones;

    private List<Notificacion> listaNotificaciones;

    private NotificacionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.adminhotel_activity_notificaciones);

        rvNotificaciones = findViewById(R.id.listaNotificaciones);
        rvNotificaciones.setLayoutManager(new LinearLayoutManager(this));
        rvNotificaciones.setHasFixedSize(true);

        // Inicializar lista de habitaciones
        listaNotificaciones = new ArrayList<>();
        listaNotificaciones.add(new Notificacion(
                "Se ha realizado una nueva reserva para el 12 de mayo. ¡Verifica los detalles!")
        );

        listaNotificaciones.add(new Notificacion(
                "La habitación 202 está lista para el check-in de 3 PM. ¡Asegúrate de que todo esté en orden!")
        );

        listaNotificaciones.add(new Notificacion(
                "El huésped en la habitación 305 ha solicitado un servicio de limpieza. ¡Por favor, coordina con el personal!")
        );

        listaNotificaciones.add(new Notificacion(
                "¡Recordatorio! El huésped en la habitación 101 requiere una extensión de su estadía hasta el 15 de mayo.")
        );

        listaNotificaciones.add(new Notificacion(
                "El huésped en la habitación 404 ha hecho una queja sobre el aire acondicionado. ¡Atención inmediata requerida!")
        );

        listaNotificaciones.add(new Notificacion(
                "¡Importante! Se ha aprobado una solicitud para un evento en el salón principal el 20 de mayo.")
        );

        listaNotificaciones.add(new Notificacion(
                "La factura de la habitación 205 ha sido pagada exitosamente. ¡Gracias por su preferencia!")
        );

        listaNotificaciones.add(new Notificacion(
                "Recuerda verificar el estado del minibar en la habitación 305 antes del check-out del huésped.")
        );

        listaNotificaciones.add(new Notificacion(
                "Se acerca la fecha de check-out para el huésped en la habitación 103. Realiza la revisión final de la habitación.")
        );

        listaNotificaciones.add(new Notificacion(
                "El sistema de reservas está experimentando una actualización. Podría haber interrupciones momentáneas.")
        );

        adapter = new NotificacionAdapter(listaNotificaciones);
        rvNotificaciones.setAdapter(adapter);

        


        // Usamos un OnClickListener estándar
        MaterialButton backButton = findViewById(R.id.backBottom);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Usar el método tradicional
            }
        });
    }
}