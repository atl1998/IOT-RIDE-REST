package com.example.hotelreservaapp.cliente;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeCliente extends AppCompatActivity {
    MaterialButton btnBusqueda;
    private EditText etFecha, etCantidad;
    private MaterialButton btnBuscar;
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
    private AutoCompleteTextView etDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottonNavigationView);
        bottomNav.setSelectedItemId(R.id.inicioCliente);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.inicioCliente) {
                startActivity(new Intent(this, HomeCliente.class));
            } else if (id == R.id.chat_cliente) {
                startActivity(new Intent(this, ClienteChat.class));
            } else if (id == R.id.historialCliente) {
                startActivity(new Intent(this, HistorialEventos.class));
            } else if (id == R.id.perfilCliente) {
                startActivity(new Intent(this, PerfilCliente.class));
            }
            return true;
        });

        etDestino = findViewById(R.id.etDestino);
        etFecha = findViewById(R.id.etFecha);
        etCantidad = findViewById(R.id.etCantidad);
        btnBuscar = findViewById(R.id.btnBuscar);

        setupDateRangeSelection();
        setupVisitorsSelection();

        // Cargar búsqueda previa
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

        // Ofertas
        ofertasRecyclerView = findViewById(R.id.ofertasRecyclerView);
        ofertasRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        cargarOfertasEjemplo();
        ofertasAdapter = new OfertaHotelAdapter(this, listaOfertas);
        ofertasRecyclerView.setAdapter(ofertasAdapter);
        ofertasAdapter.setOnItemClickListener((oferta, position) ->
                Toast.makeText(HomeCliente.this, "Seleccionaste: " + oferta.getNombre(), Toast.LENGTH_SHORT).show()
        );

        btnNotificaciones = findViewById(R.id.notificaciones_cliente);
        btnNotificaciones.setOnClickListener(v -> startActivity(new Intent(this, ClienteNotificaciones.class)));

        // Autocompletar destino
        String[] destinos = {"Lima", "Arequipa", "Cusco", "Trujillo", "Piura", "Chiclayo", "Iquitos", "Tacna", "Puno", "Huancayo",
                "Cajamarca", "Ayacucho", "Huaraz", "Tarapoto", "Tumbes", "Moquegua", "Pucallpa", "Ica", "Chimbote", "Juliaca",
                "Puerto Maldonado", "Sullana", "Jaén", "Cerro de Pasco", "Huaral", "Cañete", "Huacho", "Abancay",
                "Bagua Grande", "Satipo", "Oxapampa", "Chachapoyas", "Yurimaguas", "Tingo María", "Barranca", "Talara",
                "Huánuco", "Moyobamba", "La Merced"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, destinos);
        etDestino.setAdapter(adapter);
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

    private void realizarBusqueda() {
        String destino = etDestino.getText().toString().trim();
        String fechas = etFecha.getText().toString().trim();
        String visitantes = etCantidad.getText().toString().trim();

        if (destino.isEmpty() || fechas.isEmpty() || visitantes.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar búsqueda
        SharedPreferences prefs = getSharedPreferences("BusquedaHotelPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("destino", destino);
        editor.putLong("fechaInicio", calendarInicio.getTimeInMillis());
        editor.putLong("fechaFin", calendarFin.getTimeInMillis());
        editor.putInt("adultos", cantidadAdultos);
        editor.putInt("ninos", cantidadNinos);
        editor.apply();

        // Navegar a lista de hoteles
        Intent intent = new Intent(this, ListaHotelesCliente.class);
        intent.putExtra("destino", destino);
        intent.putExtra("fechaInicio", calendarInicio.getTimeInMillis());
        intent.putExtra("fechaFin", calendarFin.getTimeInMillis());
        intent.putExtra("adultos", cantidadAdultos);
        intent.putExtra("ninos", cantidadNinos);
        startActivity(intent);
    }

    private void cargarOfertasEjemplo() {
        listaOfertas = new ArrayList<>();
        listaOfertas.add(new OfertaHotel("Cusco Rooms", "Cuzco", "8.3", "Muy bien", "454 comentarios", "2 noches:", "S/ 286", "S/ 158", R.drawable.hotel1, true));
        listaOfertas.add(new OfertaHotel("Hotel Maison Du Soleil", "Arequipa", "8.2", "Muy bien", "350 comentarios", "2 noches:", "S/ 470", "S/ 320", R.drawable.hotel1, true));
        listaOfertas.add(new OfertaHotel("Lima Luxury Suites", "Lima", "8.5", "Fantástico", "210 comentarios", "2 noches:", "S/ 350", "S/ 280", R.drawable.hotel1, true));
        listaOfertas.add(new OfertaHotel("Playa Resort", "Máncora", "8.7", "Excelente", "325 comentarios", "2 noches:", "S/ 520", "S/ 390", R.drawable.hotel1, true));
    }
}
