package com.example.hotelreservaapp.superadmin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.adapter.LogsAdapter;
import com.example.hotelreservaapp.databinding.SuperadminLogsFragmentBinding;
import com.example.hotelreservaapp.databinding.SuperadminReportesFragmentBinding;
import com.example.hotelreservaapp.model.LogItem;
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


public class LogsFragment extends Fragment {
    private SuperadminLogsFragmentBinding binding;
    private List<LogItem> todosLosLogs = new ArrayList<>();
    private LogsAdapter adapter;

    public LogsFragment() {
        // Especifica el layout directamente si quieres evitar inflate manual
        super(R.layout.superadmin_logs_fragment); // Este es tu antiguo layout de logs
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = SuperadminLogsFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new LogsAdapter(new ArrayList<>());
        binding.recyclerLogs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerLogs.setAdapter(adapter);
        LinearLayout opcionReportes = binding.opcionReportes;
        LinearLayout opcionLogs = binding.opcionLogs;

        ImageView iconHelp = view.findViewById(R.id.iconHelp);
        iconHelp.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Sección Logs")
                    .setMessage("Aquí puedes visualizar todos los eventos realizados mediante la app.\nPodrás obtener detalles acerca de cada evento e incluso tienes la opción de descarga en formato PDF.")
                    .setPositiveButton("Entendido", null)
                    .show();
        });

        opcionLogs.setBackgroundResource(R.drawable.bg_opcion_selected);
        opcionReportes.setBackgroundResource(R.drawable.bg_opcion_unselected);

        opcionReportes.setOnClickListener(v -> {
            opcionReportes.setBackgroundResource(R.drawable.bg_opcion_selected);
            opcionLogs.setBackgroundResource(R.drawable.bg_opcion_unselected);

            // Navega al fragmento de reportes
            Fragment reportesFragment = new ReportesFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, reportesFragment)
                    .addToBackStack(null)
                    .commit();
        });
        // Cargar logs demo
        cargarLogsDemo();

        // Picker de fecha
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
                filtrarPorFecha(fechaFormateada);
            });
        });
        binding.btnLimpiarFecha.setOnClickListener(v -> {
            binding.etFecha.setText("");
            adapter.actualizarLista(todosLosLogs);
        });
    }
    private void cargarLogsDemo() {
        todosLosLogs.add(new LogItem("10/04/2025", "10:45 AM", "Jorge Coronado", "Eliminó una reserva", R.drawable.coronado));
        todosLosLogs.add(new LogItem("06/05/2025", "04:12 PM", "Lucía Quispe", "Agregó un nuevo hotel", R.drawable.default_profile));
        todosLosLogs.add(new LogItem("04/05/2025", "08:30 AM", "Nilo Cori", "Modificó información de usuario", R.drawable.default_profile));
        todosLosLogs.add(new LogItem("02/05/2025", "11:00 AM", "Adrian Bala", "Cambió estado de reserva", R.drawable.default_profile));
        adapter.actualizarLista(todosLosLogs);
    }

    private void filtrarPorFecha(String fecha) {
        List<LogItem> filtrados = new ArrayList<>();
        for (LogItem log : todosLosLogs) {
            if (log.getFecha().equals(fecha)) filtrados.add(log);
        }
        adapter.actualizarLista(filtrados);
    }
}