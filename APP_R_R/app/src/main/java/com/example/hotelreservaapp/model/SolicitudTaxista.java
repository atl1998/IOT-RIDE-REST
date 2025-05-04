package com.example.hotelreservaapp.model;

import java.io.Serializable;

public class SolicitudTaxista implements Serializable {
    private String nombre;
    private String correo;
    private String placa;
    private String fotoUsuario;   // nombre en assets
    private String fotoPlaca;    // nombre en assets

    public SolicitudTaxista(String nombre, String correo, String placa, String fotoUsuario, String fotoPlaca) {
        this.nombre = nombre;
        this.correo = correo;
        this.placa = placa;
        this.fotoUsuario = fotoUsuario;
        this.fotoPlaca = fotoPlaca;
    }

    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getPlaca() { return placa; }
    public String getFotoUsuario() { return fotoUsuario; }
    public String getFotoPlaca() { return fotoPlaca; }
}
