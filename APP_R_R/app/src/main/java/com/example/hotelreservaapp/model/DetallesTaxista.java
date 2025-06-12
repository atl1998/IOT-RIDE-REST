package com.example.hotelreservaapp.model;

public class DetallesTaxista {
    private String numeroPlaca;
    private String fotoPlaca;

    public DetallesTaxista() {
        // Constructor vacío requerido para Firestore
    }

    public DetallesTaxista(String numeroPlaca, String fotoPlaca) {
        this.numeroPlaca = numeroPlaca;
        this.fotoPlaca = fotoPlaca;
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

}

