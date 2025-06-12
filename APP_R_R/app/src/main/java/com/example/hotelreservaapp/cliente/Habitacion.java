package com.example.hotelreservaapp.cliente;

public class Habitacion {
    private String nombre;
    private int cantDisponible;
    private double precio;
    private int seleccionadas = 0;

    private String idDocumento;  // <--- Nuevo campo

    public Habitacion(String nombre, int cantDisponible, double precio) {
        this.nombre = nombre;
        this.cantDisponible = cantDisponible;
        this.precio = precio;
    }

    public Habitacion(String idDocumento, String nombre, int cantDisponible, double precio) {
        this.idDocumento = idDocumento;
        this.nombre = nombre;
        this.cantDisponible = cantDisponible;
        this.precio = precio;
    }

    // Getters y setters

    public String getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(String idDocumento) {
        this.idDocumento = idDocumento;
    }

    public int getSeleccionadas() {
        return seleccionadas;
    }

    public void setSeleccionadas(int seleccionadas) {
        this.seleccionadas = seleccionadas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantDisponible() {
        return cantDisponible;
    }

    public void setCantDisponible(int cantDisponible) {
        this.cantDisponible = cantDisponible;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}

