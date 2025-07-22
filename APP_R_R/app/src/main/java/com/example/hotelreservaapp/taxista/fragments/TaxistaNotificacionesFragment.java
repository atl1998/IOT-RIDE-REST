package com.example.hotelreservaapp.taxista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.Objetos.Notificaciones;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.taxista.TaxistaMain;
import com.example.hotelreservaapp.taxista.adapter.NotificacionesAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class TaxistaNotificacionesFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificacionesAdapter adapter;
    private ArrayList<Notificaciones> listaNotificaciones;

    public TaxistaNotificacionesFragment() {
        // Constructor vacío obligatorio
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.taxista_notificaciones, container, false);

        recyclerView = root.findViewById(R.id.recyclerNotificaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cargarListaGlobal();
        adapter = new NotificacionesAdapter(getContext(), listaNotificaciones);
        recyclerView.setAdapter(adapter);

        MaterialButton backButton = root.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        return root;
    }

    private void cargarListaGlobal() {
        if (getActivity() instanceof TaxistaMain) {
            listaNotificaciones = ((TaxistaMain) getActivity()).getListaGlobalNotificaciones();
        } else {
            listaNotificaciones = new ArrayList<>();
        }
    }

    /** Refresca la lista y notifica al adapter. */
    public void refrescarListaNotificaciones() {
        cargarListaGlobal();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Al entrar, marcamos todo como leído y actualizamos badge/lista
        if (getActivity() instanceof TaxistaMain) {
            TaxistaMain main = (TaxistaMain) getActivity();
            main.marcarNotificacionesComoLeidas();
            refrescarListaNotificaciones();
        }
    }
}
