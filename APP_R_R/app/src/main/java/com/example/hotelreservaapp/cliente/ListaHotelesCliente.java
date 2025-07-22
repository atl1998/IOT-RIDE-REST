package com.example.hotelreservaapp.cliente;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListaHotelesCliente extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Hotel> listaHoteles;
    private FirebaseFirestore db;
    private long fechaInicioMillis, fechaFinMillis;
    private int adultos, ninos;
    private HotelAdapter adapter;


    TextView tvDestinoSeleccionado;
    TextView tvFechasSeleccionadas;

    private String destino;
    private String tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_activity_lista_hoteles); // tu layout principal
        // Obtener extras enviados desde HomeCliente
        Intent intent = getIntent();
        destino = intent.getStringExtra("destino");
        tipo = intent.getStringExtra("tipo");
        long fechaInicioMillis = intent.getLongExtra("fechaInicio", -1);
        long fechaFinMillis = intent.getLongExtra("fechaFin", -1);
        int adultos = intent.getIntExtra("adultos", 0);
        int ninos = intent.getIntExtra("ninos", 0);

        tvDestinoSeleccionado = findViewById(R.id.tvDestinoSeleccionado);
        tvFechasSeleccionadas = findViewById(R.id.tvFechasSeleccionadas);
        // Mostrar destino
        String destinoTexto = (tipo != null && tipo.equals("region")) ? "Departamento: " : "Ciudad: ";
        tvDestinoSeleccionado.setText(destinoTexto + destino);

        // Mostrar fechas
        String fechasTexto = (fechaInicioMillis != -1 && fechaFinMillis != -1)
                ? "Fechas: Del " + android.text.format.DateFormat.format("dd/MM/yyyy", fechaInicioMillis)
                + " al " + android.text.format.DateFormat.format("dd/MM/yyyy", fechaFinMillis)
                : "Fechas no seleccionadas";
        tvFechasSeleccionadas.setText(fechasTexto);

        String fechas = (fechaInicioMillis != -1 && fechaFinMillis != -1) ?
                "Del " + android.text.format.DateFormat.format("dd/MM/yyyy", fechaInicioMillis) +
                        " al " + android.text.format.DateFormat.format("dd/MM/yyyy", fechaFinMillis)
                : "Fechas no seleccionadas";

        String visitantes = adultos + " adultos, " + ninos + " niños";

        // Mostrar los datos recibidos
        Toast.makeText(this, "Buscando: " + destino + "\nFechas: " + fechas + "\nVisitantes: " + visitantes,
                Toast.LENGTH_LONG).show();

        //Inicializar Firebase Firestore
        db= FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView); // asegúrate que exista en tu XML
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaHoteles = new ArrayList<>();
        obtenerDatosDeFirebase(); //metodo que obtiene los datos del firestore

        listaHoteles = new ArrayList<>();
        adapter = new HotelAdapter(this, listaHoteles, fechaInicioMillis, fechaFinMillis, adultos, ninos);
        recyclerView.setAdapter(adapter);


        // Obtener referencia al botón de ordenar
        Button btnOrdenar = findViewById(R.id.btnOrdenar);

        // Configurar el listener del clic en el botón
        btnOrdenar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoOrdenamiento();
            }
        });


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

    }

    private void obtenerDatosDeFirebase() {
        db.collection("Hoteles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaHoteles.clear(); // Limpiar lista antes de llenarla
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            if (document.contains("nombre") &&
                                    document.contains("valoracion") &&
                                    document.contains("precioMin")) {

                                String hotelId = document.getId();
                                String nombre = document.getString("nombre");
                                String descripcion = document.getString("descripcion");
                                String direccion = document.getString("direccion");
                                String departamento = document.getString("departamento");
                                String provincia = document.getString("provincia");
                                String distrito = document.getString("distrito");
                                boolean servicioTaxi = Boolean.TRUE.equals(document.getBoolean("servicioTaxi"));
                                Double valoracion = document.getDouble("valoracion");
                                Double precioMin = document.getDouble("precioMin");
                                String urlFoto = document.getString("UrlFotoHotel");

                                // Verificar si coincide con el filtro de destino
                                String campoFiltrado;

                                if ("state".equals(tipo)) {
                                    campoFiltrado = departamento != null ? departamento.trim().toLowerCase() : "";
                                } else if ("region".equals(tipo) || "administrative".equals(tipo)) {
                                    campoFiltrado = departamento != null ? departamento.trim().toLowerCase() : "";
                                } else {
                                    campoFiltrado = provincia != null ? provincia.trim().toLowerCase() : "";
                                }

                                String destinoFiltrado = destino != null ? destino.trim().toLowerCase() : "";

                                if (campoFiltrado.equals(destinoFiltrado)) {
                                    if (valoracion != null) {
                                        Hotel hotel = new Hotel(hotelId, nombre, descripcion, direccion, departamento,
                                                provincia, distrito, valoracion.floatValue(), precioMin.floatValue(),
                                                servicioTaxi, urlFoto);
                                        listaHoteles.add(hotel);
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged(); // Refrescar RecyclerView
                    } else {
                        Toast.makeText(ListaHotelesCliente.this, "Error al cargar los hoteles", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarDialogoOrdenamiento() {
        // Inflar el layout del diálogo
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.cliente_boton_ordenar_hoteles, null);

        // Obtener referencia al grupo de radio buttons
        final RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group_ordenar);

        // Crear el diálogo
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Obtener el ID del radio button seleccionado
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        if (selectedId == R.id.rb_puntuacion) {
                            // Ordenar por puntuación
                            ordenarPorPuntuacion();
                        } else if (selectedId == R.id.rb_precio_menor) {
                            // Ordenar por precio de menor a mayor
                            ordenarPorPrecioAscendente();
                        } else if (selectedId == R.id.rb_precio_mayor) {
                            // Ordenar por precio de mayor a menor
                            ordenarPorPrecioDescendente();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Mostrar el diálogo
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    // Implementa estas funciones según tu lógica de ordenamiento
    private void ordenarPorPrecioAscendente() {
        Collections.sort(listaHoteles, new Comparator<Hotel>() {
            @Override
            public int compare(Hotel h1, Hotel h2) {
                return Double.compare(h1.getPrecioMin(), h2.getPrecioMin());
            }
        });
        adapter.notifyDataSetChanged(); // Refresca la lista
    }

    private void ordenarPorPrecioDescendente() {
        Collections.sort(listaHoteles, new Comparator<Hotel>() {
            @Override
            public int compare(Hotel h1, Hotel h2) {
                return Double.compare(h2.getPrecioMin(), h1.getPrecioMin());
            }
        });
        adapter.notifyDataSetChanged(); // Refresca la lista
    }

    private void ordenarPorPuntuacion() {
        Collections.sort(listaHoteles, new Comparator<Hotel>() {
            @Override
            public int compare(Hotel h1, Hotel h2) {
                return Float.compare(h2.getValoracion(), h1.getValoracion()); // Mayor a menor
            }
        });
        adapter.notifyDataSetChanged(); // Refresca el RecyclerView
    }
}