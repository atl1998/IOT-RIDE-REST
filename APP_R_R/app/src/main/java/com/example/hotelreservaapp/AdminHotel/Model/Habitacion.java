package com.example.hotelreservaapp.AdminHotel.Model;

public class Habitacion {
    private String tipo;
    private double precio;
    private double tamano;

    private String capacidad;
    private String url;


    public Habitacion(String tipo, double precio, double tamano, String capacidad,  String url) {
        this.setTipo(tipo);
        this.setPrecio(precio);
        this.setTamano(tamano);
        this.setCapacidad(capacidad);
        this.setUrl(url);
    }

    public Habitacion() {

    }

    public String getTipo() {return tipo;}

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public double getTamano() { return tamano; }

    public void setTamano(double tamano) {
        this.tamano = tamano;
    }

    public String getCapacidad() {return capacidad;}

    public void setCapacidad(String capacidad) {this.capacidad = capacidad;}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
