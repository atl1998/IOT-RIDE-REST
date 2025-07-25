package com.example.hotelreservaapp.taxista;

import android.util.Log;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONObject;

import java.io.IOException;

public class TaxistaPushNotification {
    private OkHttpClient client = new OkHttpClient();

    public void enviarNotificacionAlCliente(String tokenCliente, String nombreTaxista) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            System.err.println("Usuario no autenticado");
            return;
        }

        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnSuccessListener(getTokenResult -> {
                    String idToken = getTokenResult.getToken();
                    Log.d("TaxistaFCM", "El token de autenticación es: " + idToken);
                    try {
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("tokenCliente", tokenCliente);
                        jsonBody.put("nombreTaxista", nombreTaxista);

                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

                        Request request = new Request.Builder()
                                .url("https://us-central1-riderest-baf4e.cloudfunctions.net/notificarTaxista")
                                .addHeader("Authorization", "Bearer " + idToken)
                                .post(body)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try (ResponseBody responseBody = response.body()) {
                                    if (response.isSuccessful() && responseBody != null) {
                                        String respuesta = responseBody.string();
                                        Log.d("Notificacion", "Notificación enviada: " + respuesta);
                                    } else {
                                        System.err.println("Error al enviar notificación: " + response.code());
                                    }
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }

    public void notificarViajeRechazado(String tokenCliente, String nombreTaxista) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnSuccessListener(getTokenResult -> {
                    String idToken = getTokenResult.getToken();

                    try {
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("tokenCliente", tokenCliente);
                        jsonBody.put("nombreTaxista", nombreTaxista);

                        RequestBody body = RequestBody.create(
                                jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));

                        Request request = new Request.Builder()
                                .url("https://us-central1-riderest-baf4e.cloudfunctions.net/notificarViajeRechazado")
                                .addHeader("Authorization", "Bearer " + idToken)
                                .post(body)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override public void onFailure(Call call, IOException e) { e.printStackTrace(); }
                            @Override public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    Log.d("Notificacion", "✅ Rechazo enviado: " + response.body().string());
                                } else {
                                    Log.e("Notificacion", "❌ Error al enviar rechazo: " + response.code());
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).addOnFailureListener(Throwable::printStackTrace);
    }

    public void notificarEsperando(String tokenCliente) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnSuccessListener(getTokenResult -> {
                    String idToken = getTokenResult.getToken();

                    try {
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("tokenCliente", tokenCliente);

                        RequestBody body = RequestBody.create(
                                jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));

                        Request request = new Request.Builder()
                                .url("https://us-central1-riderest-baf4e.cloudfunctions.net/notificarEsperando")
                                .addHeader("Authorization", "Bearer " + idToken)
                                .post(body)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override public void onFailure(Call call, IOException e) { e.printStackTrace(); }
                            @Override public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    Log.d("Notificacion", "✅ Esperando afuera enviado: " + response.body().string());
                                } else {
                                    Log.e("Notificacion", "❌ Error esperando afuera: " + response.code());
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).addOnFailureListener(Throwable::printStackTrace);
    }

    public void notificarEnProgreso(String tokenCliente) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnSuccessListener(getTokenResult -> {
                    String idToken = getTokenResult.getToken();

                    try {
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("tokenCliente", tokenCliente);

                        RequestBody body = RequestBody.create(
                                jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));

                        Request request = new Request.Builder()
                                .url("https://us-central1-riderest-baf4e.cloudfunctions.net/notificarEnProgreso")
                                .addHeader("Authorization", "Bearer " + idToken)
                                .post(body)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override public void onFailure(Call call, IOException e) { e.printStackTrace(); }
                            @Override public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    Log.d("Notificacion", "✅ En progreso enviado: " + response.body().string());
                                } else {
                                    Log.e("Notificacion", "❌ Error al iniciar viaje: " + response.code());
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).addOnFailureListener(Throwable::printStackTrace);
    }

    public void notificarFinalizado(String tokenCliente) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnSuccessListener(getTokenResult -> {
                    String idToken = getTokenResult.getToken();

                    try {
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("tokenCliente", tokenCliente);

                        RequestBody body = RequestBody.create(
                                jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));

                        Request request = new Request.Builder()
                                .url("https://us-central1-riderest-baf4e.cloudfunctions.net/notificarFinalizado")
                                .addHeader("Authorization", "Bearer " + idToken)
                                .post(body)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override public void onFailure(Call call, IOException e) { e.printStackTrace(); }
                            @Override public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    Log.d("Notificacion", "✅ Finalizado enviado: " + response.body().string());
                                } else {
                                    Log.e("Notificacion", "❌ Error finalizando viaje: " + response.code());
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).addOnFailureListener(Throwable::printStackTrace);
    }


}
