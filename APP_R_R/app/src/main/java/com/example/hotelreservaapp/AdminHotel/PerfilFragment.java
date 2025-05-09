package com.example.hotelreservaapp.AdminHotel;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotelreservaapp.MainActivity;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.cliente.HomeCliente;
import com.example.hotelreservaapp.databinding.AdminhotelFragmentInicioBinding;
import com.example.hotelreservaapp.databinding.AdminhotelFragmentPerfilBinding;
import com.google.android.material.button.MaterialButton;


public class PerfilFragment extends Fragment {


    private AdminhotelFragmentPerfilBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adminhotel_fragment_perfil, container, false);

        //Cerrar sesion
        MaterialButton btnCerrar = view.findViewById(R.id.btn_cerrar_sesion);
        btnCerrar.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            startActivity(new Intent(getActivity(), MainActivity.class));
        });
        return view;

    }
}