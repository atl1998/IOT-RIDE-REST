package com.example.hotelreservaapp.taxista;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
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

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_main);

        // Sólo buscamos el BottomNavigationView aquí:
        bottomNav = findViewById(R.id.bottomNavigationTaxista);
        bottomNav.setOnItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerTaxista, new TaxiInicioFragment())
                    .commit();
        }

        // Si la Activity arrancó por una notificación de "viaje concluido":
        procesarIntentNotificacion(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        procesarIntentNotificacion(intent);
    }

    private void procesarIntentNotificacion(Intent intent) {
        if (intent != null && intent.getBooleanExtra("notificacion_viaje_concluido", false)) {
            String nombreUsuario = intent.getStringExtra("nombre_usuario");
            String ubicacion     = intent.getStringExtra("ubicacion");

            Notificaciones n = new Notificaciones(
                    listaGlobalNotificaciones.size() + 1,
                    "pedido",
                    "Viaje concluido",
                    "Viaje concluido",
                    "Has concluido el viaje con " + (nombreUsuario != null ? nombreUsuario : ""),
                    "Lugar: " + (ubicacion    != null ? ubicacion    : ""),
                    System.currentTimeMillis()
            );
            agregarNotificacionGlobal(n);
        }
    }

    @SuppressWarnings("NonConstantResourceId")
    private final BottomNavigationView.OnItemSelectedListener navListener = item -> {
        Fragment selected = null;
        int id = item.getItemId();
        if (id == R.id.inicioTaxista) {
            selected = new TaxiInicioFragment();
        } else if (id == R.id.mapaTaxista) {
            selected = new TaxiMapaFragment();
        } else if (id == R.id.perfilTaxista) {
            selected = new TaxiPerfilFragment();
        }
        if (selected != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerTaxista, selected)
                    .commit();
            return true;
        }
        return false;
    };

    /**
     * Desde el fragmento de inicio llamaremos a este método
     * cuando el usuario pulse el icono del header de notificaciones.
     */
    public void abrirFragmentoNotificaciones() {
        TaxistaNotificacionesFragment frag = new TaxistaNotificacionesFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerTaxista, frag, "FRAG_NOTIF")
                .addToBackStack(null)
                .commit();
    }

    /**
     * Agrega una notificación nueva, incrementa el contador,
     * y si el fragmento de notificaciones está abierto, le pide refrescar.
     */
    public void agregarNotificacionGlobal(Notificaciones notificacion) {
        listaGlobalNotificaciones.add(0, notificacion);
        if (!notificacion.isLeido()) {
            contadorNotificacionesNoLeidas++;
        }
        // Si ya estamos viendo el fragmento de notifs, que se refresque:
        Fragment f = getSupportFragmentManager().findFragmentByTag("FRAG_NOTIF");
        if (f instanceof TaxistaNotificacionesFragment) {
            ((TaxistaNotificacionesFragment) f).refrescarListaNotificaciones();
        }
    }

    /**
     * Marca todas las notificaciones como leídas y resetea el contador.
     * Debe llamarse cuando el usuario abra el fragmento de notificaciones.
     */
    public void marcarNotificacionesComoLeidas() {
        for (Notificaciones n : listaGlobalNotificaciones) {
            n.setLeido(true);
        }
        contadorNotificacionesNoLeidas = 0;
    }

    /** Getters para uso en los fragments **/
    public ArrayList<Notificaciones> getListaGlobalNotificaciones() {
        return listaGlobalNotificaciones;
    }

    public int getContadorNotificacionesNoLeidas() {
        return contadorNotificacionesNoLeidas;
    }
}
