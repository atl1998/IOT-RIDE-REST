package com.example.hotelreservaapp.AdminHotel.Model;


import java.util.Date;

public class ReporteUsuario {
    private String nombre;
    private double totalGastado;
    private Date creadoEn;
    private String usuarioId;
    private String idAdminHotel;

    public ReporteUsuario() {}  // requerido por Firestore

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getTotalGastado() { return totalGastado; }
    public void setTotalGastado(double totalGastado) { this.totalGastado = totalGastado; }

    public Date getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Date creadoEn) { this.creadoEn = creadoEn; }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getIdAdminHotel() {
        return idAdminHotel;
    }

    public void setIdAdminHotel(String idAdminHotel) {
        this.idAdminHotel = idAdminHotel;
    }
}
