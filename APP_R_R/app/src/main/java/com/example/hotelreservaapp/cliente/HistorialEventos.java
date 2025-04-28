package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.R;

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
        // Obtener el botón "Detalles" por su ID y establecer el listener
        Button btnDetalles = findViewById(R.id.btnDetalles);
        btnDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para redirigir a la actividad DetalleReserva
                Intent intent = new Intent(HistorialEventos.this, DetallesReserva.class);
                startActivity(intent);
            }
        });
        Button btnCheckout = findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar modal
                showCheckoutModal();
            }
        });

    }
    private void showCheckoutModal() {
        // Activar el blur en dispositivos con Android 12 o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getWindow().getDecorView().setRenderEffect(
                    RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
            );
        }

        View modalView = getLayoutInflater().inflate(R.layout.cliente_dialog_checkout, null);

        // Establecer el padding izquierdo y derecho en la vista del modal

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.TransparentDialog);
        builder.setView(modalView);

        android.app.AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        // Establecer el tamaño del AlertDialog para que ocupe una parte significativa de la pantalla
        dialog.getWindow().setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,  // Ancho completo
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT // Alto ajustable
        );

        Button btnContinuar = modalView.findViewById(R.id.btn_continue);
        btnContinuar.setOnClickListener(v -> {
            dialog.dismiss();
            // Quitar el blur cuando se cierra el modal
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getWindow().getDecorView().setRenderEffect(null);
            }
        });

        dialog.setOnDismissListener(d -> {
            // Asegúrate de quitar el blur también si el usuario lo cierra de otra forma
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getWindow().getDecorView().setRenderEffect(null);
            }
        });
    }
}