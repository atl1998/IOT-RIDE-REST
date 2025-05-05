package com.example.hotelreservaapp.model;

public class Reporte {
    private String hotel;
    private String accion;
    private String usuario;
    private String fecha;

    public Reporte(String hotel, String accion, String usuario, String fecha) {
        this.hotel = hotel;
        this.accion = accion;
        this.usuario = usuario;
        this.fecha = fecha;
    }

    public String getHotel() {
        return hotel;
    }

    public String getAccion() {
        return accion;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getFecha() {
        return fecha;
    }
}
