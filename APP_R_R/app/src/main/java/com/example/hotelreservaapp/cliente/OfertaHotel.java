package com.example.hotelreservaapp.cliente;

public class OfertaHotel {
    private String nombre;
    private String ubicacion;
    private String puntuacion;
    private String calificacion;
    private String comentarios;
    private String duracion;
    private String precioOriginal;
    private String precioFinal;
    private int imagenResourceId;
    private boolean tieneOfertaEscapada;

    public OfertaHotel(String nombre, String ubicacion, String puntuacion, String calificacion,
                       String comentarios, String duracion, String precioOriginal,
                       String precioFinal, int imagenResourceId, boolean tieneOfertaEscapada) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.puntuacion = puntuacion;
        this.calificacion = calificacion;
        this.comentarios = comentarios;
        this.duracion = duracion;
        this.precioOriginal = precioOriginal;
        this.precioFinal = precioFinal;
        this.imagenResourceId = imagenResourceId;
        this.tieneOfertaEscapada = tieneOfertaEscapada;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getPuntuacion() {
        return puntuacion;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public String getComentarios() {
        return comentarios;
    }

    public String getDuracion() {
        return duracion;
    }

    public String getPrecioOriginal() {
        return precioOriginal;
    }

    public String getPrecioFinal() {
        return precioFinal;
    }

    public int getImagenResourceId() {
        return imagenResourceId;
    }

    public boolean isTieneOfertaEscapada() {
        return tieneOfertaEscapada;
    }

    public String getCalificacionCompleta() {
        return getCalificacion() + " • " + getComentarios();
    }
}
