package com.example.hotelreservaapp.model;

public class Usuario {
    private String nombre;  // Nombre del usuario
    private String apellido;  // Apellido del usuario
    private String rol;  // Rol del usuario (ej. "taxista", "usuario")
    private String tipoDocumento;  // Tipo de documento (ej. DNI, Pasaporte)
    private String numeroDocumento;  // Número de documento
    private String fechaNacimiento;  // Fecha de nacimiento
    private String correo;  // Correo electrónico
    private String telefono;  // Teléfono del usuario
    private String direccion;  // Dirección del usuario
    private String urlFotoPerfil;  // URL de la foto de perfil
    private boolean estado;  // Estado del usuario (activo o inactivo)
    private boolean requiereCambioContrasena;  // Indica si requiere cambio de contraseña

    // Atributos adicionales para los taxistas (si aplica)
    private String placaVehiculo;  // Placa del vehículo (solo para taxistas)
    private String imagenVehiculo;  // Imagen del vehículo (solo para taxistas)

    // Constructor de la clase Usuario
    public Usuario(String nombre, String apellido, String rol, String tipoDocumento, String numeroDocumento,
                   String fechaNacimiento, String correo, String telefono, String direccion, String urlFotoPerfil, boolean estado,
                   boolean requiereCambioContrasena, String placaVehiculo, String imagenVehiculo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.fechaNacimiento = fechaNacimiento;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.urlFotoPerfil = urlFotoPerfil;
        this.estado = estado;
        this.requiereCambioContrasena = requiereCambioContrasena;
        this.placaVehiculo = placaVehiculo;  // Solo se llena si es taxista
        this.imagenVehiculo = imagenVehiculo; // Solo se llena si es taxista
    }

    // Constructor vacío para Firebase y otras instancias donde se necesita
    public Usuario() {}

    // Métodos Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getUrlFotoPerfil() {
        return urlFotoPerfil;
    }

    public void setUrlFotoPerfil(String urlFotoPerfil) {
        this.urlFotoPerfil = urlFotoPerfil;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public boolean isRequiereCambioContrasena() {
        return requiereCambioContrasena;
    }

    public void setRequiereCambioContrasena(boolean requiereCambioContrasena) {
        this.requiereCambioContrasena = requiereCambioContrasena;
    }

    // Métodos específicos para Taxista (solo si el rol es "taxista")
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
