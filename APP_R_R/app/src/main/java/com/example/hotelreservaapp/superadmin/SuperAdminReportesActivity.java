package com.example.hotelreservaapp.superadmin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hotelreservaapp.adapter.ReportesAdapter;
import com.example.hotelreservaapp.base.BaseBottomNavActivity;
import com.example.hotelreservaapp.databinding.SuperadminReportesActivityBinding;
import com.example.hotelreservaapp.model.Reporte;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SuperAdminReportesActivity extends BaseBottomNavActivity {
    private SuperadminReportesActivityBinding binding;
    private ReportesAdapter adapter;
    private List<Reporte> todosLosReportes;

    private final List<String> hoteles = List.of("Hotel A", "Hotel B", "Hotel C");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SuperadminReportesActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Adapter para spinner
        ArrayAdapter<String> adapterHoteles = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, hoteles);
        binding.spinnerHotel.setAdapter(adapterHoteles);

        // Date picker
        binding.etFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, (month + 1), year);
                binding.etFecha.setText(fecha);
                filtrarResultados();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Chips
        binding.chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Aquí podrías filtrar por tipo si más adelante hay diferencias entre "reportes" y "logs"
            filtrarResultados();
        });

        // Filtro por selección de hotel
        binding.spinnerHotel.setOnItemClickListener((parent, view, position, id) -> filtrarResultados());

        // Datos demo
        todosLosReportes = new ArrayList<>();
        todosLosReportes.add(new Reporte("A", "Reserva realizada por:", "Adrián Tipo", "28/03/2025"));
        todosLosReportes.add(new Reporte("B", "Reserva realizada por:", "Jorge Coronado", "09/04/2025"));
        todosLosReportes.add(new Reporte("C", "Reserva realizada por:", "Jorge Coronado", "10/04/2025"));

        // Setup RecyclerView
        adapter = new ReportesAdapter(todosLosReportes);
        binding.recyclerRegistros.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerRegistros.setAdapter(adapter);
    }

    private void filtrarResultados() {
        String hotelSeleccionado = binding.spinnerHotel.getText().toString().trim();
        String fechaSeleccionada = binding.etFecha.getText().toString().trim();

        List<Reporte> filtrados = new ArrayList<>();
        for (Reporte r : todosLosReportes) {
            boolean coincideHotel = hotelSeleccionado.isEmpty() || ("Hotel " + r.getHotel()).equalsIgnoreCase(hotelSeleccionado);
            boolean coincideFecha = fechaSeleccionada.isEmpty() || r.getFecha().equals(fechaSeleccionada);

            if (coincideHotel && coincideFecha) {
                filtrados.add(r);
            }
        }

        adapter.actualizarLista(filtrados);
    }
}
