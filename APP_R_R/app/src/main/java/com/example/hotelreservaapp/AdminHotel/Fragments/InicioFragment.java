package com.example.hotelreservaapp.AdminHotel.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotelreservaapp.AdminHotel.Adapter.ReservaAdapter;
import com.example.hotelreservaapp.AdminHotel.Model.ReservaInicio;
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
    private ReservaAdapter adapter;
    private MaterialButton btnListaChats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = AdminhotelFragmentInicioBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Para ir a los chats
        btnListaChats = binding.NotificacionesAdminHotel;
        btnListaChats.setOnClickListener(v -> {
            Fragment chatFragment = new ChatFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminhotel_container_view, chatFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Configurar el RecyclerView
        recyclerView = binding.recyclerUsuarios; // ó el id correcto de tu RV
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //  ⇨ Aquí solo pasamos el Context, el Adapter se auto-carga
        adapter = new ReservaAdapter(getContext());
        recyclerView.setAdapter(adapter);

        return view;
    }
}
