package com.example.hotelreservaapp.cliente;

import java.io.Serializable;

public class Hotel implements Serializable {
    private String nombre;
    private float rating;
    private String puntuacion;
    private String ubicacion;
    private String fechas;
    private String precio;
    private int imagen;

    public Hotel(String nombre, float rating, String puntuacion, String ubicacion, String fechas, String precio, int imagen) {
        this.nombre = nombre;
        this.rating = rating;
        this.puntuacion = puntuacion;
        this.ubicacion = ubicacion;
        this.fechas = fechas;
        this.precio = precio;
        this.imagen = imagen;
    }

    // Getters
    public String getNombre() { return nombre; }
    public float getRating() { return rating; }
    public String getPuntuacion() { return puntuacion; }
    public String getUbicacion() { return ubicacion; }
    public String getFechas() { return fechas; }
    public String getPrecio() { return precio; }
    public int getImagen() { return imagen; }
}
