package com.example.hotelreservaapp.taxista.model;

public class TarjetaModel {
    private String nombreUsuario;
    private String correo;
    private String telefono;
    private String fecha;
    private String hora;
    private String ubicacion;
    private String destino;
    private String estado;

    public TarjetaModel(String nombreUsuario, String correo, String telefono,
                        String fecha, String hora, String ubicacion, String destino, String estado) {
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.telefono = telefono;
        this.fecha = fecha;
        this.hora = hora;
        this.ubicacion = ubicacion;
        this.destino = destino;
        this.estado = estado;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
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

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
