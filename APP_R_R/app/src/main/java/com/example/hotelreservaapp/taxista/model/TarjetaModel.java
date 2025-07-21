package com.example.hotelreservaapp.taxista.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

@IgnoreExtraProperties
public class TarjetaModel {

    private String idCliente;

    private String nombreCliente;
    private String correoCliente;
    private String telefonoCliente;

    @PropertyName("urlFotoPerfil")
    private String fotoCliente;

    private String direccionHotel;  // <- campo que Firestore envía como "direccionHotel"
    private String destino;
    private String fecha;
    private String hora;
    private String estado;
    private long timestamp;

    // Datos del taxista (opcionales)
    private String nombreTaxista;
    private String correoTaxista;
    private String telefonoTaxista;
    private String placaVehiculo;
    private String fotoTaxista;
    private String fotoVehiculo;

    private String horaLlegadaEstimada;
    private String horaFinalizacion;

    private double latOrigen;
    private double lngOrigen;
    private double latDestino;
    private double lngDestino;

    public TarjetaModel() {
        // Requerido por Firestore
    }

    // Getters y Setters
    // Getters & setters
    public double getLatOrigen() { return latOrigen; }
    public void setLatOrigen(double latOrigen) { this.latOrigen = latOrigen; }

    public double getLngOrigen() { return lngOrigen; }
    public void setLngOrigen(double lngOrigen) { this.lngOrigen = lngOrigen; }

    public double getLatDestino() { return latDestino; }
    public void setLatDestino(double latDestino) { this.latDestino = latDestino; }

    public double getLngDestino() { return lngDestino; }
    public void setLngDestino(double lngDestino) { this.lngDestino = lngDestino; }
    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getCorreoCliente() {
        return correoCliente;
    }

    public void setCorreoCliente(String correoCliente) {
        this.correoCliente = correoCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    @PropertyName("urlFotoPerfil")
    public String getFotoCliente() {
        return fotoCliente;
    }

    @PropertyName("urlFotoPerfil")
    public void setFotoCliente(String fotoCliente) {
        this.fotoCliente = fotoCliente;
    }

    // Este es el campo esperado por Firestore como "direccionHotel"
    @PropertyName("direccionHotel")
    public String getUbicacionOrigen() {
        return direccionHotel;
    }

    @PropertyName("direccionHotel")
    public void setUbicacionOrigen(String direccionHotel) {
        this.direccionHotel = direccionHotel;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNombreTaxista() {
        return nombreTaxista;
    }

    public void setNombreTaxista(String nombreTaxista) {
        this.nombreTaxista = nombreTaxista;
    }

    public String getCorreoTaxista() {
        return correoTaxista;
    }

    public void setCorreoTaxista(String correoTaxista) {
        this.correoTaxista = correoTaxista;
    }

    public String getTelefonoTaxista() {
        return telefonoTaxista;
    }

    public void setTelefonoTaxista(String telefonoTaxista) {
        this.telefonoTaxista = telefonoTaxista;
    }

    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }

    public String getFotoTaxista() {
        return fotoTaxista;
    }

    public void setFotoTaxista(String fotoTaxista) {
        this.fotoTaxista = fotoTaxista;
    }

    public String getFotoVehiculo() {
        return fotoVehiculo;
    }

    public void setFotoVehiculo(String fotoVehiculo) {
        this.fotoVehiculo = fotoVehiculo;
    }

    public String getHoraLlegadaEstimada() {
        return horaLlegadaEstimada;
    }

    public void setHoraLlegadaEstimada(String horaLlegadaEstimada) {
        this.horaLlegadaEstimada = horaLlegadaEstimada;
    }

    public String getHoraFinalizacion() {
        return horaFinalizacion;
    }

    public void setHoraFinalizacion(String horaFinalizacion) {
        this.horaFinalizacion = horaFinalizacion;
    }
}
