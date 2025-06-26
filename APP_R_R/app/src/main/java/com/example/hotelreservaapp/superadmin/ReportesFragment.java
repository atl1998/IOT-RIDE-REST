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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

        // Datos ficticios
        Map<String, Integer> conteo = new HashMap<>();
        conteo.put("Hotel Miraflores Palace", 5);
        conteo.put("Hotel Costa Azul", 3);
        conteo.put("Hotel Andino Real", 4);

        // Ordenar y tomar top 3
        List<Map.Entry<String, Integer>> topHoteles = new ArrayList<>(conteo.entrySet());
        Collections.sort(topHoteles, (a, b) -> b.getValue().compareTo(a.getValue()));
        topHoteles = topHoteles.subList(0, Math.min(3, topHoteles.size()));

        List<BarEntry> entries = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Integer> entry : topHoteles) {
            entries.add(new BarEntry(index, entry.getValue()));
            etiquetas.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Top 3 Hoteles");
        dataSet.setColor(getResources().getColor(R.color.bluee));
        dataSet.setValueTextColor(getResources().getColor(R.color.marron_oscuro));
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        BarChart chart = binding.barChartTop3;
        chart.setData(barData);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(etiquetas));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setGranularityEnabled(true);
        chart.getXAxis().setTextColor(getResources().getColor(R.color.marron_oscuro));
        chart.getAxisLeft().setTextColor(getResources().getColor(R.color.marron_oscuro));
        chart.getAxisRight().setEnabled(false);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0f);

        Description description = new Description();
        description.setText("");
        chart.setDescription(description);

        chart.setExtraTopOffset(10f);
        chart.setExtraLeftOffset(10f);
        chart.setExtraRightOffset(10f);

        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.animateY(1000);
        chart.invalidate();

        // Botón para ver historial de reservas
        binding.btnVerHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), HistorialReservasActivity.class);
            startActivity(intent);
        });

        // Botón para ver Logs
        binding.opcionLogs.setOnClickListener(v -> {
            binding.opcionLogs.setBackgroundResource(R.drawable.bg_opcion_selected);
            binding.opcionReportes.setBackgroundResource(R.drawable.bg_opcion_unselected);
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragmentContainer, new LogsFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        binding.opcionReportes.setOnClickListener(v -> {
            binding.opcionReportes.setBackgroundResource(R.drawable.bg_opcion_selected);
            binding.opcionLogs.setBackgroundResource(R.drawable.bg_opcion_unselected);
        });
        binding.iconHelp.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Reportes de reservas")
                    .setMessage("Aquí puedes visualizar las estadísticas de los hoteles con mayor número de reservas. Usa el botón inferior para ver el historial detallado.")
                    .setPositiveButton("Entendido", null)
                    .show();
        });
    }

}
