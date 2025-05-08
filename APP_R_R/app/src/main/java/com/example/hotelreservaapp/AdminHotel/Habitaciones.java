package com.example.hotelreservaapp.AdminHotel;

public class Habitaciones {
    private String titulo;
    private String detalles;
    private int disponibles;
    private double precio;
    private String tipoCama;
    private int tamano;

    private int seleccionadas = 0;

    public int getSeleccionadas() {
        return seleccionadas;
    }

    public void setSeleccionadas(int seleccionadas) {
        this.seleccionadas = seleccionadas;
    }

    public Habitaciones(String titulo, String detalles, int disponibles, double precio, String tipoCama, int tamano) {
        this.titulo = titulo;
        this.detalles = detalles;
        this.disponibles = disponibles;
        this.precio = precio;
        this.tipoCama = tipoCama;
        this.tamano = tamano;
    }

    // Getters
    public String getTitulo() { return titulo; }
    public String getDetalles() { return detalles; }
    public int getDisponibles() { return disponibles; }
    public double getPrecio() { return precio; }
    public String getTipoCama() { return tipoCama; }
    public int getTamano() { return tamano; }
}
