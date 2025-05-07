package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.AdminhotelFragmentInicioBinding;
import com.example.hotelreservaapp.databinding.AdminhotelFragmentPerfilBinding;


public class PerfilFragment extends Fragment {


    private AdminhotelFragmentPerfilBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = AdminhotelFragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }
}