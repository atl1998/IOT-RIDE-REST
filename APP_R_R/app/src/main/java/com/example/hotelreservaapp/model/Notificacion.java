package com.example.hotelreservaapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Notificacion {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String titulo;
    public String mensaje;
    public long timestamp;
    public boolean leido;

    public Notificacion(String titulo, String mensaje, long timestamp, boolean leido) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.timestamp = timestamp;
        this.leido = leido;
    }
}