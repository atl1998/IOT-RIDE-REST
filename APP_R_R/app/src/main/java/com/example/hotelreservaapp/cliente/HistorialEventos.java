package com.example.hotelreservaapp.cliente;

import android.app.AlertDialog;
import android.content.Intent;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.loginAndRegister.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class HistorialEventos extends AppCompatActivity {

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

        CardView cardView = findViewById(R.id.card_view);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acciones que quieres realizar cuando se haga clic
                Intent intent = new Intent(HistorialEventos.this, DetallesReserva.class);
                startActivity(intent);
            }
        });

        Button btnCheckout = findViewById(R.id.btnCheckout);
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

            // Aquí iría la validación real, por ahora mostramos mensaje:
            Toast.makeText(HistorialEventos.this, "¡Solicitud registrada correctamente!", Toast.LENGTH_SHORT).show();
        });
    }

}