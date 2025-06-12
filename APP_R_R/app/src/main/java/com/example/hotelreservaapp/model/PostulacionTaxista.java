package com.example.hotelreservaapp.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class PostulacionTaxista implements Serializable {
    private String nombres;
    private String apellidos;
    private String tipoDocumento;
    private String numeroDocumento;
    private String fechaNacimiento;
    private String correo;
    private String telefono;
    private String direccion;
    private String urlFotoPerfil;
    private String numeroPlaca; // Mapea a 'placa' del intent
    private String fotoPlacaURL; // Mapea a 'fotoVehiculo' del intent
    private String estadoSolicitud; // Por defecto "pendiente_revision"
    @ServerTimestamp
    private Date fechaPostulacion; // La fecha en que se envió la postulación
    private String notasAdministrador; // Para que el admin agregue comentarios
    private Date fechaRevision; // Fecha en que el admin revisó
    private String idUsuarioAsignado; // UID del usuario si la postulación es aprobada

    public PostulacionTaxista() {
        // Constructor vacío requerido para Firestore
    }

    public PostulacionTaxista(String nombres, String apellidos, String tipoDocumento, String numeroDocumento,
                              String fechaNacimiento, String correo, String telefono, String direccion,
                              String numeroPlaca, String fotoPlacaURL, String estadoSolicitud) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.fechaNacimiento = fechaNacimiento;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.numeroPlaca = numeroPlaca;
        this.fotoPlacaURL = fotoPlacaURL;
        this.estadoSolicitud = estadoSolicitud;
        // La fechaPostulacion se seteará automáticamente por @ServerTimestamp al guardar en Firestore
    }

    // --- Getters y Setters para todos los campos ---
    // Puedes generar estos automáticamente en Android Studio (Click derecho -> Generate -> Getter and Setter)

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getUrlFotoPerfil() {
        return urlFotoPerfil;
    }

    public void setUrlFotoPerfil(String urlFotoPerfil) {
        this.urlFotoPerfil = urlFotoPerfil;
    }
    public String getNumeroPlaca() { return numeroPlaca; }
    public void setNumeroPlaca(String numeroPlaca) { this.numeroPlaca = numeroPlaca; }

    public String getFotoPlacaURL() { return fotoPlacaURL; }
    public void setFotoPlacaURL(String fotoPlacaURL) { this.fotoPlacaURL = fotoPlacaURL; }

    public String getEstadoSolicitud() { return estadoSolicitud; }
    public void setEstadoSolicitud(String estadoSolicitud) { this.estadoSolicitud = estadoSolicitud; }

    public Date getFechaPostulacion() { return fechaPostulacion; }
    public void setFechaPostulacion(Date fechaPostulacion) { this.fechaPostulacion = fechaPostulacion; }

    public String getNotasAdministrador() { return notasAdministrador; }
    public void setNotasAdministrador(String notasAdministrador) { this.notasAdministrador = notasAdministrador; }

    public Date getFechaRevision() { return fechaRevision; }
    public void setFechaRevision(Date fechaRevision) { this.fechaRevision = fechaRevision; }

    public String getIdUsuarioAsignado() { return idUsuarioAsignado; }
    public void setIdUsuarioAsignado(String idUsuarioAsignado) { this.idUsuarioAsignado = idUsuarioAsignado; }
}