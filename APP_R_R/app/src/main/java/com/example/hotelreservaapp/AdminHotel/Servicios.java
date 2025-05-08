package com.example.hotelreservaapp.AdminHotel;

public class Servicios {

    private String titulo;
    private String precio;

    private String url;
    private String detalles;

    public Servicios(String titulo, String precio, String url, String detalles){
        this.setTitulo(titulo);
        this.setPrecio(precio);
        this.setUrl(url);
        this.setDetalles(detalles);
    }


    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
