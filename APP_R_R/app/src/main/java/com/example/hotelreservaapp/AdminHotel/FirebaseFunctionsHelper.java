package com.example.hotelreservaapp.AdminHotel;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FirebaseFunctionsHelper {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String TAG = "FirebaseFunctionsHelper";

    public void enviarNotificacionChecjout(
            String tokenNotificacion,
            Callback callback
    ) {
        String url = "https://us-central1-riderest-baf4e.cloudfunctions.net/enviarNotificacion";

        String jsonPayload = "{"
                + "\"token\":\"" + tokenNotificacion + "\""
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
    public void enviarNotificacionTaxi(String tokenDestino) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.e("NotiTaxi", "❌ Usuario no autenticado");
            return;
        }

        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnSuccessListener(getTokenResult -> {
                    String idToken = getTokenResult.getToken();
                    Log.d("NotiTaxi", "✅ Token de autenticación: " + idToken);

                    try {
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("token", tokenDestino);

                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

                        Request request = new Request.Builder()
                                .url("https://us-central1-riderest-baf4e.cloudfunctions.net/enviarNotificacionTaxi")
                                .addHeader("Authorization", "Bearer " + idToken)
                                .post(body)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("NotiTaxi", "❌ Error al enviar", e);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try (ResponseBody responseBody = response.body()) {
                                    if (response.isSuccessful() && responseBody != null) {
                                        String respuesta = responseBody.string();
                                        Log.d("NotiTaxi", "✅ Notificación enviada: " + respuesta);
                                    } else {
                                        Log.e("NotiTaxi", "❌ Error HTTP: " + response.code());
                                    }
                                }
                            }
                        });

                    } catch (Exception e) {
                        Log.e("NotiTaxi", "❌ Error construyendo JSON", e);
                    }
                })
                .addOnFailureListener(e -> Log.e("NotiTaxi", "❌ Error al obtener token", e));
    }

}
