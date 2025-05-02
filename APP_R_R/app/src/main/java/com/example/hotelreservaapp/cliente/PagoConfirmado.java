package com.example.hotelreservaapp.cliente;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.R;

public class PagoConfirmado extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pago_confirmado);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button btnCalificar = findViewById(R.id.btn_calificar); // Asegúrate de tener este botón en tu layout
        if (btnCalificar != null) {
            btnCalificar.setOnClickListener(v -> {
                mostrarModalCalificacion();
            });
        }
    }

    private void mostrarModalCalificacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.cliente_valoracion); // Asegúrate de crear este layout
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Obtener referencias a los elementos en el diálogo
        EditText comentarioText = dialog.findViewById(R.id.comentario_valo);
        RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
        Button btnEnviarValo = dialog.findViewById(R.id.btn_enviar_valo);

        btnEnviarValo.setOnClickListener(v -> {
            // Obtener el valor de la calificación
            float calificacion = ratingBar.getRating();
            String comentario = comentarioText.getText().toString().trim();

            // Validar que haya dado una calificación
            if (calificacion == 0) {
                Toast.makeText(this, "Por favor, califica nuestro servicio", Toast.LENGTH_SHORT).show();
                return;
            }

            // Aquí puedes guardar la calificación y el comentario
            // Por ejemplo, enviarlos a tu servidor o guardarlos localmente

            // Mostrar mensaje de confirmación
            Toast.makeText(this, "¡Gracias por tu calificación!", Toast.LENGTH_SHORT).show();

            // Cerrar el diálogo
            dialog.dismiss();
        });
    }
}