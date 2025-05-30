package com.example.hotelreservaapp.AdminHotel.ViewModel;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hotelreservaapp.AdminHotel.Habitaciones;

import java.util.ArrayList;
import java.util.List;

public class RegistroViewModel extends ViewModel {
    private final MutableLiveData<List<Habitaciones>> listaHabitaciones = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Habitaciones>> getListHabitaciones() {
        return listaHabitaciones;
    }

    // Método para agregar un usuario a la lista
    public void agregarHabitacion(Habitaciones habitacion) {
        List<Habitaciones> currentList = listaHabitaciones.getValue();
        if(currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(habitacion);
        listaHabitaciones.setValue(currentList);
    }

    // Método para reemplazar toda la lista
    public void setNuevaListaHabitaciones(List<Habitaciones> nuevaLista) {
        listaHabitaciones.setValue(nuevaLista);
    }
}
