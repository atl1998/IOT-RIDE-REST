package com.example.hotelreservaapp.cliente;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProcesarPago extends AppCompatActivity {
    private TextView nombreHotel, status, valoracion, ubicacion, valorFecha, valorPersonas, valorHabitacion,
            valorPrecioHabitacion, valorServiciosExtras, valorCargoDanhos, valorServicioTaxi, valorPrecioTotal;
    ImageView imageHotel;
    private String userId;
    private String hotelId;
    private LinearLayout procesarPagoLayout;
    HistorialItem historialItem;
    // ES RESUMEN DE PAGO XD NO REALIZAR PAGOOO, SE AUTOCOBRA
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_procesar_pago);
        procesarPagoLayout = findViewById(R.id.procesarPagoLayout);
        procesarPagoLayout.setVisibility(View.INVISIBLE);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            userId = currentUser.getUid();
        }

        // Inicialización de vistas
        valorFecha = findViewById(R.id.valorFecha);
        valorPersonas = findViewById(R.id.valorPersonas);
        nombreHotel = findViewById(R.id.nombreHotel);
        status = findViewById(R.id.status);
        valoracion = findViewById(R.id.valoracion);
        ubicacion = findViewById(R.id.ubicacion);
        valorHabitacion = findViewById(R.id.valorHabitacion);
        valorPrecioHabitacion = findViewById(R.id.valorPrecioHabitacion);
        valorServiciosExtras = findViewById(R.id.valorServiciosExtras);
        valorCargoDanhos = findViewById(R.id.valorCargoDanhos);
        valorServicioTaxi = findViewById(R.id.valorServicioTaxi);
        valorPrecioTotal = findViewById(R.id.valorPrecioTotal);
        imageHotel = findViewById(R.id.imageHotel);

        historialItem = (HistorialItem) getIntent().getSerializableExtra("HistorialItem");
        hotelId = getIntent().getStringExtra("hotelId");

        nombreHotel.setText(historialItem.getNombreHotel());
        status.setText(historialItem.getEstado());
        valoracion.setText(String.valueOf(historialItem.getValoracion()));
        ubicacion.setText(historialItem.getUbicacion());
        valorFecha.setText(historialItem.getFechas());
        valorPersonas.setText(historialItem.getPersonas() + " Personas");
        valorHabitacion.setText(historialItem.getTipoHab());
        descargarMostrarSinGuardar(historialItem.getUrlImage());

        if ("No disponible".equalsIgnoreCase(historialItem.getTaxistaEnabled())) {
            valorServicioTaxi.setText("No Disponible");
        } else {
            valorServicioTaxi.setText("Gratis");
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios")
                .document(userId)
                .collection("Reservas")
                .document(historialItem.getIdReserva())
                .collection("PagosRealizados")
                .document("Pago")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtener los valores numéricos desde Firestore
                        Double precioHabitacion = documentSnapshot.getDouble("PrecioHabitacion");
                        Double serviciosExtras = documentSnapshot.getDouble("ServiciosExtras");
                        Double cargoDanhos = documentSnapshot.getDouble("CargoPorDanhos");

                        // Precio de habitación (siempre mostrar el número)
                        if (precioHabitacion != null) {
                            valorPrecioHabitacion.setText("S/. " + precioHabitacion);
                        } else {
                            valorPrecioHabitacion.setText("S/. - - -");
                        }
                        // Servicios extras
                        if (serviciosExtras != null) {
                            if (serviciosExtras == 0.0) {
                                valorServiciosExtras.setText("S/. - - -");
                            } else {
                                valorServiciosExtras.setText("S/. " + serviciosExtras);
                            }
                        } else {
                            valorServiciosExtras.setText("S/. - - -");
                        }
                        // Cargo por daños
                        if (cargoDanhos != null) {
                            if (cargoDanhos == 0.0) {
                                valorCargoDanhos.setText("S/. - - -");
                            } else {
                                valorCargoDanhos.setText("S/. " + cargoDanhos);
                            }
                        } else {
                            valorCargoDanhos.setText("S/. - - -");
                        }
                        // Solo calcular y mostrar total si precioHabitacion no es null
                        if (precioHabitacion != null) {
                            double total = (precioHabitacion != null ? precioHabitacion : 0.0)
                                    + (serviciosExtras != null ? serviciosExtras : 0.0)
                                    + (cargoDanhos != null ? cargoDanhos : 0.0);
                            valorPrecioTotal.setText("S/. " + total);
                        } else {
                            valorPrecioTotal.setText("S/. - - -");
                        }
                    } else {
                        valorPrecioHabitacion.setText("S/. - - -");
                        valorServiciosExtras.setText("S/. - - -");
                        valorCargoDanhos.setText("S/. - - -");
                        valorPrecioTotal.setText("S/. - - -");
                    }
                    procesarPagoLayout.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener datos", e);
                    procesarPagoLayout.setVisibility(View.VISIBLE); // en caso de error, también mostrar algo
                });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.inicioCliente) {
                startActivity(new Intent(this, HomeCliente.class));
                return true;
            } else if (id == R.id.chat_cliente) {
                startActivity(new Intent(this, ClienteChat.class));
                return true;
            } else if (id == R.id.historialCliente) {
                startActivity(new Intent(this, HistorialEventos.class));
                return true;
            } else if (id == R.id.perfilCliente) {
                startActivity(new Intent(this, PerfilCliente.class));
                return true;
            }
            return false;
        });
        //es solicitar taxi xd
        Button btnrealizarpago = findViewById(R.id.btn_realizar_pago);
        btnrealizarpago.setEnabled(false);
        btnrealizarpago.setAlpha(0.5f);

        /*
        SharedPreferences sharedPreferences = getSharedPreferences("ReservaPrefs", MODE_PRIVATE);
        Boolean ServicioTaxi = sharedPreferences.getBoolean("ServicioTaxi", false);
        */

        if("No solicitado".equals(historialItem.getTaxistaEnabled())){
            btnrealizarpago.setEnabled(true);
            btnrealizarpago.setAlpha(1f);
        }

        MaterialButton btnNotificaciones = findViewById(R.id.notificaciones_cliente);
        btnNotificaciones.setOnClickListener(v -> {
            Intent intent = new Intent(this, ClienteNotificaciones.class);
            startActivity(intent);
        });
        btnrealizarpago.setOnClickListener(v -> {
            mostrarDialogoTaxista();
        });
    }
    private void mostrarDialogoTaxista() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.cliente_dialog_taxista);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Obtener la vista del diálogo
        View modalView = dialog.findViewById(android.R.id.content);

        // Ahora puedes acceder al botón dentro del diálogo
        Button btn_aceptar = modalView.findViewById(R.id.btn_aceptar);
        btn_aceptar.setOnClickListener(v -> {
            // Cierra el modal actual
            dialog.dismiss();

            // Quitar el blur si es Android 12 o superior
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getWindow().getDecorView().setRenderEffect(null);
            }

            // Guardar en SharedPreferences que se solicitó el taxi
            /*
            SharedPreferences sharedPreferences = getSharedPreferences("ReservaPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("ServicioTaxi", true);
            editor.apply();
            */

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("usuarios")
                    .document(userId)
                    .collection("Reservas")
                    .document(historialItem.getIdReserva())
                    .update("solicitarTaxista", "Solicitado")
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Campo actualizado a 'Solicitado'"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error al actualizar el campo", e));

            // Lanzar la siguiente actividad
            Intent intent = new Intent(ProcesarPago.this, HistorialEventos.class);
            startActivity(intent);

            // Mostrar mensaje al usuario
            Toast.makeText(ProcesarPago.this, "¡Servicio solicitado correctamente!", Toast.LENGTH_SHORT).show();
        });
    }
    public void descargarMostrarSinGuardar(String url){
        //FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        //StorageReference imageRef = firebaseStorage.getReference().child(path);
        Glide.with(ProcesarPago.this).load(url).into(imageHotel);
    }
}