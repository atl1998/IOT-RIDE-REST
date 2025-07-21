package com.example.hotelreservaapp.AdminHotel.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotelreservaapp.AdminHotel.NotificacionesActivity;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.adapter.Adminhotel_UsuarioAdapter;
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

    MaterialButton btnListaChats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adminhotel_fragment_inicio, container, false);
        cargarUsuariosDeEjemplo();

        //Para ir los chats
        btnListaChats = view.findViewById(R.id.NotificacionesAdminHotel);
        btnListaChats.setOnClickListener(v -> {
            Fragment chatFragment = new ChatFragment(); // O el nombre real de tu fragmento de chat
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminhotel_container_view, chatFragment) // Aquí usas el id de tu FragmentContainerView
                    .addToBackStack(null) // Opcional, para que puedas volver atrás
                    .commit();
        });



        // Configurar el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Usa un LinearLayoutManager
        adapter = new Adminhotel_UsuarioAdapter(getContext() , listaOriginal);
        recyclerView.setAdapter(adapter);

        return view;

    }

    private void cargarUsuariosDeEjemplo() {
        listaOriginal.add(new UsuarioListaSuperAdmin("Jorge Coronado", "maxwell@pucp.edu.pe", "Vier 12","coronado.png" , true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Lucía Quispe", "lucia@email.com", "Mar 14", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Ana Pérez", "ana@email.com", "uwu 18", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Giorgio Maxwell", "gmaxwell@gmail.com", "sab 1", "coronado.png", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Nilo Cori", "nilocori@pucp.edu.pe", "mier 14", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Juan Pérez", "juanexample@hotmail.com", "uwu 18", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Adrian Tipo", "adriantipo@pucp.edu.pe", "uwu 18", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Adrian López", "adrianlopez@pucp.edu.pe", "uwu 18", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Pedro BM", "pedro@pucp.edu.pe", "uwu 18", "pedro.png", true));
    }
}