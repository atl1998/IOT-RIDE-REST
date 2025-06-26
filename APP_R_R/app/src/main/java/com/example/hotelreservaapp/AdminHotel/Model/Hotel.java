package com.example.hotelreservaapp.AdminHotel.Model;

import java.util.List;

public class Hotel {
    private String Nombre;
    private String Descripcion;
    private String Departamento;
    private String Provincia;
    private String Distrito;
    private String Direccion;
    private String urlImage;
    private double valoracion;

    private List<Habitacion> habitaciones; // Lista de habitaciones
    private List<Servicio> servicios; // Lista de servicios

    public Hotel() {}
    public Hotel(String nombre, String descripcion, String departamento, String provincia, String distrito, String direccion, String urlImage, double valoracion, List<Habitacion> habitaciones, List<Servicio> servicios ) {
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
        this.setDepartamento(departamento);
        this.setProvincia(provincia);
        this.setDistrito(distrito);
        this.setDireccion(direccion);
        this.setUrlImage(urlImage);
        this.setValoracion(valoracion);
        this.setHabitaciones(habitaciones);
        this.setServicios(servicios);
    }

    public Hotel(String nombre, String descripcion, String departamento, String provincia, String distrito, String direccion, String urlImage, double valoracion ) {
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
        this.setDepartamento(departamento);
        this.setProvincia(provincia);
        this.setDistrito(distrito);
        this.setDireccion(direccion);
        this.setUrlImage(urlImage);
        this.setValoracion(valoracion);
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

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
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
}
