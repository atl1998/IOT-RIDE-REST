package com.example.hotelreservaapp.AdminHotel.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hotelreservaapp.AdminHotel.Model.Habitacion;
import com.example.hotelreservaapp.AdminHotel.Model.Hotel;

import java.util.ArrayList;
import java.util.List;

public class RegistroViewModel extends ViewModel {
    private MutableLiveData<Hotel> hotel = new MutableLiveData<>(new Hotel());

    public LiveData<Hotel> getHotel() {
        return hotel;
    }

    public void setHotel(Hotel p) {
        hotel.setValue(p);
    }
}
