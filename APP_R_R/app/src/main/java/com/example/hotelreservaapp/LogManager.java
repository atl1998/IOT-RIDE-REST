package com.example.hotelreservaapp;


import com.example.hotelreservaapp.model.LogItem;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogManager {

    public static void registrarLogRegistro(String usuario, String accion, String detalle) {
        LogItem log = new LogItem(usuario, accion, detalle);
        FirebaseFirestore.getInstance()
                .collection("logs")
                .add(log);
    }
}