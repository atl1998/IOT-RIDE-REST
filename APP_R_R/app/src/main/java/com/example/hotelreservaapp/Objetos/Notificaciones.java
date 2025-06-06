package com.example.hotelreservaapp.Objetos;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Notificaciones implements Serializable {
    private int id; // un ID único
    private String tipo;
    private String titulo;
    private String tituloAmigable;
    private String mensaje;
    private String mensajeExtra;
    private long fecha;
    private boolean leido;


    // Constructor con id
    public Notificaciones(int id, String tipo, String titulo, String tituloAmigable, String mensaje, String mensajeExtra, long fecha) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.tituloAmigable= tituloAmigable;
        this.mensaje = mensaje;
        this.mensajeExtra = mensajeExtra;
        this.fecha = fecha;
        this.leido = false;
    }


    public String getFechaBonita() {
        Date date = new Date(this.fecha);
        // Formato para "23 de mayo"
        SimpleDateFormat sdf = new SimpleDateFormat("d 'de' MMMM", new Locale("es", "ES"));
        return sdf.format(date);
    }

    public String getHoraBonita() {
        Date date = new Date(this.fecha);
        // Formato para "14:30"
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", new Locale("es", "ES"));
        return sdf.format(date);
    }

    public String getFechaFormatoCorto() {
        Date date = new Date(this.fecha);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    public String getMensajeExtra() {
        return mensajeExtra;
    }

    public void setMensajeExtra(String mensajeExtra) {
        this.mensajeExtra = mensajeExtra;
    }

    public String getTituloAmigable() {
        return tituloAmigable;
    }

    public void setTituloAmigable(String tituloAmigable) {
        this.tituloAmigable = tituloAmigable;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }
}
