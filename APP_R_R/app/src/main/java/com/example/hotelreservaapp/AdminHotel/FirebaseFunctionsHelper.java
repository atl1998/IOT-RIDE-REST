package com.example.hotelreservaapp.AdminHotel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

public class FirebaseFunctionsHelper {
    private FirebaseFunctions functions;

    public FirebaseFunctionsHelper() {
        functions = FirebaseFunctions.getInstance();
    }

    public Task<String> enviarNotificacion(String token, String titulo, String cuerpo) {
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("titulo", titulo);
        data.put("cuerpo", cuerpo);

        return functions
                .getHttpsCallable("enviarNotificacion")
                .call(data)
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
                    Boolean success = (Boolean) result.get("success");
                    if (success) {
                        return "Notificación enviada correctamente";
                    } else {
                        throw new Exception("Error al enviar notificación");
                    }
                });
    }
}
