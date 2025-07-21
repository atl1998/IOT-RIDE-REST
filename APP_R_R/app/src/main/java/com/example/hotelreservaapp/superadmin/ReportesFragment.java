package com.example.hotelreservaapp.superadmin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class ReportesFragment extends Fragment {

    private SuperadminReportesFragmentBinding binding;
    private FirebaseFirestore db;
    private static final String TAG = "ReportesFragment";

    // Formatos de fecha soportados
    private static final String[] DATE_FORMATS = {
            "dd 'de' MMMM 'de' yyyy",
            "dd/MM/yyyy",
            "yyyy-MM-dd",
            "dd-MM-yyyy"
    };

    // Colores para gráficos
    private static final List<Integer> CHART_COLORS = Arrays.asList(
            Color.parseColor("#FFD700"), // Amarillo dorado
            Color.parseColor("#8B4513"), // Marrón oscuro
            Color.parseColor("#E0E0E0"), // Gris claro
            Color.parseColor("#BDBDBD"), // Gris medio
            Color.parseColor("#9E9E9E"), // Gris
            Color.parseColor("#757575")  // Gris oscuro
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

        db = FirebaseFirestore.getInstance();

        // Mostrar loading state
        showLoadingState(true);

        loadDataFromFirestore();
        contarHotelesRegistrados();
        setupClickListeners();
    }

    private void showLoadingState(boolean isLoading) {
        if (binding != null) {
            // Aquí puedes mostrar/ocultar un progress bar si tienes uno en tu layout
            // binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void loadDataFromFirestore() {
        db.collection("reservas")
                .whereNotEqualTo("estado", "Cancelado")
                .get()
                .addOnCompleteListener(task -> {
                    showLoadingState(false);

                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Map<String, Object>> reservas = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            if (data != null) {
                                reservas.add(data);
                            }
                        }

                        if (reservas.isEmpty()) {
                            Log.w(TAG, "No se encontraron reservas");
                            setupChartsWithEmptyData();
                            return;
                        }

                        processReservationsData(reservas);
                        binding.tvTotalReservas.setText(String.valueOf(reservas.size()));

                    } else {
                        Log.e(TAG, "Error obteniendo reservas", task.getException());
                        setupChartsWithEmptyData();
                    }
                });
    }

    private void processReservationsData(List<Map<String, Object>> reservas) {
        try {
            // Procesar gráficos que no necesitan consultas adicionales
            setupBarChartWithData(reservas);
            setupLineChartWithData(reservas);

            // Procesar gráfico de habitaciones (requiere consultas optimizadas)
            setupPieChartWithDataOptimized(reservas);

        } catch (Exception e) {
            Log.e(TAG, "Error procesando datos de reservas", e);
            setupChartsWithEmptyData();
        }
    }

    private void setupBarChartWithData(List<Map<String, Object>> reservas) {
        Map<String, Integer> conteoHoteles = new HashMap<>();

        for (Map<String, Object> reserva : reservas) {
            String idHotel = (String) reserva.get("IdHotel");
            if (idHotel != null && !idHotel.trim().isEmpty()) {
                conteoHoteles.put(idHotel, conteoHoteles.getOrDefault(idHotel, 0) + 1);
            }
        }

        if (conteoHoteles.isEmpty()) {
            Log.w(TAG, "No hay datos de hoteles para mostrar");
            createBarChart(Collections.singletonList(new BarEntry(0, 0)), Collections.singletonList("Sin datos"));
            return;
        }

        List<Map.Entry<String, Integer>> topHoteles = new ArrayList<>(conteoHoteles.entrySet());
        Collections.sort(topHoteles, (a, b) -> b.getValue() - a.getValue());

        int limit = Math.min(3, topHoteles.size());
        List<Map.Entry<String, Integer>> top3 = topHoteles.subList(0, limit);

        consultarNombresHotelesYCrearBarChart(top3);
    }

    private void consultarNombresHotelesYCrearBarChart(List<Map.Entry<String, Integer>> top3) {
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : top3) {
            String idHotel = entry.getKey();
            Task<DocumentSnapshot> task = db.collection("Hoteles").document(idHotel).get();
            tasks.add(task);
        }

        Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
            List<BarEntry> entries = new ArrayList<>();
            List<String> etiquetas = new ArrayList<>();

            for (int i = 0; i < tasks.size(); i++) {
                Task<DocumentSnapshot> hotelTask = tasks.get(i);
                if (hotelTask.isSuccessful() && hotelTask.getResult() != null) {
                    DocumentSnapshot doc = hotelTask.getResult();
                    String nombreHotel = doc.getString("nombre");
                    if (nombreHotel == null || nombreHotel.trim().isEmpty()) {
                        nombreHotel = "Hotel desconocido";
                    }
                    etiquetas.add(nombreHotel);
                } else {
                    etiquetas.add("Hotel desconocido");
                }
                entries.add(new BarEntry(i, top3.get(i).getValue()));
            }

            createBarChart(entries, etiquetas);
        });
    }


    private void setupLineChartWithData(List<Map<String, Object>> reservas) {
        Map<String, Integer> reservasPorMes = initializeLastSixMonths();

        for (Map<String, Object> reserva : reservas) {
            String fechaFin = (String) reserva.get("fechaFin");
            if (fechaFin != null && !fechaFin.trim().isEmpty()) {
                String mesKey = parseDateToMonthKey(fechaFin);
                if (mesKey != null && reservasPorMes.containsKey(mesKey)) {
                    reservasPorMes.put(mesKey, reservasPorMes.get(mesKey) + 1);
                }
            }
        }

        createLineChartFromMonthlyData(reservasPorMes);
    }

    private Map<String, Integer> initializeLastSixMonths() {
        Map<String, Integer> meses = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());

        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 6; i++) {
            String mesKey = sdf.format(cal.getTime());
            meses.put(mesKey, 0);
            cal.add(Calendar.MONTH, -1);
        }

        return meses;
    }

    private String parseDateToMonthKey(String fechaString) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());

        for (String format : DATE_FORMATS) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(format, new Locale("es", "ES"));
                Date fecha = inputFormat.parse(fechaString);
                if (fecha != null) {
                    return outputFormat.format(fecha);
                }
            } catch (ParseException e) {
                // Continuar con el siguiente formato
            }
        }

        Log.w(TAG, "No se pudo parsear la fecha: " + fechaString);
        return null;
    }

    private void createLineChartFromMonthlyData(Map<String, Integer> reservasPorMes) {
        List<String> mesesOrdenados = new ArrayList<>(reservasPorMes.keySet());
        Collections.sort(mesesOrdenados);

        List<Entry> entries = new ArrayList<>();
        List<String> etiquetasMeses = new ArrayList<>();
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM", new Locale("es", "ES"));
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());

        for (int i = 0; i < mesesOrdenados.size(); i++) {
            String mesKey = mesesOrdenados.get(i);
            Integer valor = reservasPorMes.get(mesKey);
            entries.add(new Entry(i, valor != null ? valor : 0));

            try {
                Date fecha = parseFormat.parse(mesKey);
                if (fecha != null) {
                    String mesDisplay = displayFormat.format(fecha);
                    etiquetasMeses.add(capitalize(mesDisplay));
                } else {
                    etiquetasMeses.add(mesKey);
                }
            } catch (ParseException e) {
                etiquetasMeses.add(mesKey);
            }
        }

        createLineChart(entries, etiquetasMeses);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void setupPieChartWithDataOptimized(List<Map<String, Object>> reservas) {
        Map<String, Map<String, Integer>> tiposPorHabitacion = new HashMap<>();
        Set<String> uniqueHotelIds = new HashSet<>();

        for (Map<String, Object> reserva : reservas) {
            String idHotel = (String) reserva.get("IdHotel");
            if (idHotel == null) continue;
            uniqueHotelIds.add(idHotel);

            List<Map<String, Object>> habitaciones = (List<Map<String, Object>>) reserva.get("habitaciones");
            if (habitaciones != null) {
                for (Map<String, Object> habitacion : habitaciones) {
                    String habitacionId = (String) habitacion.get("habitacionId");
                    if (habitacionId != null) {
                        Map<String, Integer> habitacionesHotel = tiposPorHabitacion.computeIfAbsent(idHotel, k -> new HashMap<>());
                        habitacionesHotel.put(habitacionId, habitacionesHotel.getOrDefault(habitacionId, 0) + 1);
                    }
                }
            }
        }

        if (tiposPorHabitacion.isEmpty()) {
            crearGraficoVacio();
            return;
        }

        Map<String, Integer> conteoTiposGlobal = new HashMap<>();
        List<Task<Void>> tasks = new ArrayList<>();

        for (String idHotel : uniqueHotelIds) {
            Task<Void> task = db.collection("Hoteles")
                    .document(idHotel)
                    .collection("habitaciones")
                    .get()
                    .continueWith(hotelTask -> {
                        if (hotelTask.isSuccessful()) {
                            for (DocumentSnapshot doc : hotelTask.getResult()) {
                                String habitacionId = doc.getId();
                                String nombreHabitacion = doc.getString("nombreHabitacion");
                                if (nombreHabitacion == null) nombreHabitacion = "Desconocido";

                                if (tiposPorHabitacion.containsKey(idHotel)) {
                                    Map<String, Integer> habs = tiposPorHabitacion.get(idHotel);
                                    if (habs.containsKey(habitacionId)) {
                                        int count = habs.get(habitacionId);
                                        synchronized (conteoTiposGlobal) {
                                            conteoTiposGlobal.put(nombreHabitacion, conteoTiposGlobal.getOrDefault(nombreHabitacion, 0) + count);
                                        }
                                    }
                                }
                            }
                        }
                        return null;
                    });

            tasks.add(task);
        }

        Tasks.whenAllComplete(tasks).addOnCompleteListener(t -> {
            List<PieEntry> entries = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : conteoTiposGlobal.entrySet()) {
                entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }
            if (entries.isEmpty()) {
                entries.add(new PieEntry(1, "Sin datos"));
            }
            createPieChart(entries);
        });
    }
    private void contarHotelesRegistrados() {
        db.collection("Hoteles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        int totalHoteles = task.getResult().size();
                        binding.tvHotelesRegistrados.setText(String.valueOf(totalHoteles));
                    } else {
                        Log.e(TAG, "Error obteniendo hoteles", task.getException());
                        binding.tvHotelesRegistrados.setText("0");
                    }
                });
    }

    private void crearGraficoVacio() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(1, "Sin datos"));
        createPieChart(entries);
    }

    private void createBarChart(List<BarEntry> entries, List<String> etiquetas) {
        if (binding == null || entries == null || entries.isEmpty()) {
            Log.w(TAG, "No se puede crear gráfico de barras: datos inválidos");
            return;
        }

        try {
            BarDataSet dataSet = new BarDataSet(entries, "");
            dataSet.setColor(getResources().getColor(R.color.amarillo_dorado));
            dataSet.setValueTextColor(getResources().getColor(R.color.marron_oscuro));
            dataSet.setValueTextSize(12f);

            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.6f);

            BarChart chart = binding.barChartTop3;
            chart.setData(barData);

            if (etiquetas != null && !etiquetas.isEmpty()) {
                chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(etiquetas));
            }

            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.getXAxis().setGranularity(1f);
            chart.getXAxis().setGranularityEnabled(true);
            chart.getXAxis().setTextColor(getResources().getColor(R.color.marron_oscuro));
            chart.getXAxis().setTextSize(10f);

            chart.getAxisLeft().setTextColor(getResources().getColor(R.color.marron_oscuro));
            chart.getAxisRight().setEnabled(false);
            chart.getLegend().setEnabled(false);

            YAxis yAxis = chart.getAxisLeft();
            yAxis.setAxisMinimum(0f);
            yAxis.setTextSize(10f);

            Description description = new Description();
            description.setText("");
            chart.setDescription(description);

            chart.setDrawGridBackground(false);
            chart.setDrawBarShadow(false);
            chart.animateY(1000);
            chart.invalidate();

        } catch (Exception e) {
            Log.e(TAG, "Error creando gráfico de barras", e);
        }
    }

    private void createLineChart(List<Entry> entries, List<String> etiquetas) {
        if (binding == null || entries == null || entries.isEmpty()) {
            Log.w(TAG, "No se puede crear gráfico de líneas: datos inválidos");
            return;
        }

        try {
            LineDataSet dataSet = new LineDataSet(entries, "");
            dataSet.setColor(getResources().getColor(R.color.amarillo_dorado));
            dataSet.setCircleColor(getResources().getColor(R.color.amarillo_dorado));
            dataSet.setLineWidth(3f);
            dataSet.setCircleRadius(5f);
            dataSet.setDrawCircleHole(false);
            dataSet.setValueTextColor(getResources().getColor(R.color.marron_oscuro));
            dataSet.setValueTextSize(10f);
            dataSet.setDrawFilled(true);
            dataSet.setFillColor(getResources().getColor(R.color.amarillo_dorado));
            dataSet.setFillAlpha(50);

            LineData lineData = new LineData(dataSet);

            LineChart chart = binding.lineChartMensual;
            chart.setData(lineData);

            if (etiquetas != null && !etiquetas.isEmpty()) {
                chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(etiquetas));
            }

            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.getXAxis().setGranularity(1f);
            chart.getXAxis().setTextColor(getResources().getColor(R.color.marron_oscuro));
            chart.getXAxis().setTextSize(10f);

            chart.getAxisLeft().setTextColor(getResources().getColor(R.color.marron_oscuro));
            chart.getAxisLeft().setTextSize(10f);
            chart.getAxisRight().setEnabled(false);
            chart.getLegend().setEnabled(false);

            Description description = new Description();
            description.setText("");
            chart.setDescription(description);

            chart.setDrawGridBackground(false);
            chart.animateX(1200);
            chart.invalidate();

        } catch (Exception e) {
            Log.e(TAG, "Error creando gráfico de líneas", e);
        }
    }

    private void createPieChart(List<PieEntry> entries) {
        if (binding == null || entries == null || entries.isEmpty()) {
            Log.w(TAG, "No se puede crear gráfico circular: datos inválidos");
            return;
        }

        try {
            PieDataSet dataSet = new PieDataSet(entries, "");

            // Usar colores dinámicos basados en la cantidad de entradas
            List<Integer> colores = new ArrayList<>();
            for (int i = 0; i < entries.size() && i < CHART_COLORS.size(); i++) {
                colores.add(CHART_COLORS.get(i));
            }

            // Si hay más entradas que colores, repetir colores
            while (colores.size() < entries.size()) {
                colores.addAll(CHART_COLORS.subList(0,
                        Math.min(CHART_COLORS.size(), entries.size() - colores.size())));
            }

            dataSet.setColors(colores);
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setValueTextSize(12f);
            dataSet.setSliceSpace(3f);

            PieData pieData = new PieData(dataSet);

            PieChart chart = binding.pieChartHabitaciones;
            chart.setData(pieData);
            chart.setHoleRadius(40f);
            chart.setTransparentCircleRadius(45f);
            chart.setHoleColor(Color.WHITE);

            chart.getLegend().setTextColor(getResources().getColor(R.color.marron_oscuro));
            chart.getLegend().setTextSize(11f);

            Description description = new Description();
            description.setText("");
            chart.setDescription(description);

            chart.animateY(1000);
            chart.invalidate();

        } catch (Exception e) {
            Log.e(TAG, "Error creando gráfico circular", e);
        }
    }

    private void setupChartsWithEmptyData() {
        try {
            List<BarEntry> emptyBarEntries = Collections.singletonList(new BarEntry(0, 0));
            createBarChart(emptyBarEntries, Collections.singletonList("Sin datos"));

            List<Entry> emptyLineEntries = Collections.singletonList(new Entry(0, 0));
            createLineChart(emptyLineEntries, Collections.singletonList("Sin datos"));

            List<PieEntry> emptyPieEntries = Collections.singletonList(new PieEntry(1, "Sin datos"));
            createPieChart(emptyPieEntries);

        } catch (Exception e) {
            Log.e(TAG, "Error configurando gráficos vacíos", e);
        }
    }

    private void setupClickListeners() {
        if (binding == null) return;

        binding.btnVerHistorial.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(requireContext(), HistorialReservasActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error abriendo historial", e);
            }
        });

        binding.opcionLogs.setOnClickListener(v -> {
            try {
                binding.opcionLogs.setBackgroundResource(R.drawable.bg_opcion_selected);
                binding.opcionReportes.setBackgroundResource(R.drawable.bg_opcion_unselected);
                FragmentTransaction transaction = requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.fragmentContainer, new LogsFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            } catch (Exception e) {
                Log.e(TAG, "Error cambiando a LogsFragment", e);
            }
        });

        binding.opcionReportes.setOnClickListener(v -> {
            binding.opcionReportes.setBackgroundResource(R.drawable.bg_opcion_selected);
            binding.opcionLogs.setBackgroundResource(R.drawable.bg_opcion_unselected);
        });

        binding.iconHelp.setOnClickListener(v -> {
            try {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Reportes de reservas")
                        .setMessage("Visualiza estadísticas en tiempo real desde Firestore: hoteles top, tendencias mensuales y distribución por tipo de habitación.")
                        .setPositiveButton("Entendido", null)
                        .show();
            } catch (Exception e) {
                Log.e(TAG, "Error mostrando diálogo de ayuda", e);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}