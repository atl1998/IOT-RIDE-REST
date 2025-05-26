package com.example.hotelreservaapp.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hotelreservaapp.model.Notificacion;

import java.util.List;

@Dao
public interface NotificacionDao {
    @Insert
    void insertar(Notificacion notificacion);

    @Query("SELECT * FROM Notificacion ORDER BY timestamp DESC")
    List<Notificacion> obtenerTodas();

    @Query("SELECT COUNT(*) FROM Notificacion WHERE leido = 0")
    int contarNoLeidas();

    @Update
    void actualizar(Notificacion... notificaciones);
}