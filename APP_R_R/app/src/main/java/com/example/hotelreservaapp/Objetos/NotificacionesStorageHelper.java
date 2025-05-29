package com.example.hotelreservaapp.Objetos;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class NotificacionesStorageHelper {
    private static final String FILE_NAME = "notificaciones.json";

    private static final String FOLDER_NAME_CLIENTE = "subdirectorio_notificaciones_cliente";
    private static final String FOLDER_NAME_TAXISTA = "subdirectorio_notificaciones_taxista";

    private Context context;
    private String folderName;

    /**
     * Constructor para determinar la carpeta de almacenamiento según rol.
     * @param context Contexto de la aplicación.
     * @param esTaxista true si es para rol taxista, false para cliente.
     */
    public NotificacionesStorageHelper(Context context, boolean esTaxista) {
        this.context = context;
        this.folderName = esTaxista ? FOLDER_NAME_TAXISTA : FOLDER_NAME_CLIENTE;
    }

    /**
     * Guarda el arreglo de notificaciones serializado en la carpeta según el rol.
     * @param listaNotificaciones arreglo de notificaciones a guardar.
     */
    public void guardarArchivoNotificacionesEnSubcarpeta(Notificaciones[] listaNotificaciones) {
        File folder = new File(context.getFilesDir(), folderName);
        if (!folder.exists()) {
            folder.mkdirs();  // crea la carpeta si no existe
        }

        File file = new File(folder, FILE_NAME);

        try (FileOutputStream outputStream = new FileOutputStream(file);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {

            objectOutputStream.writeObject(listaNotificaciones);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al guardar notificaciones", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Lee el archivo de notificaciones desde la carpeta según el rol.
     * @return arreglo de notificaciones, o null si no existe.
     */
    public Notificaciones[] leerArchivoNotificacionesDesdeSubcarpeta() {
        File folder = new File(context.getFilesDir(), folderName);
        File file = new File(folder, FILE_NAME);

        if (!file.exists()) {
            return null;
        }

        try (FileInputStream fileInputStream = new FileInputStream(file);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return (Notificaciones[]) objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al leer notificaciones", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     * Borra el archivo de notificaciones de la carpeta según el rol.
     * @param context contexto para mostrar mensajes.
     */
    public void borrarArchivoNotificaciones(Context context) {
        File folder = new File(context.getFilesDir(), folderName);
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
