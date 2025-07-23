package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotelreservaapp.AdminHotel.Adapter.ReporteUsuarioAdapter;
import com.example.hotelreservaapp.AdminHotel.Model.ReporteUsuario;
import com.example.hotelreservaapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ReporteUsuarioActivity extends AppCompatActivity {

    private EditText etFecha;
    private MaterialButton btnBuscar, btnBack;
    private RecyclerView recyclerView;
    private ReporteUsuarioAdapter adapter;
    private final List<ReporteUsuario> lista = new ArrayList<>();

    private Calendar calendarInicio = Calendar.getInstance();
    private Calendar calendarFin    = Calendar.getInstance();
    private  FirebaseFirestore db = FirebaseFirestore.getInstance();
    private  String idAdminHotel;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.adminhotel_activity_reporte_usuario);

        idAdminHotel = FirebaseAuth.getInstance().getCurrentUser().getUid();

        etFecha      = findViewById(R.id.etFecha);
        btnBuscar    = findViewById(R.id.btnBuscar);
        btnBack      = findViewById(R.id.backBottom);
        recyclerView = findViewById(R.id.recyclerUsers);

        // 1) RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new ReporteUsuarioAdapter(lista);
        recyclerView.setAdapter(adapter);

        // 2) Selector de rango
        etFecha.setOnClickListener(v -> showDateRangePicker());

        // 3) Botón buscar
        btnBuscar.setOnClickListener(v -> fetchReportes());

        // 4) Volver
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void showDateRangePicker() {
        long hoy = Calendar.getInstance().getTimeInMillis();
        CalendarConstraints.DateValidator val = DateValidatorPointForward.now();

        MaterialDatePicker<Pair<Long,Long>> picker = MaterialDatePicker
                .Builder.<Pair<Long,Long>>dateRangePicker()
                .setTitleText("Selecciona rango de fechas")
                .setCalendarConstraints(
                        new CalendarConstraints.Builder()
                                .setStart(hoy)
                                .setValidator(val)
                                .build()
                )
                .build();

        picker.show(getSupportFragmentManager(), "RANGE");
        picker.addOnPositiveButtonClickListener(sel -> {
            long s = Math.min(sel.first, sel.second);
            long e = Math.max(sel.first, sel.second);
            calendarInicio.setTimeInMillis(s);
            calendarFin  .setTimeInMillis(e);

            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            etFecha.setText(fmt.format(new Date(s)) + " - " + fmt.format(new Date(e)));
        });
    }

    private void fetchReportes() {
        // Validar rango
        if (calendarFin.before(calendarInicio)) {
            Toast.makeText(this, "Rango inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        Date start = calendarInicio.getTime();
        // para incluir el día final:
        Calendar c = (Calendar) calendarFin.clone();
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date end = c.getTime();

        db.collection("reporteCostosUsuario")
                .whereGreaterThanOrEqualTo("creadoEn", start)
                .whereLessThan("creadoEn", end)
                .get()
                .addOnSuccessListener(q -> {
                    lista.clear();
                    for (var doc : q.getDocuments()) {
                        ReporteUsuario r = doc.toObject(ReporteUsuario.class);
                        System.out.println(
                                "Error al cargar reporte: " + r.getNombre());
                        System.out.println(
                                "Error al cargar reporte: " + r.getUsuarioId());
                        System.out.println(
                                "Error al cargar reporte: " + r.getTotalGastado());
                        if (r != null) lista.add(r);
                    }
                    System.out.println(
                            "Error al cargar reporte: " + lista.size());
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        System.out.println(
                                "Error al cargar reporte: " + e.getMessage())
                );
    }
}
