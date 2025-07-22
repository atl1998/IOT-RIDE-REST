package com.example.hotelreservaapp.model;

public class Reporte {
    private String hotel;
    private String cliente;
    private String fecha;
    private String estado;
    private int imagenResId; // ← referencia al drawable
    private String checkIn;
    private String checkOut;
    private String habitacion;
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
    public String getCheckIn() { return checkIn; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }

    public String getCheckOut() { return checkOut; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }

    public String getHabitacion() { return habitacion; }
    public void setHabitacion(String habitacion) { this.habitacion = habitacion; }
}
