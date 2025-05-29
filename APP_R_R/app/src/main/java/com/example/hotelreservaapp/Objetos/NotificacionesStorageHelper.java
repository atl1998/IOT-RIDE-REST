package com.example.hotelreservaapp.Objetos;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class NotificacionesStorageHelper {
    private static final String FILE_NAME = "notificaciones.json";
    private static final String FOLDER_NAME = "subdirectorio_notificaciones";
    private Context context;

    public NotificacionesStorageHelper(Context context) {
        this.context = context;
    }
    public void guardarArchivoNotificacionesEnSubcarpeta(Notificaciones[] listaNotificaciones) {
        File folder = new File(context.getFilesDir(), FOLDER_NAME);
        if (!folder.exists()) {
            folder.mkdirs();  // crea la carpeta si no existe
        }

        File file = new File(folder, FILE_NAME);

        try (FileOutputStream outputStream = new FileOutputStream(file);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {

            objectOutputStream.writeObject(listaNotificaciones);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Notificaciones[] leerArchivoNotificacionesDesdeSubcarpeta() {
        File folder = new File(context.getFilesDir(), FOLDER_NAME);
        File file = new File(folder, FILE_NAME);

        if (!file.exists()) {
            return null;
        }

        try (FileInputStream fileInputStream = new FileInputStream(file);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return (Notificaciones[]) objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void borrarArchivoNotificaciones(Context context) {
        File folder = new File(context.getFilesDir(), FOLDER_NAME);
        File file = new File(folder, FILE_NAME);
        if (file.exists()) {
            if (file.delete()) {
                Toast.makeText(context, "Memoria de notificaciones borrada correctamente.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "No se pudo borrar la memoria de notificaciones.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "No hay memoria de notificaciones para borrar.", Toast.LENGTH_SHORT).show();
        }
    }

}
