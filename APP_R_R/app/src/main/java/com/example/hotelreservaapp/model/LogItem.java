package com.example.hotelreservaapp.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LogItem {
    private String usuario;
    private String accion;
    private String detalle;

    @ServerTimestamp
    private Date fecha; // ⏰ Será asignada automáticamente por Firestore

    public LogItem() {
        // Constructor vacío requerido por Firestore
    }

    public LogItem(String usuario, String accion, String detalle) {
        this.usuario = usuario;
        this.accion = accion;
        this.detalle = detalle;
    }

    // Getters y setters

    public String getUsuario() {
        return usuario;
    }

    public String getAccion() {
        return accion;
    }

    public String getDetalle() {
        return detalle;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    //Métodos adicionales para el adapter
    public String getFechaFormateada() {
        if (fecha == null) return "¿?";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(fecha);
    }

    public String getHoraFormateada() {
        if (fecha == null) return "¿?";
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(fecha);
    }
}