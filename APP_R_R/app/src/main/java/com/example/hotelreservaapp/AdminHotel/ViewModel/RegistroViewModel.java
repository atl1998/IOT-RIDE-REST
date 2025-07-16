package com.example.hotelreservaapp.AdminHotel.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hotelreservaapp.AdminHotel.Model.Habitacion;
import com.example.hotelreservaapp.AdminHotel.Model.Hotel;
import com.example.hotelreservaapp.AdminHotel.Model.Servicio;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RegistroViewModel extends ViewModel {
    private MutableLiveData<Hotel> hotel = new MutableLiveData<>(new Hotel());
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /** Llama a este método pasándole el ID del hotel para iniciar la carga. */
    public void loadHotelWithSubcollections(String hotelId) {
        // 1) Trae primero el documento principal
        db.collection("Hoteles")
                .document(hotelId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    // Convierte a objeto Hotel (sin listas aún)
                    Hotel h = doc.toObject(Hotel.class);
                    if (h == null) return;

                    // 2) Trae la sub-colección “habitaciones”
                    db.collection("Hoteles")
                            .document(hotelId)
                            .collection("habitaciones")
                            .get()
                            .addOnSuccessListener(qsHab -> {
                                // Convierte todos los documentos en List<Habitacion>
                                List<Habitacion> listaHab = qsHab.toObjects(Habitacion.class);
                                h.setHabitaciones(listaHab);

                                // 3) Trae la sub-colección “servicios”
                                db.collection("Hoteles")
                                        .document(hotelId)
                                        .collection("servicios")
                                        .get()
                                        .addOnSuccessListener(qsSrv -> {
                                            List<Servicio> listaSrv = qsSrv.toObjects(Servicio.class);
                                            h.setServicios(listaSrv);

                                            // 4) Finalmente publica el Hotel completo
                                            hotel.postValue(h);
                                        });
                            });
                });
    }

    public LiveData<Hotel> getHotel() {
        return hotel;
    }

    public void setHotel(Hotel p) {
        hotel.setValue(p);
    }
}
