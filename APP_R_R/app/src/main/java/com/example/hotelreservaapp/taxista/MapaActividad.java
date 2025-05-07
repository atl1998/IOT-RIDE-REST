
package com.example.hotelreservaapp.taxista;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hotelreservaapp.R;

public class MapaActividad extends AppCompatActivity {

    String ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_mapa);

        ubicacion = getIntent().getStringExtra("ubicacion");
        Toast.makeText(this, "Ubicación recibida: " + ubicacion, Toast.LENGTH_LONG).show();

        // Aquí podrías usar Google Maps para centrar el punto usando la ubicación recibida
    }
}
