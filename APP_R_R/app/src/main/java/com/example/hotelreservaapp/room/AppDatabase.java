package com.example.hotelreservaapp.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.hotelreservaapp.daos.NotificacionDao;
import com.example.hotelreservaapp.model.Notificacion;

@Database(entities = {Notificacion.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract NotificacionDao notificacionDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "notificaciones_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // En producción usar con ViewModel
                    .build();
        }
        return INSTANCE;
    }
}