package com.example.hotelreservaapp.AdminHotel.Model;

import java.util.ArrayList;
import java.util.List;

public class HotelSinListas {
    private String Nombre;
    private String Descripcion;
    private String Departamento;
    private String Provincia;
    private String Distrito;
    private String Direccion;
    private String UrlFotoHotel;
    private String contacto;
    private double precioMin;
    private double valoracion = 0.0;
    private boolean servicioTaxi;

    private String idAdminHotel;



    public HotelSinListas(String nombre, String descripcion, String departamento, String provincia, String distrito, String direccion, String UrlFotoHotel, double valoracion, String contacto, double precioMin, boolean servicioTaxi, String idAdminHotel ) {
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
        this.setDepartamento(departamento);
        this.setProvincia(provincia);
        this.setDistrito(distrito);
        this.setDireccion(direccion);
        this.setUrlFotoHotel(UrlFotoHotel);
        this.setValoracion(valoracion);
        this.setContacto(contacto);
        this.setPrecioMin(precioMin);
        this.setServicioTaxi(servicioTaxi);
        this.setIdAdminHotel(idAdminHotel);
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getDepartamento() {
        return Departamento;
    }

    public void setDepartamento(String departamento) {
        Departamento = departamento;
    }

    public String getProvincia() {
        return Provincia;
    }

    public void setProvincia(String provincia) {
        Provincia = provincia;
    }

    public String getDistrito() {
        return Distrito;
    }

    public void setDistrito(String distrito) {
        Distrito = distrito;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getUrlFotoHotel() {
        return UrlFotoHotel;
    }

    public void setUrlFotoHotel(String UrlFotoHotel) {
        this.UrlFotoHotel = UrlFotoHotel;
    }

    public double getValoracion() {
        return valoracion;
    }

    public void setValoracion(double valoracion) {
        this.valoracion = valoracion;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public double getPrecioMin() {
        return precioMin;
    }

    public void setPrecioMin(double precioMin) {
        this.precioMin = precioMin;
    }

    public boolean getServicioTaxi() {
        return servicioTaxi;
    }

    public void setServicioTaxi(boolean servicioTaxi) {
        this.servicioTaxi = servicioTaxi;
    }

    public String getIdAdminHotel() {
        return idAdminHotel;
    }

    public void setIdAdminHotel(String idAdminHotel) {
        this.idAdminHotel = idAdminHotel;
    }
}
