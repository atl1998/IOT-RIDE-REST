package com.example.hotelreservaapp.superadmin;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.adapter.ReportesAdapter;
import com.example.hotelreservaapp.databinding.SuperadminHistorialActivityBinding;
import com.example.hotelreservaapp.model.Reporte;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HistorialReservasActivity extends AppCompatActivity {

    private SuperadminHistorialActivityBinding binding;
    private ReportesAdapter adapter;
    private List<Reporte> todosLosReportes;

    private final List<String> hoteles = List.of(
            "Todos",
            "Hotel Miraflores Palace",
            "Hotel Costa Azul",
            "Hotel Andino Real",
            "Hotel El Sol Imperial",
            "Hotel Laguna Dorada",
            "Hotel Nevado Blanco",
            "Hotel Bahía Serena"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SuperadminHistorialActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayAdapter<String> adapterHoteles = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                hoteles
        );
        binding.spinnerHotel.setAdapter(adapterHoteles);
        binding.spinnerHotel.setText("Todos", false);

        binding.iconHelp.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Sección Historial")
                    .setMessage("Aquí puedes visualizar todas las reservas realizadas.\nFiltra por hotel o fecha para encontrar lo que buscas.")
                    .setPositiveButton("Entendido", null)
                    .show();
        });

        binding.btnBack.setOnClickListener(v -> finish());

        binding.etFecha.setOnClickListener(v -> {
            CalendarConstraints.DateValidator dateValidator =
                    DateValidatorPointBackward.before(MaterialDatePicker.todayInUtcMilliseconds());

            CalendarConstraints constraints = new CalendarConstraints.Builder()
                    .setValidator(dateValidator)
                    .build();

            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selecciona una fecha")
                    .setCalendarConstraints(constraints)
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String fechaFormateada = sdf.format(new Date(selection));
                binding.etFecha.setText(fechaFormateada);
                filtrarResultados();
            });
        });

        binding.spinnerHotel.setOnItemClickListener((parent, view, position, id) -> filtrarResultados());

        todosLosReportes = new ArrayList<>();
        todosLosReportes.add(new Reporte("Hotel Miraflores Palace", "Adrian Bala", "10/04/2025", "Confirmada", R.drawable.hotel1));
        todosLosReportes.add(new Reporte("Hotel Costa Azul", "Nilo Cori", "06/05/2025", "Cancelada", R.drawable.hotel2));
        todosLosReportes.add(new Reporte("Hotel Andino Real", "Giorgio Coronado", "07/05/2025", "Confirmada", R.drawable.hotel1_example));
        todosLosReportes.add(new Reporte("Hotel El Sol Imperial", "Camila Mendoza", "05/05/2025", "Confirmada", R.drawable.hotel2_example));
        todosLosReportes.add(new Reporte("Hotel Laguna Dorada", "Sebastián Vega", "04/05/2025", "Cancelada", R.drawable.hotel3_example));
        todosLosReportes.add(new Reporte("Hotel Nevado Blanco", "Renata Silva", "03/05/2025", "Confirmada", R.drawable.hotel4_example));
        todosLosReportes.add(new Reporte("Hotel Bahía Serena", "Diego Ríos", "02/05/2025", "Confirmada", R.drawable.hotel5_example));
        todosLosReportes.add(new Reporte("Hotel Miraflores Palace", "Valeria López", "02/05/2025", "Confirmada", R.drawable.hotel1));
        todosLosReportes.add(new Reporte("Hotel Costa Azul", "Santiago Pérez", "30/04/2025", "Cancelada", R.drawable.hotel2));
        todosLosReportes.add(new Reporte("Hotel Andino Real", "Andrea Ruiz", "01/05/2025", "Confirmada", R.drawable.hotel1_example));
        todosLosReportes.add(new Reporte("Hotel Miraflores Palace", "Matías Delgado", "29/04/2025", "Cancelada", R.drawable.hotel1));

        adapter = new ReportesAdapter(todosLosReportes);
        binding.recyclerRegistros.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerRegistros.setAdapter(adapter);

        binding.btnLimpiarFecha.setOnClickListener(v -> {
            binding.etFecha.setText("");
            filtrarResultados();
        });
    }

    private void filtrarResultados() {
        String hotelSeleccionado = binding.spinnerHotel.getText().toString().trim();
        String fechaSeleccionada = binding.etFecha.getText().toString().trim();

        List<Reporte> filtrados = new ArrayList<>();
        for (Reporte r : todosLosReportes) {
            boolean coincideHotel = hotelSeleccionado.equals("Todos") || r.getHotel().equalsIgnoreCase(hotelSeleccionado);
            boolean coincideFecha = fechaSeleccionada.isEmpty() || r.getFecha().equals(fechaSeleccionada);

            if (coincideHotel && coincideFecha) {
                filtrados.add(r);
            }
        }

        adapter.actualizarLista(filtrados);
    }
}

