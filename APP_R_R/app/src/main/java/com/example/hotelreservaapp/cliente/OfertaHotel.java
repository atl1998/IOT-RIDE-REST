package com.example.hotelreservaapp.cliente;

public class OfertaHotel {
    private String hotelId;
    private String nombre;
    private double valoracion;
    private String urlFotoHotel;
    private String departamento;
    private double precioMin;

    public OfertaHotel() {
        // Constructor vacío necesario para Firestore
    }

    public OfertaHotel(String hotelId, String nombre, double valoracion, String urlFotoHotel, String departamento, double precioMin) {
        this.hotelId = hotelId;
        this.nombre = nombre;
        this.valoracion = valoracion;
        this.urlFotoHotel = urlFotoHotel;
        this.departamento = departamento;
        this.precioMin = precioMin;
    }

    public String getHotelId() {
        return hotelId;
    }


    public String getNombre() {
        return nombre;
    }

    public double getValoracion() {
        return valoracion;
    }

    public String getUrlFotoHotel() {
        return urlFotoHotel;
    }

    public String getDepartamento() {
        return departamento;
    }

    public double getPrecioMin() {
        return precioMin;
    }

    // Puedes agregar setters si necesitas modificarlos manualmente
}
