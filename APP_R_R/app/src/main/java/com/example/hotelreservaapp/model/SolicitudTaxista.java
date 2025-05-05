package com.example.hotelreservaapp.model;

import java.io.Serializable;

public class SolicitudTaxista implements Serializable {
    private String nombre;
    private String apellido;
    private String tipoDoc;
    private String numDoc;
    private String fechaNacimiento;
    private String telefono;
    private String domicilio;
    private String correo;
    private String placa;
    private String fotoUsuario;
    private String fotoPlaca;

    public SolicitudTaxista(String nombre, String apellido, String tipoDoc, String numDoc,
                            String fechaNacimiento, String telefono, String domicilio,
                            String correo, String placa, String fotoUsuario, String fotoPlaca) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoDoc = tipoDoc;
        this.numDoc = numDoc;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.domicilio = domicilio;
        this.correo = correo;
        this.placa = placa;
        this.fotoUsuario = fotoUsuario;
        this.fotoPlaca = fotoPlaca;
    }

    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getTipoDoc() { return tipoDoc; }
    public String getNumDoc() { return numDoc; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getTelefono() { return telefono; }
    public String getDomicilio() { return domicilio; }
    public String getCorreo() { return correo; }
    public String getPlaca() { return placa; }
    public String getFotoUsuario() { return fotoUsuario; }
    public String getFotoPlaca() { return fotoPlaca; }
}
