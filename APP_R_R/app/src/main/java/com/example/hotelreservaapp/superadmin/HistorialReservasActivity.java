package com.example.hotelreservaapp.superadmin;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class HistorialReservasActivity extends AppCompatActivity {

    private SuperadminHistorialActivityBinding binding;
    private ReportesAdapter adapter;
    private final List<Reporte> todosLosReportes = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<String> hoteles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SuperadminHistorialActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUI();
        cargarHotelesYReservas();
    }

    private void initUI() {
        hoteles.add("Todos");
        ArrayAdapter<String> adapterHoteles = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, hoteles);
        binding.spinnerHotel.setAdapter(adapterHoteles);
        binding.spinnerHotel.setText("Todos", false);

        binding.iconHelp.setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
                .setTitle("Historial")
                .setMessage("Visualiza reservas reales filtrando por hotel o fecha.")
                .setPositiveButton("Ok", null)
                .show());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.etFecha.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) -> {
                String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y);
                binding.etFecha.setText(fecha);
                filtrarResultados();
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        binding.btnLimpiarFecha.setOnClickListener(v -> {
            binding.etFecha.setText("");
            filtrarResultados();
        });

        adapter = new ReportesAdapter(todosLosReportes, reporte -> {
            DetalleReserva detalle = DetalleReserva.newInstance(reporte);
            detalle.show(getSupportFragmentManager(), "DetalleReserva");
        });

        binding.recyclerRegistros.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerRegistros.setAdapter(adapter);

        binding.spinnerHotel.setOnItemClickListener((parent, view, pos, id) -> filtrarResultados());
    }

    private void cargarHotelesYReservas() {
        hoteles.clear();
        hoteles.add("Todos");

        db.collection("Hoteles").get().addOnSuccessListener(snapshot -> {
            hoteles.clear();  // <-- Limpia antes
            hoteles.add("Todos");  // <-- Siempre incluye "Todos" como primera opción
            for (DocumentSnapshot doc : snapshot) {
                String nombre = doc.getString("nombre");
                if (nombre != null && !nombre.isEmpty()) {
                    hoteles.add(nombre);
                }
            }
            ArrayAdapter<String> adapterHoteles = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, hoteles);
            binding.spinnerHotel.setAdapter(adapterHoteles);
            binding.spinnerHotel.setText("Todos", false);
        });

        db.collection("reservaas").get().addOnSuccessListener(hotelsSnapshot -> {
            todosLosReportes.clear();

            for (DocumentSnapshot hotelDoc : hotelsSnapshot) {
                String idHotel = hotelDoc.getId();
                List<?> listaReservas = (List<?>) hotelDoc.get("listareservas");

                db.collection("Hoteles").document(idHotel).get().addOnSuccessListener(hotelInfo -> {
                    String nombreHotel = hotelInfo.getString("nombre");
                    if (nombreHotel == null) nombreHotel = "Hotel desconocido";
                    final String nombreHotelFinal = nombreHotel;  // FIX aquí
                    int imagenHotel = R.drawable.hotel1;

                    if (listaReservas != null) {
                        for (Object obj : listaReservas) {
                            if (obj instanceof Map) {
                                Map<String, Object> reservaMap = (Map<String, Object>) obj;
                                String idUsuario = (String) reservaMap.get("idusuario");
                                String idReserva = (String) reservaMap.get("idreserva");

                                db.collection("usuarios").document(idUsuario).get().addOnSuccessListener(userDoc -> {
                                    String clienteNombre =
                                            (userDoc.getString("nombre") != null ? userDoc.getString("nombre") : "") + " " +
                                                    (userDoc.getString("apellido") != null ? userDoc.getString("apellido") : "");

                                    db.collection("usuarios").document(idUsuario)
                                            .collection("Reservas").document(idReserva)
                                            .get().addOnSuccessListener(reservaDoc -> {
                                                String estado = reservaDoc.getString("estado");
                                                if (estado == null) estado = "Desconocido";

                                                String fechaStr = "Sin fecha";
                                                String checkInStr = "Sin fecha";
                                                String checkOutStr = "Sin fecha";

                                                Object fechaReservaObj = reservaDoc.get("fechaReserva");
                                                if (fechaReservaObj instanceof com.google.firebase.Timestamp) {
                                                    Date fechaReserva = ((com.google.firebase.Timestamp) fechaReservaObj).toDate();
                                                    fechaStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fechaReserva);
                                                }

                                                Object fechaIniObj = reservaDoc.get("fechaIni");
                                                if (fechaIniObj instanceof com.google.firebase.Timestamp) {
                                                    Date fechaIni = ((com.google.firebase.Timestamp) fechaIniObj).toDate();
                                                    checkInStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fechaIni);
                                                }

                                                Object fechaFinObj = reservaDoc.get("fechaFin");
                                                if (fechaFinObj instanceof com.google.firebase.Timestamp) {
                                                    Date fechaFin = ((com.google.firebase.Timestamp) fechaFinObj).toDate();
                                                    checkOutStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fechaFin);
                                                }

                                                String nombreHabitacion = "Desconocida";
                                                List<Map<String, Object>> habitaciones = (List<Map<String, Object>>) reservaDoc.get("habitaciones");
                                                if (habitaciones != null && !habitaciones.isEmpty()) {
                                                    String nh = (String) habitaciones.get(0).get("nombreHabitacion");
                                                    if (nh != null) nombreHabitacion = nh;
                                                }

                                                Reporte reporte = new Reporte(nombreHotelFinal, clienteNombre.trim(), fechaStr, estado, imagenHotel);
                                                reporte.setCheckIn(checkInStr);
                                                reporte.setCheckOut(checkOutStr);
                                                reporte.setHabitacion(nombreHabitacion);

                                                todosLosReportes.add(reporte);
                                                adapter.actualizarLista(new ArrayList<>(todosLosReportes));
                                            });
                                });
                            }
                        }
                    }
                });
            }
        });
    }

    private void filtrarResultados() {
        String hotelSel = binding.spinnerHotel.getText().toString().trim();
        String fechaSel = binding.etFecha.getText().toString().trim();

        List<Reporte> filtrados = new ArrayList<>();
        for (Reporte r : todosLosReportes) {
            boolean matchHotel = hotelSel.equals("Todos") || r.getHotel().equalsIgnoreCase(hotelSel);
            boolean matchFecha = fechaSel.isEmpty() || r.getFecha().equals(fechaSel);
            if (matchHotel && matchFecha) filtrados.add(r);
        }
        adapter.actualizarLista(filtrados);
    }
}