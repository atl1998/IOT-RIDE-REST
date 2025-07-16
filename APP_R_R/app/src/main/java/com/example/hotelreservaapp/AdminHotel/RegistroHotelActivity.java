package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelreservaapp.AdminHotel.Fragments.Registro1Datos_fragment;
import com.example.hotelreservaapp.R;

public class RegistroHotelActivity extends AppCompatActivity {

    private com.example.hotelreservaapp.AdminHotel.ViewModel.RegistroViewModel registroViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminhotel_activity_registro_hotel);

        registroViewModel = new ViewModelProvider(this).get(com.example.hotelreservaapp.AdminHotel.ViewModel.RegistroViewModel.class);

        // Cargar el primer fragmento
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new Registro1Datos_fragment())
                .commit();
    }

    public void irASiguientePaso(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }


}