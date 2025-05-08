package com.example.hotelreservaapp.AdminHotel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.adapter.Adminhotel_UsuarioAdapter;
import com.example.hotelreservaapp.adapter.UsuarioAdapter;
import com.example.hotelreservaapp.cliente.HomeCliente;
import com.example.hotelreservaapp.databinding.AdminhotelFragmentInicioBinding;
import com.example.hotelreservaapp.model.UsuarioListaSuperAdmin;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;


public class InicioFragment extends Fragment {

     private AdminhotelFragmentInicioBinding binding;
     private RecyclerView recyclerView;
    private Adminhotel_UsuarioAdapter adapter;
    private List<UsuarioListaSuperAdmin> listaOriginal = new ArrayList<>();

    MaterialButton btnNotificaiones;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adminhotel_fragment_inicio, container, false);
        cargarUsuariosDeEjemplo();

        //Para ir a notificaciones
        btnNotificaiones = view.findViewById(R.id.NotificacionesAdminHotel);
        btnNotificaiones.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            startActivity(new Intent(getActivity(), NotificacionesActivity.class));
        });


        // Configurar el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Usa un LinearLayoutManager
        adapter = new Adminhotel_UsuarioAdapter(getContext() , listaOriginal);
        recyclerView.setAdapter(adapter);

        return view;

    }

    private void cargarUsuariosDeEjemplo() {
        listaOriginal.add(new UsuarioListaSuperAdmin("Jorge Coronado", "maxwell@pucp.edu.pe", "Administrador de hotel","coronado.png" , true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Lucía Quispe", "lucia@email.com", "Taxista", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Ana Pérez", "ana@email.com", "Cliente", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Giorgio Maxwell", "gmaxwell@gmail.com", "Taxista", "coronado.png", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Nilo Cori", "nilocori@pucp.edu.pe", "Administrador de hotel", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Juan Pérez", "juanexample@hotmail.com", "Administrador de hotel", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Adrian Tipo", "adriantipo@pucp.edu.pe", "Administrador de hotel", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Adrian López", "adrianlopez@pucp.edu.pe", "Administrador de hotel", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Pedro BM", "pedro@pucp.edu.pe", "Administrador de hotel", "pedro.png", true));
    }
}