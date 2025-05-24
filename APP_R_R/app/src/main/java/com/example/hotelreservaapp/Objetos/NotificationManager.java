package com.example.hotelreservaapp.Objetos;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationManager {
    private ArrayList<Notificaciones> listaNotificaciones;

    public NotificationManager() {
        listaNotificaciones = new ArrayList<>();
    }

    public void agregarNotificacion(String tipo, String titulo, String mensaje, Long fecha) {
        int nuevoId = listaNotificaciones.size() > 0
                ? listaNotificaciones.get(listaNotificaciones.size() - 1).getId() + 1
                : 1;
        Notificaciones nueva = new Notificaciones(nuevoId, tipo, titulo, mensaje, fecha);
        listaNotificaciones.add(nueva);
    }

    public ArrayList<Notificaciones> getListaNotificaciones() {
        return listaNotificaciones;
    }

    // Convertir lista a JSON String
    public String toJson() {
        JSONArray jsonArray = new JSONArray();
        for (Notificaciones n : listaNotificaciones) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("id", n.getId());
                obj.put("tipo", n.getTipo());
                obj.put("titulo", n.getTitulo());
                obj.put("mensaje", n.getMensaje());
                obj.put("leido", n.isLeido());
                obj.put("fecha", n.getFecha());
                jsonArray.put(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }

    // Cargar lista desde JSON String
    public void fromJson(String jsonString) {
        listaNotificaciones.clear();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                int id = obj.getInt("id");
                String titulo = obj.getString("titulo");
                String mensaje = obj.getString("mensaje");
                String tipo = obj.getString("tipo");
                Long fecha = obj.getLong("fecha");
                boolean leido = obj.getBoolean("leido");

                Notificaciones n = new Notificaciones(id, tipo, titulo, mensaje, fecha);
                n.setLeido(leido);
                listaNotificaciones.add(n);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}