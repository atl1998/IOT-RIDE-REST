package com.example.hotelreservaapp.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Usuario {
    private String nombre;
    private String apellido;
    private String rol;
    private String tipoDocumento;
    private String numeroDocumento;
    private String fechaNacimiento;
    private String correo;
    private String telefono;
    private String direccion;
    private String urlFotoPerfil; // URL de la imagen o nombre del recurso local
    private boolean estado;
    public boolean requiereCambioContrasena;
    @ServerTimestamp
    private Date fechaCreacion;
    private DetallesTaxista driverDetails;
    public Usuario(String nombre, String apellido, String rol, String tipoDocumento, String numeroDocumento,
                   String fechaNacimiento, String correo, String telefono, String direccion, String urlFotoPerfil, boolean estado, boolean requiereCambioContrasena) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.fechaNacimiento = fechaNacimiento;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.urlFotoPerfil = urlFotoPerfil;
        this.estado = estado;
        this.requiereCambioContrasena = requiereCambioContrasena;
        // driverDetails será null por defecto

    }
    public Usuario() {}
    // Constructor, getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getUrlFotoPerfil() {
        return urlFotoPerfil;
    }

    public void setUrlFotoPerfil(String urlFotoPerfil) {
        this.urlFotoPerfil = urlFotoPerfil;
    }
    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    public boolean isRequiereCambioContrasena() {
        return requiereCambioContrasena;
    }

    public void setRequiereCambioContrasena(boolean requiereCambioContrasena) {
        this.requiereCambioContrasena = requiereCambioContrasena;
    }
    public DetallesTaxista getDriverDetails() { return driverDetails; }
    public void setDriverDetails(DetallesTaxista driverDetails) { this.driverDetails = driverDetails; }
}
