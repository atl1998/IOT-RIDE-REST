package com.example.hotelreservaapp.model;

public class DetallesTaxista {
    private String numeroPlaca;
    private String fotoPlaca;
    private String estadoSolicitud; // Estado final en el documento del taxista: "aprobado", "rechazado"

    public DetallesTaxista() {
        // Constructor vacío requerido para Firestore
    }

    public DetallesTaxista(String numeroPlaca, String fotoPlaca, String estadoSolicitud) {
        this.numeroPlaca = numeroPlaca;
        this.fotoPlaca = fotoPlaca;
        this.estadoSolicitud = estadoSolicitud;
    }

    // --- Getters y Setters ---
    public String getNumeroPlaca() {
        return numeroPlaca;
    }

    public void setNumeroPlaca(String numeroPlaca) {
        this.numeroPlaca = numeroPlaca;
    }

    public String getFotoPlaca() {
        return fotoPlaca;
    }

    public void setFotoPlaca(String fotoPlaca) {
        this.fotoPlaca = fotoPlaca;
    }

    public String getEstadoSolicitud() {
        return estadoSolicitud;
    }

    public void setEstadoSolicitud(String estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }
}

