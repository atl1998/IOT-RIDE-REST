package com.example.hotelreservaapp.AdminHotel;

public class Habitaciones {
    private String titulo;
    private String detalles;
    private int disponibles;
    private double precio;
    private String tipoCama;
    private int tamano;

    private String url;

    private int seleccionadas = 0;

    public int getSeleccionadas() {
        return seleccionadas;
    }

    public void setSeleccionadas(int seleccionadas) {
        this.seleccionadas = seleccionadas;
    }

    public Habitaciones(String titulo, String detalles, int disponibles, double precio, String tipoCama, int tamano, String url) {
        this.setTitulo(titulo);
        this.setDetalles(detalles);
        this.setDisponibles(disponibles);
        this.setPrecio(precio);
        this.setTipoCama(tipoCama);
        this.setTamano(tamano);
        this.setUrl(url);
    }

    public Habitaciones() {

    }

    // Getters
    public String getTitulo() { return titulo; }
    public String getDetalles() { return detalles; }
    public int getDisponibles() { return disponibles; }
    public double getPrecio() { return precio; }
    public String getTipoCama() { return tipoCama; }
    public int getTamano() { return tamano; }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public void setDisponibles(int disponibles) {
        this.disponibles = disponibles;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setTipoCama(String tipoCama) {
        this.tipoCama = tipoCama;
    }

    public void setTamano(int tamano) {
        this.tamano = tamano;
    }
}
