package com.example.hotelreservaapp.model;

public class LogItem {
    private String fecha;
    private String hora;
    private String usuario;
    private String accion;
    private int fotoResId; // recurso drawable local


    public LogItem(String fecha, String hora, String usuario, String accion,int fotoResId) {
        this.fecha = fecha;
        this.hora = hora;
        this.usuario = usuario;
        this.accion = accion;
        this.fotoResId = fotoResId;
    }

    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getUsuario() { return usuario; }
    public String getAccion() { return accion; }
    public int getFotoResId() {
        return fotoResId;
    }

}