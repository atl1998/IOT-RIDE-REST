package com.example.hotelreservaapp.model;

public class Taxista extends Usuario {
    public Taxista(String nombre, String apellido, String rol, String tipoDocumento, String numeroDocumento,
                   String fechaNacimiento, String correo, String telefono, String direccion, String urlFotoPerfil,
                   boolean estado, boolean requiereCambioContrasena, DetallesTaxista driverDetails) {
        super(nombre, apellido, rol, tipoDocumento, numeroDocumento, fechaNacimiento, correo, telefono,
                direccion, urlFotoPerfil, estado, requiereCambioContrasena, driverDetails);
        // Asegúrate de que el rol sea "taxista" al crear una instancia de Taxista
        this.setRol("taxista");
    }

    public Taxista() {
        // Constructor vacío requerido, también llama al de la clase padre
        super();
        this.setRol("taxista"); // Asegura el rol por defecto si se crea sin argumentos
    }
}
