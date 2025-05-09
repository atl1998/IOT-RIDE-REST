package com.example.hotelreservaapp.superadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.adapter.ReportesAdapter;
import com.example.hotelreservaapp.databinding.SuperadminReportesFragmentBinding;
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

public class ReportesFragment extends Fragment {

    private SuperadminReportesFragmentBinding binding;
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

    public ReportesFragment() {
        super(R.layout.superadmin_reportes_fragment);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = SuperadminReportesFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<String> adapterHoteles = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                hoteles
        );
        binding.spinnerHotel.setAdapter(adapterHoteles);
        binding.spinnerHotel.setText("Todos", false);

        ImageView iconHelp = view.findViewById(R.id.iconHelp);
        iconHelp.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Sección Reportes")
                    .setMessage("Aquí puedes visualizar todos los reportes de reservas realizados mediante la app.\nUtiliza los distintos filtros y selecciona la reserva de tu interés para ver más información")
                    .setPositiveButton("Entendido", null)
                    .show();
        });

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

            datePicker.show(getParentFragmentManager(), "MATERIAL_DATE_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String fechaFormateada = sdf.format(new Date(selection));
                binding.etFecha.setText(fechaFormateada);
                filtrarResultados();
            });
        });

        binding.spinnerHotel.setOnItemClickListener((parent, view1, position, id) -> filtrarResultados());

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
        binding.recyclerRegistros.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerRegistros.setAdapter(adapter);

        binding.btnLimpiarFecha.setOnClickListener(v -> {
            binding.etFecha.setText("");
            filtrarResultados();
        });

        LinearLayout opcionReportes = binding.opcionReportes;
        LinearLayout opcionLogs = binding.opcionLogs;

        binding.opcionReportes.setBackgroundResource(R.drawable.bg_opcion_selected);
        binding.opcionLogs.setBackgroundResource(R.drawable.bg_opcion_unselected);


        opcionReportes.setOnClickListener(v -> {
            opcionReportes.setBackgroundResource(R.drawable.bg_opcion_selected);
            opcionLogs.setBackgroundResource(R.drawable.bg_opcion_unselected);
        });

        opcionLogs.setOnClickListener(v -> {
            opcionLogs.setBackgroundResource(R.drawable.bg_opcion_selected);
            opcionReportes.setBackgroundResource(R.drawable.bg_opcion_unselected);
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragmentContainer, new LogsFragment());  // Usa el ID de tu container principal
            transaction.addToBackStack(null); // Permite regresar con el botón atrás
            transaction.commit();
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
