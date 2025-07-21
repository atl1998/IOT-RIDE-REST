package com.example.hotelreservaapp.cliente;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.cliente.TaxistaCliente.ClienteServicioTaxi;
import com.example.hotelreservaapp.taxista.TaxistaMain;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HomeCliente extends AppCompatActivity {
    MaterialButton btnBusqueda;
    BottomNavigationView bottomNav;
    private EditText etFecha, etCantidad;
    private MaterialButton btnBuscar ,btntaxi;
    private Calendar calendarInicio = Calendar.getInstance();
    private Calendar calendarFin = Calendar.getInstance();
    private boolean isStartDateSelected = false;
    private int cantidadAdultos = 1;
    private int cantidadNinos = 0;
    private HotelCarouselManager carouselManager;
    private MaterialButton btnNotificaciones;
    private RecyclerView ofertasRecyclerView;
    private OfertaHotelAdapter ofertasAdapter;
    private List<OfertaHotel> listaOfertas;
    private EditText etDestino;
    private String tipoDestinoSeleccionado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_home);

        etDestino = findViewById(R.id.etDestino);
        etFecha = findViewById(R.id.etFecha);
        etCantidad = findViewById(R.id.etCantidad);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnNotificaciones = findViewById(R.id.notificaciones_cliente);
        bottomNav = findViewById(R.id.bottonNavigationView);
        ofertasRecyclerView = findViewById(R.id.ofertasRecyclerView);
        btntaxi = findViewById(R.id.btntaxi);

        configurarBottomNav();

        // Abrir BuscadorDestino personalizado
        etDestino.setOnClickListener(v -> {
            Intent intent = new Intent(this, BuscadorDestino.class);
            startActivityForResult(intent, 1001);
        });

        // Configurar selección de fechas y visitantes
        etFecha.setOnClickListener(view -> showDateRangePicker());
        setupVisitorsSelection();

        // Cargar búsqueda anterior si existe
        SharedPreferences prefs = getSharedPreferences("BusquedaHotelPrefs", MODE_PRIVATE);
        String destinoGuardado = prefs.getString("destino", "");
        long fechaInicio = prefs.getLong("fechaInicio", -1);
        long fechaFin = prefs.getLong("fechaFin", -1);
        int adultos = prefs.getInt("adultos", 1);
        int ninos = prefs.getInt("ninos", 0);

        if (!destinoGuardado.isEmpty()) etDestino.setText(destinoGuardado);
        if (fechaInicio != -1 && fechaFin != -1) {
            calendarInicio.setTimeInMillis(fechaInicio);
            calendarFin.setTimeInMillis(fechaFin);
            actualizarFechasSeleccionadas();
        }

        cantidadAdultos = adultos;
        cantidadNinos = ninos;
        actualizarCantidadVisitantes();

        btnBuscar.setOnClickListener(v -> realizarBusqueda());

        // Cargar ofertas demo
        ofertasRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        cargarOfertasEjemplo();
        ofertasAdapter = new OfertaHotelAdapter(this, listaOfertas);
        ofertasRecyclerView.setAdapter(ofertasAdapter);
        ofertasAdapter.setOnItemClickListener((oferta, position) ->
                Toast.makeText(HomeCliente.this, "Seleccionaste: " + oferta.getNombre(), Toast.LENGTH_SHORT).show()
        );

        btnNotificaciones.setOnClickListener(v -> startActivity(new Intent(this, ClienteNotificaciones.class)));
        btntaxi.setOnClickListener(v -> {
            startActivity(new Intent(this, ClienteServicioTaxi.class));
            finish();
        });

        // Autocompletado local como ayuda adicional
        //String[] destinos = {"Lima", "Arequipa", "Cusco", "Trujillo", "Piura", "Chiclayo", "Iquitos", "Tacna", "Puno", "Huancayo"};
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, destinos);
        //etDestino.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            String nombre = data.getStringExtra("name");
            String tipo = data.getStringExtra("tipo"); // <- tipo: "city", "state", etc.

            if (nombre != null) etDestino.setText(nombre);
            tipoDestinoSeleccionado = tipo != null ? tipo : "";

            Log.d("Destino", "Seleccionado: " + nombre + " (Tipo: " + tipoDestinoSeleccionado + ")");
            Log.d("Destino", "Seleccionado: " + nombre + " (Tipo: " + tipoDestinoSeleccionado + ")");
        }
    }

    private void realizarBusqueda() {
        String destino = etDestino.getText().toString().trim();

        if (destino.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un destino", Toast.LENGTH_SHORT).show();
            return;
        }

        if (calendarInicio == null || calendarFin == null || calendarInicio.getTimeInMillis() >= calendarFin.getTimeInMillis()) {
            Toast.makeText(this, "Selecciona un rango de fechas válido", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = getSharedPreferences("BusquedaHotelPrefs", MODE_PRIVATE).edit();
        editor.putString("destino", destino);
        editor.putLong("fechaInicio", calendarInicio.getTimeInMillis());
        editor.putLong("fechaFin", calendarFin.getTimeInMillis());
        editor.putInt("adultos", cantidadAdultos);
        editor.putInt("ninos", cantidadNinos);
        editor.apply();

        Intent intent = new Intent(this, ListaHotelesCliente.class);
        intent.putExtra("destino", destino);
        intent.putExtra("tipo", tipoDestinoSeleccionado); // <- enviar tipo al siguiente activity
        intent.putExtra("fechaInicio", calendarInicio.getTimeInMillis());
        intent.putExtra("fechaFin", calendarFin.getTimeInMillis());
        intent.putExtra("adultos", cantidadAdultos);
        intent.putExtra("ninos", cantidadNinos);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.inicioCliente);
    }
    private void configurarBottomNav() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.inicioCliente) {
                return true; // Ya estás en esta pantalla
            } else if (id == R.id.chat_cliente) {
                startActivity(new Intent(this, ClienteChat.class));
            } else if (id == R.id.historialCliente) {
                startActivity(new Intent(this, HistorialEventos.class));
            } else if (id == R.id.perfilCliente) {
                startActivity(new Intent(this, PerfilCliente.class));
            }
            return true;
        });
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
            formatter.setTimeZone(TimeZone.getTimeZone("America/Lima"));

            String fechaInicioStr = formatter.format(new Date(startDate));
            String fechaFinStr = formatter.format(new Date(endDate));

            etFecha.setText(fechaInicioStr + " - " + fechaFinStr);
        });
    }


    private void setupDateRangeSelection() {
        etFecha.setOnClickListener(v -> {
            isStartDateSelected = true;
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        calendarInicio.set(year, month, dayOfMonth);
                        DatePickerDialog datePickerDialogFin = new DatePickerDialog(this,
                                (view1, y, m, d) -> {
                                    calendarFin.set(y, m, d);
                                    actualizarFechasSeleccionadas();
                                }, year, month, dayOfMonth);
                        datePickerDialogFin.getDatePicker().setMinDate(calendarInicio.getTimeInMillis());
                        datePickerDialogFin.show();
                    },
                    calendarInicio.get(Calendar.YEAR),
                    calendarInicio.get(Calendar.MONTH),
                    calendarInicio.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
    }

    private void actualizarFechasSeleccionadas() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        etFecha.setText(sdf.format(calendarInicio.getTime()) + " - " + sdf.format(calendarFin.getTime()));
    }

    private void setupVisitorsSelection() {
        etCantidad.setOnClickListener(v -> {
            View visitorDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_visitors, null);
            TextView tvAdultos = visitorDialogView.findViewById(R.id.tvAdultos);
            TextView tvNinos = visitorDialogView.findViewById(R.id.tvNinos);
            Button btnMenosAdultos = visitorDialogView.findViewById(R.id.btnMenosAdultos);
            Button btnMasAdultos = visitorDialogView.findViewById(R.id.btnMasAdultos);
            Button btnMenosNinos = visitorDialogView.findViewById(R.id.btnMenosNinos);
            Button btnMasNinos = visitorDialogView.findViewById(R.id.btnMasNinos);
            Button btnAceptar = visitorDialogView.findViewById(R.id.btnAceptar);

            tvAdultos.setText(String.valueOf(cantidadAdultos));
            tvNinos.setText(String.valueOf(cantidadNinos));

            btnMenosAdultos.setOnClickListener(btn -> {
                if (cantidadAdultos > 1) cantidadAdultos--;
                tvAdultos.setText(String.valueOf(cantidadAdultos));
            });
            btnMasAdultos.setOnClickListener(btn -> {
                cantidadAdultos++;
                tvAdultos.setText(String.valueOf(cantidadAdultos));
            });
            btnMenosNinos.setOnClickListener(btn -> {
                if (cantidadNinos > 0) cantidadNinos--;
                tvNinos.setText(String.valueOf(cantidadNinos));
            });
            btnMasNinos.setOnClickListener(btn -> {
                cantidadNinos++;
                tvNinos.setText(String.valueOf(cantidadNinos));
            });

            AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                    .setTitle("Cantidad de visitantes")
                    .setView(visitorDialogView)
                    .setCancelable(true)
                    .create();

            btnAceptar.setOnClickListener(btn -> {
                actualizarCantidadVisitantes();
                dialog.dismiss();
            });

            dialog.show();
        });
    }

    private void actualizarCantidadVisitantes() {
        String visitantes = (cantidadAdultos == 1 ? "1 adulto" : cantidadAdultos + " adultos");
        if (cantidadNinos > 0) visitantes += cantidadNinos == 1 ? ", 1 niño" : ", " + cantidadNinos + " niños";
        etCantidad.setText(visitantes);
    }

    private void cargarOfertasEjemplo() {
        listaOfertas = new ArrayList<>();
        listaOfertas.add(new OfertaHotel("Cusco Rooms", "Cuzco", "8.3", "Muy bien", "454 comentarios", "2 noches:", "S/ 286", "S/ 158", R.drawable.hotel1, true));
        listaOfertas.add(new OfertaHotel("Hotel Maison Du Soleil", "Arequipa", "8.2", "Muy bien", "350 comentarios", "2 noches:", "S/ 470", "S/ 320", R.drawable.hotel1, true));
        listaOfertas.add(new OfertaHotel("Lima Luxury Suites", "Lima", "8.5", "Fantástico", "210 comentarios", "2 noches:", "S/ 350", "S/ 280", R.drawable.hotel1, true));
        listaOfertas.add(new OfertaHotel("Playa Resort", "Máncora", "8.7", "Excelente", "325 comentarios", "2 noches:", "S/ 520", "S/ 390", R.drawable.hotel1, true));
    }
}
