package com.example.hotelreservaapp.model;

public class Taxista extends Usuario {
    private String placaVehiculo;  // Placa del vehículo (solo para taxistas)
    private String imagenVehiculo; // URL de la imagen del vehículo (solo para taxistas)

    // Constructor
    public Taxista(String nombre, String apellido, String tipoDocumento, String numeroDocumento,
                   String fechaNacimiento, String correo, String telefono, String direccion, String urlFotoPerfil,
                   boolean estado, boolean requiereCambioContrasena, String placaVehiculo, String imagenVehiculo) {
        // Llamada al constructor de la clase base (Usuario)
        super(nombre, apellido, "taxista", tipoDocumento, numeroDocumento, fechaNacimiento, correo, telefono, direccion, urlFotoPerfil, estado, requiereCambioContrasena);
        this.placaVehiculo = placaVehiculo;
        this.imagenVehiculo = imagenVehiculo;
    }

    // Métodos Getters y Setters
    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }

    public String getImagenVehiculo() {
        return imagenVehiculo;
    }

    public void setImagenVehiculo(String imagenVehiculo) {
        this.imagenVehiculo = imagenVehiculo;
    }
}
