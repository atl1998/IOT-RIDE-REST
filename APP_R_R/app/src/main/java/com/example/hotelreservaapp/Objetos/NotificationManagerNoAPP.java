package com.example.hotelreservaapp.Objetos;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationManagerNoAPP {
    private ArrayList<Notificaciones> listaNotificaciones;

    public NotificationManagerNoAPP() {
        listaNotificaciones = new ArrayList<>();
    }

    public void agregarNotificacion(String tipo, String titulo, String tituloAmigable, String mensaje, String mensajeExtra, Long fecha) {
        int nuevoId = listaNotificaciones.size() > 0
                ? listaNotificaciones.get(listaNotificaciones.size() - 1).getId() + 1
                : 1;
        Notificaciones nueva = new Notificaciones(nuevoId, tipo, titulo, tituloAmigable, mensaje, mensajeExtra, fecha);
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
                obj.put("tituloAmigable", n.getTituloAmigable());
                obj.put("mensaje", n.getMensaje());
                obj.put("mensajeExtra", n.getMensajeExtra());
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
                String tituloAmigable = obj.getString("tituloAmigable");
                String mensaje = obj.getString("mensaje");
                String mensajeExtra = obj.getString("mensajeExtra");
                String tipo = obj.getString("tipo");
                Long fecha = obj.getLong("fecha");
                boolean leido = obj.getBoolean("leido");

                Notificaciones n = new Notificaciones(id, tipo, titulo, tituloAmigable, mensaje, mensajeExtra, fecha);
                n.setLeido(leido);
                listaNotificaciones.add(n);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}