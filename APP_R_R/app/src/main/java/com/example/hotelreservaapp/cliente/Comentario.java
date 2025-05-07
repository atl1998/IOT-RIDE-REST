package com.example.hotelreservaapp.cliente;

/**
 * Modelo de datos para representar un comentario de usuario
 */
public class Comentario {
    private String nombreUsuario;
    private float calificacion;
    private String comentario;
    private int imagenPerfil; // Recurso drawable id para la imagen de perfil

    // Constructor vacío
    public Comentario() {
    }

    // Constructor con parámetros
    public Comentario(String nombreUsuario, float calificacion, String comentario, int imagenPerfil) {
        this.nombreUsuario = nombreUsuario;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.imagenPerfil = imagenPerfil;
    }

    // Getters y setters
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(int imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }
}
