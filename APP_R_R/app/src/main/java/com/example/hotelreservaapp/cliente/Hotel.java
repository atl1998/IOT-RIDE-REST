package com.example.hotelreservaapp.cliente;

public class Hotel {

    private String hotelId;

    private String nombre;
    private float valoracion;
    private String contacto;
    private String ubicacion;
    private boolean servicioTaxi;

    // Constructor

    public Hotel(String hotelId, String nombre, float valoracion, String contacto, String ubicacion, boolean servicioTaxi) {
        this.hotelId = hotelId;
        this.nombre = nombre;
        this.valoracion = valoracion;
        this.contacto = contacto;
        this.ubicacion = ubicacion;
        this.servicioTaxi = servicioTaxi;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getValoracion() {
        return valoracion;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean isServicioTaxi() {
        return servicioTaxi;
    }

    public void setServicioTaxi(boolean servicioTaxi) {
        this.servicioTaxi = servicioTaxi;
    }
}
