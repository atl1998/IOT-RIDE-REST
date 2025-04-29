package com.example.riderest.superadmin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.riderest.R;
import com.example.riderest.databinding.SuperadminFragmentReportesBinding;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SuperAdminReportesFragment extends Fragment {

    private SuperadminFragmentReportesBinding binding;
    private final Calendar calendar = Calendar.getInstance();

    public SuperAdminReportesFragment() {
        super(R.layout.superadmin_fragment_reportes); // reemplaza con tu layout real
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding = SuperadminFragmentReportesBinding.bind(view);

        setupSpinnerHoteles();
        setupDatePicker();
        setupChips();
    }

    private void setupSpinnerHoteles() {
        String[] hoteles = {"Hotel A", "Hotel B", "Hotel C"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, hoteles);
        binding.spinnerHoteles.setAdapter(adapter);
    }

    private void setupDatePicker() {
        binding.etFecha.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        binding.etFecha.setText(format.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
    }

    private void setupChips() {
        binding.chipReportes.setOnClickListener(v -> {
            binding.chipLogs.setChecked(false);
            binding.chipReportes.setChecked(true);
            // TODO: Cargar reportes
        });

        binding.chipLogs.setOnClickListener(v -> {
            binding.chipReportes.setChecked(false);
            binding.chipLogs.setChecked(true);
            // TODO: Cargar logs
        });

        // Por defecto, marcar "Reportes"
        binding.chipReportes.setChecked(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
