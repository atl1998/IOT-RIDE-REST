package com.example.hotelreservaapp.cliente;

public class Hotel {

    private String hotelId;

    private String nombre;
    private String descripcion;
    private String direccion;
    private String departamento;
    private String provincia;
    private String distrito;
    private float valoracion;
    private float precioMin;
    private boolean servicioTaxi;
    private String UrlFotoHotel;
    // Constructor

    public Hotel(String hotelId, String nombre, String descripcion, String direccion, String departamento, String provincia, String distrito, float valoracion, float precioMin, boolean servicioTaxi, String urlFotoHotel) {
        this.hotelId = hotelId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.departamento = departamento;
        this.provincia = provincia;
        this.distrito = distrito;
        this.valoracion = valoracion;
        this.precioMin = precioMin;
        this.servicioTaxi = servicioTaxi;
        UrlFotoHotel = urlFotoHotel;
    }

    //getter an setters


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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public float getValoracion() {
        return valoracion;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }

    public float getPrecioMin() {
        return precioMin;
    }

    public void setPrecioMin(float precioMin) {
        this.precioMin = precioMin;
    }

    public boolean isServicioTaxi() {
        return servicioTaxi;
    }

    public void setServicioTaxi(boolean servicioTaxi) {
        this.servicioTaxi = servicioTaxi;
    }

    public String getUrlFotoHotel() {
        return UrlFotoHotel;
    }

    public void setUrlFotoHotel(String urlFotoHotel) {
        UrlFotoHotel = urlFotoHotel;
    }
}
