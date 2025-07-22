// ReporteServicioActivity.java
package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.AdminHotel.Adapter.ReporteServicioAdapter;
import com.example.hotelreservaapp.AdminHotel.Model.Servicio;
import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ReporteServicioActivity extends AppCompatActivity {

    private EditText etFecha;
    private MaterialButton btnBuscar, btnRefresh, btnBack;
    private RecyclerView recyclerView;
    private ReporteServicioAdapter adapter;
    private List<Servicio> serviciosList = new ArrayList<>();

    private Calendar calendarInicio = Calendar.getInstance();
    private Calendar calendarFin    = Calendar.getInstance();
    private FirebaseFirestore db    = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminhotel_activity_reporte_servicio);

        etFecha      = findViewById(R.id.etFecha);
        btnBuscar    = findViewById(R.id.btnBuscar);
        recyclerView = findViewById(R.id.recyclerUsers);
        btnBack = findViewById(R.id.backBottom);

        // Configura RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new ReporteServicioAdapter(serviciosList, this);
        recyclerView.setAdapter(adapter);

        // Selector de rango
        etFecha.setOnClickListener(v -> showDateRangePicker());

        // Buscar datos
        btnBuscar.setOnClickListener(v -> fetchReporte());

        // 3) Botones
        btnBack.setOnClickListener(v -> onBackPressed());

    }

    private void showDateRangePicker() {
        // Obtener la fecha mínima permitida (hoy)
        Calendar calendar = Calendar.getInstance();
        long hoy = calendar.getTimeInMillis();

        // Crear restricciones
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setStart(hoy) // Fecha mínima: hoy
                .setValidator(DateValidatorPointForward.now()); // Solo fechas desde hoy

        // Crear el picker con restricciones
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Selecciona un rango de fechas");
        builder.setCalendarConstraints(constraintsBuilder.build()); // Aplicar restricciones

        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();
        picker.show(getSupportFragmentManager(), picker.toString());

        picker.addOnPositiveButtonClickListener(selection -> {
            Long startDate = selection.first;
            Long endDate = selection.second;

            // Guardar en tus Calendars
            calendarInicio.setTimeInMillis(startDate);
            calendarFin.setTimeInMillis(endDate);

            // Formatear para mostrar
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String fechaInicioStr = formatter.format(new Date(startDate));
            String fechaFinStr = formatter.format(new Date(endDate));

            etFecha.setText(fechaInicioStr + " - " + fechaFinStr);
        });
    }

    private void fetchReporte() {
        if (calendarFin.before(calendarInicio)) {
            Toast.makeText(this, "Rango inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        Date start = calendarInicio.getTime();
        // Asegurar inclusión del día final
        Calendar c = (Calendar) calendarFin.clone();
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date end = c.getTime();

        db.collection("costos")
                .whereGreaterThanOrEqualTo("creadoEn", start)
                .whereLessThan("creadoEn", end)
                .orderBy("creadoEn", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(query -> {
                    Map<String, Double> totales = new HashMap<>();
                    for (var doc : query.getDocuments()) {
                        List<Map<String,Object>> servicios =
                                (List<Map<String,Object>>) doc.get("servicios");
                        if (servicios == null) continue;
                        for (Map<String,Object> s : servicios) {
                            String nombre = (String) s.get("nombre");
                            Number costo  = (Number) s.get("costo");
                            double val    = costo != null ? costo.doubleValue() : 0;
                            totales.put(nombre, totales.getOrDefault(nombre, 0.0) + val);
                        }
                    }
                    // Construir lista de Servicio
                    serviciosList.clear();
                    for (var entry : totales.entrySet()) {
                        Servicio srv = new Servicio();
                        srv.setNombre(entry.getKey());
                        srv.setPrecio(entry.getValue());
                        serviciosList.add(srv);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Error al generar reporte: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }
}
