package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotelreservaapp.databinding.AdminhotelFragmentInicioBinding;


public class InicioFragment extends Fragment {

     private AdminhotelFragmentInicioBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = AdminhotelFragmentInicioBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }
}