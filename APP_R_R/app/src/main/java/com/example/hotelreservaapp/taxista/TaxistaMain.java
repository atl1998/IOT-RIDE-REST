package com.example.hotelreservaapp.taxista;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.taxista.fragments.TaxiInicioFragment;
import com.example.hotelreservaapp.taxista.fragments.TaxiMapaFragment;
import com.example.hotelreservaapp.taxista.fragments.TaxiPerfilFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TaxistaMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationTaxista);
        bottomNav.setOnItemSelectedListener(navListener);

        // Fragmento inicial por defecto
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerTaxista, new TaxiInicioFragment())
                    .commit();
        }
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.inicioTaxista) {
                    selectedFragment = new TaxiInicioFragment();
                } else if (itemId == R.id.mapaTaxista) {
                    selectedFragment = new TaxiMapaFragment();
                } else if (itemId == R.id.perfilTaxista) {
                    selectedFragment = new TaxiPerfilFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerTaxista, selectedFragment)
                            .commit();
                    return true;
                }
                return false;
            };
}
