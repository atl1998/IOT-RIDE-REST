package com.example.hotelreservaapp.Objetos;

import android.content.Context;
import android.util.Log;

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

    public boolean borrarArchivoNotificaciones() {
        File folder = new File(context.getFilesDir(), FOLDER_NAME);
        File file = new File(folder, FILE_NAME);
        if (file.exists()) {
            return file.delete();  // retorna true si se borró correctamente
        }
        return false; // si no existía el archivo, no hizo nada
    }


}
