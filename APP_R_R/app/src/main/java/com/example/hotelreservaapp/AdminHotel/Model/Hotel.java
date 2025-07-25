package com.example.hotelreservaapp.AdminHotel.Model;

import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private String Nombre;
    private String Descripcion;
    private String Departamento;
    private String Provincia;
    private String Distrito;
    private String Direccion;
    private String UrlFotoHotel;
    private String contacto;

    private String idAdminHotel;
    private double precioMin;
    private double valoracion = 0.0;
    private boolean servicioTaxi;

    private List<Habitacion> habitaciones = new ArrayList<>(); // Lista de habitaciones
    private List<Servicio> servicios = new ArrayList<>(); // Lista de servicios

    public Hotel() {}
    public Hotel(String nombre, String descripcion, String departamento, String provincia, String distrito, String direccion, String UrlFotoHotel, double valoracion, List<Habitacion> habitaciones, List<Servicio> servicios, String contacto, double precioMin, boolean servicioTaxi ) {
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
        this.setDepartamento(departamento);
        this.setProvincia(provincia);
        this.setDistrito(distrito);
        this.setDireccion(direccion);
        this.setUrlFotoHotel(UrlFotoHotel);
        this.setValoracion(valoracion);
        this.setHabitaciones(habitaciones);
        this.setServicios(servicios);
        this.setContacto(contacto);
        this.setPrecioMin(precioMin);
        this.setServicioTaxi(servicioTaxi);
    }

    public Hotel(String nombre, String descripcion, String departamento, String provincia, String distrito, String direccion, String UrlFotoHotel, double valoracion, String contacto, double precioMin, boolean servicioTaxi, List<Habitacion> habitaciones, List<Servicio> servicios ) {
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
        this.setHabitaciones(habitaciones);
        this.setServicios(servicios);
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

    public List<Habitacion> getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(List<Habitacion> habitaciones) {
        this.habitaciones = habitaciones;
    }

    public double getValoracion() {
        return valoracion;
    }

    public void setValoracion(double valoracion) {
        this.valoracion = valoracion;
    }

    public List<Servicio> getServicios() {
        return servicios;
    }

    public void setServicios(List<Servicio> servicios) {
        this.servicios = servicios;
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
