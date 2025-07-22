package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.AdminHotel.Model.ReservaInicio;
import com.example.hotelreservaapp.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ResumenReservaActivity extends AppCompatActivity {

    public static final String EXTRA_RESERVA = "extra_reserva";

    ResumenReservaActivity binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminhotel_activity_resumenreserva);

        // 1) Recupera el objeto ReservaInicio (debe implementar Serializable o Parcelable)
        ReservaInicio reserva = (ReservaInicio) getIntent().getSerializableExtra(EXTRA_RESERVA);


        System.out.println("Reserva Id Usario " + reserva.getIdUsuario());
        System.out.println("Reserva Nombre Completo " + reserva.getNombreCompleto());

        // …
    }
}