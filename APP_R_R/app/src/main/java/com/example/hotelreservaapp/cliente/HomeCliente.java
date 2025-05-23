package com.example.hotelreservaapp.cliente;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.hotelreservaapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.appcompat.app.AppCompatActivity;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeCliente extends AppCompatActivity {
    MaterialButton btnBusqueda;


    private EditText etFecha, etCantidad;
    private MaterialButton btnBuscar;

    // Variables para el rango de fechas
    private Calendar calendarInicio = Calendar.getInstance();
    private Calendar calendarFin = Calendar.getInstance();
    private boolean isStartDateSelected = false;

    // Variables para la cantidad de visitantes
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


        // Inicializar vistas
        etDestino = findViewById(R.id.etDestino);
        etFecha = findViewById(R.id.etFecha);
        etCantidad = findViewById(R.id.etCantidad);
        btnBuscar = findViewById(R.id.btnBuscar);

        // Configurar eventos de clic para los campos
        setupDateRangeSelection();
        setupVisitorsSelection();


        // Configurar botón de búsqueda
        btnBuscar.setOnClickListener(v -> realizarBusqueda());



        //recycler de ofertas
        // Inicializar RecyclerView
        ofertasRecyclerView = findViewById(R.id.ofertasRecyclerView);

        // Configurar el layout manager para scroll horizontal
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        ofertasRecyclerView.setLayoutManager(layoutManager);

        // Cargar datos de ejemplo
        cargarOfertasEjemplo();

        // Configurar el adaptador
        ofertasAdapter = new OfertaHotelAdapter(this, listaOfertas);
        ofertasRecyclerView.setAdapter(ofertasAdapter);

        // Configurar click listener para los items
        ofertasAdapter.setOnItemClickListener((oferta, position) -> {
            Toast.makeText(HomeCliente.this,
                    "Seleccionaste: " + oferta.getNombre(),
                    Toast.LENGTH_SHORT).show();
            // Aquí puedes navegar a detalles del hotel u otra acción
        });


        btnNotificaciones = findViewById(R.id.notificaciones_cliente);
        btnNotificaciones.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            Intent intent = new Intent(this, ClienteNotificaciones.class);
            startActivity(intent);
        });



        /*autocompletar destino*/
        etDestino = findViewById(R.id.etDestino);

        // Lista de destinos (puedes cargarla desde una API o base de datos si deseas)
        String[] destinos = new String[] {
                "Lima", "Cusco", "Arequipa", "Trujillo", "Piura", "Iquitos","Mancora","Huanuco","Huaraz"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                destinos
        );

        etDestino.setAdapter(adapter);




    }

    private void setupDateRangeSelection() {
        etFecha.setOnClickListener(v -> {
            isStartDateSelected = true;

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    HomeCliente.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            calendarInicio.set(Calendar.YEAR, year);
                            calendarInicio.set(Calendar.MONTH, month);
                            calendarInicio.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            // Mostrar diálogo para fecha final
                            DatePickerDialog datePickerDialogFin = new DatePickerDialog(
                                    HomeCliente.this,
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                            calendarFin.set(Calendar.YEAR, year);
                                            calendarFin.set(Calendar.MONTH, month);
                                            calendarFin.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                            actualizarFechasSeleccionadas();
                                        }
                                    },
                                    calendarInicio.get(Calendar.YEAR),
                                    calendarInicio.get(Calendar.MONTH),
                                    calendarInicio.get(Calendar.DAY_OF_MONTH)
                            );

                            // Establecer fecha mínima como la fecha de inicio seleccionada
                            datePickerDialogFin.getDatePicker().setMinDate(calendarInicio.getTimeInMillis());
                            datePickerDialogFin.show();
                        }
                    },
                    calendarInicio.get(Calendar.YEAR),
                    calendarInicio.get(Calendar.MONTH),
                    calendarInicio.get(Calendar.DAY_OF_MONTH)
            );

            // Establecer fecha mínima como hoy
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
    }
    private void actualizarFechasSeleccionadas() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaInicio = sdf.format(calendarInicio.getTime());
        String fechaFin = sdf.format(calendarFin.getTime());

        etFecha.setText(fechaInicio + " - " + fechaFin);
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

            // Inicializar contadores
            tvAdultos.setText(String.valueOf(cantidadAdultos));
            tvNinos.setText(String.valueOf(cantidadNinos));

            // Configurar botones para adultos
            btnMenosAdultos.setOnClickListener(btn -> {
                if (cantidadAdultos > 1) {
                    cantidadAdultos--;
                    tvAdultos.setText(String.valueOf(cantidadAdultos));
                }
            });

            btnMasAdultos.setOnClickListener(btn -> {
                cantidadAdultos++;
                tvAdultos.setText(String.valueOf(cantidadAdultos));
            });

            // Configurar botones para niños
            btnMenosNinos.setOnClickListener(btn -> {
                if (cantidadNinos > 0) {
                    cantidadNinos--;
                    tvNinos.setText(String.valueOf(cantidadNinos));
                }
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
        String visitantes = "";

        if (cantidadAdultos == 1) {
            visitantes = "1 adulto";
        } else {
            visitantes = cantidadAdultos + " adultos";
        }

        if (cantidadNinos > 0) {
            if (cantidadNinos == 1) {
                visitantes += ", 1 niño";
            } else {
                visitantes += ", " + cantidadNinos + " niños";
            }
        }

        etCantidad.setText(visitantes);
    }

    private void realizarBusqueda() {
        String destino = etDestino.getText().toString().trim();
        String fechas = etFecha.getText().toString().trim();
        String visitantes = etCantidad.getText().toString().trim();

        if (destino.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese un destino", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechas.isEmpty()) {
            Toast.makeText(this, "Por favor seleccione las fechas", Toast.LENGTH_SHORT).show();
            return;
        }

        if (visitantes.isEmpty()) {
            Toast.makeText(this, "Por favor seleccione la cantidad de visitantes", Toast.LENGTH_SHORT).show();
            return;
        }

        // Aquí puedes continuar con la lógica de búsqueda
        Toast.makeText(this, "Buscando: " + destino + "\nFechas: " + fechas + "\nVisitantes: " + visitantes, Toast.LENGTH_LONG).show();

        // Implementar navegación a la siguiente pantalla con estos parámetros
        Intent intent = new Intent(this, ListaHotelesCliente.class);
        // intent.putExtra("destino", destino);
        // intent.putExtra("fechaInicio", calendarInicio.getTimeInMillis());
        // intent.putExtra("fechaFin", calendarFin.getTimeInMillis());
        // intent.putExtra("adultos", cantidadAdultos);
        // intent.putExtra("ninos", cantidadNinos);
        startActivity(intent);
    }


    private void cargarOfertasEjemplo() {
        listaOfertas = new ArrayList<>();

        // Agregar datos de ejemplo (reemplaza R.drawable.ejemplo con tus propias imágenes)
        listaOfertas.add(new OfertaHotel(
                "Cusco Rooms",
                "Cuzco",
                "8.3",
                "Muy bien",
                "454 comentarios",
                "2 noches:",
                "S/ 286",
                "S/ 158",
                R.drawable.hotel1, // Reemplaza con tu imagen
                true));

        listaOfertas.add(new OfertaHotel(
                "Hotel Maison Du Soleil",
                "Arequipa",
                "8.2",
                "Muy bien",
                "350 comentarios",
                "2 noches:",
                "S/ 470",
                "S/ 320",
                R.drawable.hotel1, // Reemplaza con tu imagen
                true));

        // Agrega más hoteles aquí
        listaOfertas.add(new OfertaHotel(
                "Lima Luxury Suites",
                "Lima",
                "8.5",
                "Fantástico",
                "210 comentarios",
                "2 noches:",
                "S/ 350",
                "S/ 280",
                R.drawable.hotel1, // Reemplaza con tu imagen
                true));

        listaOfertas.add(new OfertaHotel(
                "Playa Resort",
                "Máncora",
                "8.7",
                "Excelente",
                "325 comentarios",
                "2 noches:",
                "S/ 520",
                "S/ 390",
                R.drawable.hotel1, // Reemplaza con tu imagen
                true));
    }


}