package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

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