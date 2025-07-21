package com.example.hotelreservaapp.AdminHotel;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirebaseFunctionsHelper {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String TAG = "FirebaseFunctionsHelper";

    public void enviarNotificacion(
            String tokenNotificacion,
            String tipo,
            String titulo,
            String tituloAmigable,
            String mensaje,
            String mensajeExtra,
            Callback callback
    ) {
        String url = "https://us-central1-riderest-baf4e.cloudfunctions.net/enviarNotificacion";

        String jsonPayload = "{"
                + "\"token\":\"" + tokenNotificacion + "\","
                + "\"tipo\":\"" + tipo + "\","
                + "\"titulo\":\"" + titulo + "\","
                + "\"tituloAmigable\":\"" + tituloAmigable + "\","
                + "\"mensaje\":\"" + mensaje + "\","
                + "\"mensajeExtra\":\"" + mensajeExtra + "\""
                + "}";

        // Obtener token ID Firebase de forma asíncrona
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnSuccessListener(getTokenResult -> {
                    String idToken = getTokenResult.getToken();
                    // Imprimir el idToken en el log para depuración
                    Log.d(TAG, "ID Token Firebase: " + idToken);
                    RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json"));

                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("Authorization", "Bearer " + idToken)
                            .post(body)
                            .build();

                    // Ejecutar llamada HTTP asincrónica
                    client.newCall(request).enqueue(callback);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error obteniendo token ID Firebase", e);
                });
    }
}
