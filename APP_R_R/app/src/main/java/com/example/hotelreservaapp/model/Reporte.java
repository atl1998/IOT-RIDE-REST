package com.example.hotelreservaapp.model;

public class Reporte {
    private String hotel;
    private String cliente;
    private String fecha;
    private String estado;
    private int imagenResId; // ← referencia al drawable

    public Reporte(String hotel, String cliente, String fecha, String estado, int imagenResId) {
        this.hotel = hotel;
        this.cliente = cliente;
        this.fecha = fecha;
        this.estado = estado;
        this.imagenResId = imagenResId;
    }

    public String getHotel() {
        return hotel;
    }

    public String getCliente() {
        return cliente;
    }

    public String getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public int getImagenResId() {
        return imagenResId;
    }
}
