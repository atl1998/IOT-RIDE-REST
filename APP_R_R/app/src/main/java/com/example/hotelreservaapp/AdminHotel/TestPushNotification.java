package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class TestPushNotification extends AppCompatActivity {

    private static final String TAG = "NOTIFICACIÓN";
    private static final String TOKEN_HARDCODEADO = "ebcrJ1avQTOVMf3V6_2Kve:APA91bHIMk5eEFBvHAE5FBGJ1ZxqJXnHRcwZUKpYMgNkYEmfbMuw6Wv1fcqK2ofiBVKb0CWl9wceoRk6BeWo9glTS7Uysa8g3mzBzoopCZoATAdV0CgoWDs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.adminhotel_activity_test_push_notification);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a vistas (corregido nombres y tipos)
        EditText etToken = findViewById(R.id.editToken);
        EditText etTitulo = findViewById(R.id.editTitulo);
        EditText etCuerpo = findViewById(R.id.editCuerpo);
        MaterialButton btnBack = findViewById(R.id.btnBack);
        Button btnEnviar = findViewById(R.id.btnEnviar);

        btnBack.setOnClickListener(v -> finish());

        btnEnviar.setOnClickListener(v -> {
            String token = TOKEN_HARDCODEADO; // Ignorar texto del EditText
            /*
            String titulo = etTitulo.getText().toString().trim();
            String cuerpo = etCuerpo.getText().toString().trim();

            if (titulo.isEmpty() || cuerpo.isEmpty()) {
                Toast.makeText(this, "Rellena título y cuerpo", Toast.LENGTH_SHORT).show();
                return;
            }
               */
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser == null) {
                Toast.makeText(this, "Debes iniciar sesión para enviar la notificación", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseFunctionsHelper functionsHelper = new FirebaseFunctionsHelper();

                String tipo = "02";
                String titulo = "Checkout Finalizado";
                String tituloAmigable = "¡Checkout finalizado correctamente!";
                String mensaje = "El checkout ha finalizado. Por favor dirígete a la opción 'Detalles' en el hotel seleccionado y busca el botón 'Revisar el pago realizado'.";
                String mensajeExtra = "En ese apartado verás un resumen del pago antes de la reserva, los cobros extras, y si cuentas con servicio de taxi.";

                functionsHelper.enviarNotificacion(token, tipo, titulo, tituloAmigable, mensaje, mensajeExtra, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                            Log.e(TAG, "❌ Error enviando notificación", e);
                            Toast.makeText(TestPushNotification.this, "Error al enviar", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        runOnUiThread(() -> {
                            if (response.isSuccessful()) {
                                Toast.makeText(TestPushNotification.this, "✅ Notificación enviada", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Notificación enviada correctamente");
                            } else {
                                Toast.makeText(TestPushNotification.this, "Error al enviar: código " + response.code(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Error HTTP: " + response.code());
                            }
                        });
                    }
                });
            }
        });

    }
}
