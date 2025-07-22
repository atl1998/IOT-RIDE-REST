package com.example.hotelreservaapp.AdminHotel.Model;


import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;

@IgnoreExtraProperties
public class ReservaInicio implements Serializable {
    private String idusuario;
    private Date fechainiciocheckin;
    private String idreserva;

    private double pagohabitacion;
    private String urlFoto;

    // Campo transitorio para mostrar nombre completo del usuario
    private String nombreCompleto;

    public ReservaInicio() {
        // Constructor vacío requerido por Firestore
    }

    public ReservaInicio(String idusuario, Date fechainiciocheckin, String idreserva, double pagohabitacion) {
        this.idusuario = idusuario;
        this.fechainiciocheckin = fechainiciocheckin;
        this.setIdreserva(idreserva);
        this.pagohabitacion = pagohabitacion;
    }

    public String getIdUsuario() {
        return idusuario;
    }

    public void setIdUsuario(String idusuario) {
        this.idusuario = idusuario;
    }

    public Date getFechaInicioCheckIn() {
        return fechainiciocheckin;
    }

    public void setFechaInicioCheckIn(Date fechainiciocheckin) {
        this.fechainiciocheckin = fechainiciocheckin;
    }

    public double getPago() {
        return pagohabitacion;
    }

    public void setPago(double pagohabitacion) {
        this.pagohabitacion = pagohabitacion;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }


    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getIdreserva() {
        return idreserva;
    }

    public void setIdreserva(String idreserva) {
        this.idreserva = idreserva;
    }
}