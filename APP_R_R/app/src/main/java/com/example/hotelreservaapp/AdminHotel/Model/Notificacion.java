package com.example.hotelreservaapp.AdminHotel.Model;

public class Notificacion {

    private String contenido;

    public Notificacion(String contenido){
        this.setContenido(contenido);
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
