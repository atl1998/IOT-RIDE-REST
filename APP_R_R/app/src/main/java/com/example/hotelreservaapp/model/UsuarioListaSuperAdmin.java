package com.example.hotelreservaapp.model;

public class UsuarioListaSuperAdmin {
    private String nombre;
    private String correo;
    private String rol;
    private boolean activo;
    private String urlFoto;

    public UsuarioListaSuperAdmin(String nombre, String correo, String rol, String urlFoto, boolean activo) {
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.urlFoto = urlFoto;
        this.activo = activo;
    }

    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getRol() { return rol; }
    public boolean isActivo() { return activo; }
    public String getUrlFoto() { return urlFoto; }
}
