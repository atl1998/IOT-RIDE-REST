package com.example.hotelreservaapp.AdminHotel.Model;

public class Servicio {
    private String nombre;
    private String descripcion;
    private double precio;
    private String url;

    public Servicio(String nombre, String descripcion, double precio, String url) {
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
        this.setPrecio(precio);
        this.setUrl(url);
    }

    public Servicio(){}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
