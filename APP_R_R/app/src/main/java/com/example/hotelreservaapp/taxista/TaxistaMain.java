package com.example.hotelreservaapp.taxista;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.hotelreservaapp.Objetos.Notificaciones;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.taxista.fragments.TaxiInicioFragment;
import com.example.hotelreservaapp.taxista.fragments.TaxiMapaFragment;
import com.example.hotelreservaapp.taxista.fragments.TaxistaNotificacionesFragment;
import com.example.hotelreservaapp.taxista.fragments.TaxiPerfilFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class TaxistaMain extends AppCompatActivity {

    private final ArrayList<Notificaciones> listaGlobalNotificaciones = new ArrayList<>();
    private int contadorNotificacionesNoLeidas = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationTaxista);
        bottomNav.setOnItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerTaxista, new TaxiInicioFragment())
                    .commit();
        }
        // Chequear si esta activity fue lanzada con notificación de viaje concluido
        Intent intent = getIntent();
        procesarIntentNotificacion(intent);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        procesarIntentNotificacion(intent);
    }

    private void procesarIntentNotificacion(Intent intent) {
        if (intent != null && intent.getBooleanExtra("notificacion_viaje_concluido", false)) {
            String nombreUsuario = intent.getStringExtra("nombre_usuario");
            String ubicacion = intent.getStringExtra("ubicacion");

            agregarNotificacionGlobal(new com.example.hotelreservaapp.Objetos.Notificaciones(
                    listaGlobalNotificaciones.size() + 1,
                    "pedido",
                    "Viaje concluido",
                    "Viaje concluido",
                    "Has concluido el viaje con " + (nombreUsuario != null ? nombreUsuario : ""),
                    "Lugar: " + (ubicacion != null ? ubicacion : ""),
                    System.currentTimeMillis()
            ));
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

    public void abrirFragmentoNotificaciones() {
        TaxistaNotificacionesFragment frag = new TaxistaNotificacionesFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerTaxista, frag, "FRAG_NOTIFICACIONES")
                .addToBackStack(null)
                .commit();
    }

    public TaxistaNotificacionesFragment obtenerFragmentoNotificaciones() {
        Fragment frag = getSupportFragmentManager().findFragmentByTag("FRAG_NOTIFICACIONES");
        if (frag instanceof TaxistaNotificacionesFragment) {
            return (TaxistaNotificacionesFragment) frag;
        }
        return null;
    }

    public ArrayList<Notificaciones> getListaGlobalNotificaciones() {
        return listaGlobalNotificaciones;
    }

    public int getContadorNotificacionesNoLeidas() {
        return contadorNotificacionesNoLeidas;
    }

    public void agregarNotificacionGlobal(Notificaciones notificacion) {
        listaGlobalNotificaciones.add(0, notificacion);
        if (!notificacion.isLeido()) {
            contadorNotificacionesNoLeidas++;
        }
    }

    public void marcarNotificacionesComoLeidas() {
        for (Notificaciones n : listaGlobalNotificaciones) {
            if (!n.isLeido()) {
                n.setLeido(true);
            }
        }
        contadorNotificacionesNoLeidas = 0;
    }
}
