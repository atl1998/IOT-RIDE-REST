package com.example.hotelreservaapp.cliente;

import android.util.Log;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService{
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM", "Nuevo token: " + token);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("usuarios").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String rol = documentSnapshot.getString("rol");
                            if ("cliente".equals(rol)) {
                                Map<String, Object> data = new HashMap<>();
                                data.put("fcmToken", token);
                                db.collection("usuarios").document(uid)
                                        .update(data)
                                        .addOnSuccessListener(aVoid -> Log.d("FCM", "Token guardado en Firestore para usuario rol Usuario"))
                                        .addOnFailureListener(e -> Log.w("FCM", "Error guardando token", e));
                            } else {
                                Log.d("FCM", "Usuario no tiene rol 'cliente', no guardo token");
                            }
                        } else {
                            Log.d("FCM", "Documento de usuario no existe");
                        }
                    })
                    .addOnFailureListener(e -> Log.w("FCM", "Error obteniendo rol de cliente", e));
        } else {
            Log.d("FCM", "No hay cliente logueado, no se guarda token");
        }
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Manejo de notificación cuando app está en primer plano
        if (remoteMessage.getNotification() != null) {
            Log.d("FCM", "Título: " + remoteMessage.getNotification().getTitle());
            Log.d("FCM", "Cuerpo: " + remoteMessage.getNotification().getBody());
        }
    }
}
