package com.example.hotelreservaapp.AdminHotel;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.AdminHotel.Model.ReservaInicio;
import com.example.hotelreservaapp.LogManager;
import com.example.hotelreservaapp.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.hotelreservaapp.AdminHotel.Model.Servicio;
import com.example.hotelreservaapp.databinding.AdminhotelActivityResumenreservaBinding;
import com.example.hotelreservaapp.databinding.SuperadminActivityEditarPagoBinding;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditarPagoActivity extends AppCompatActivity {
    private LinearLayout llServicesContainer;
    private MaterialButton backBottom;
    private FirebaseFirestore db;
    private String hotelId;

    private String reservaId;

    private SuperadminActivityEditarPagoBinding binding;
    public static final String EXTRA_RESERVA = "extra_reserva";

    double sumaServiciosExtras = 0.0;
    double cargosPorDanhos = 0.0;

    private ReservaInicio reserva;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SuperadminActivityEditarPagoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        llServicesContainer = binding.llServicesContainer;

        binding.backBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Usar el método tradicional
            }
        });

        // listener de guardar
        binding.btnGuardar.setOnClickListener(v -> saveUpdatedCosts());

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Recuperar el objeto ReservaInicio
        reserva = (ReservaInicio) getIntent()
                .getSerializableExtra(EXTRA_RESERVA);
        if (reserva == null) {
            Toast.makeText(this, "No se recibió la reserva", Toast.LENGTH_LONG).show();
            finish();
            return;
        }




        // Cargar hotelId y luego los servicios
        loadServices();

    }


    private void loadServices() {
        String reservaId = reserva.getIdreserva();
        db.collection("costos")
                .document(reservaId)
                .get()
                .addOnSuccessListener(this::onCostosDoc)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error cargando costos", Toast.LENGTH_SHORT).show()
                );
    }

    private void onCostosDoc(DocumentSnapshot doc) {
        if (!doc.exists() || !doc.contains("servicios")) {
            Toast.makeText(this, "No hay costos iniciales", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtenemos la lista de mapas [{ nombre: String, costo: Number }, ...]
        List<Map<String,Object>> listaCostos =
                (List<Map<String,Object>>) doc.get("servicios");

        populateServiceFields(listaCostos);
    }

    private void populateServiceFields(List<Map<String,Object>> listaCostos) {
        LayoutInflater inflater = LayoutInflater.from(this);
        llServicesContainer.removeAllViews();

        for (Map<String,Object> itemMap : listaCostos) {
            String nombre = (String) itemMap.get("nombre");
            // Firestore suele devolver los números como Long o Double
            Number costoNum = (Number) itemMap.get("costo");
            String costoStr = costoNum != null ? costoNum.toString() : "0";

            View itemView = inflater.inflate(
                    R.layout.adminhotel_item_costo_servicio,
                    llServicesContainer,
                    false
            );
            TextView tvName  = itemView.findViewById(R.id.tvServiceName);
            EditText etCost  = itemView.findViewById(R.id.etServiceCost);

            if (tvName == null || etCost == null) {
                throw new IllegalStateException(
                        "Revisa adminhotel_item_costo_servicio.xml: faltan tvServiceName o etServiceCost"
                );
            }

            tvName.setText(nombre);
            etCost.setText(costoStr);
            // opcional: guardar el nombre como tag para luego saber cuál editar
            itemView.setTag(nombre);

            llServicesContainer.addView(itemView);
        }
    }

    private void saveUpdatedCosts() {
        String reservaId = reserva.getIdreserva();
        List<Map<String,Object>> nuevosCostos = new ArrayList<>();

        // Recorre cada item inflado en llServicesContainer
        for (int i = 0; i < llServicesContainer.getChildCount(); i++) {
            View itemView = llServicesContainer.getChildAt(i);
            TextView tvName = itemView.findViewById(R.id.tvServiceName);
            EditText etCost = itemView.findViewById(R.id.etServiceCost);

            String nombre = tvName.getText().toString();
            String costoStr = etCost.getText().toString().trim();
            double costo;
            try {
                costo = Double.parseDouble(costoStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this,
                        "Costo inválido en “" + nombre + "”",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String,Object> map = new HashMap<>();
            map.put("nombre", nombre);
            map.put("costo", costo);
            nuevosCostos.add(map);

            if(i == llServicesContainer.getChildCount() - 1) {
                cargosPorDanhos = costo;
                continue;
            }
            sumaServiciosExtras += costo;
        }

        // Actualiza el array “servicios” en costos/{reservaId}
        db.collection("costos")
                .document(reservaId)
                .update("servicios", nuevosCostos)
                .addOnSuccessListener(aVoid -> {
                    updatePagoDocument(sumaServiciosExtras, cargosPorDanhos);
                    Toast.makeText(this,
                            "Costos actualizados correctamente",
                            Toast.LENGTH_SHORT).show();

                    // Obtener idHotel del usuario
                    db.collection("usuarios").document(uid)
                            .get().addOnSuccessListener(userSnap -> {
                                String idHotel = userSnap.getString("idHotel");
                                db.collection("Hoteles").document(idHotel)
                                        .get().addOnSuccessListener(ga -> {
                                            String nombre = ga.getString("nombre");
                                            LogManager.registrarLogRegistro(
                                                    nombre,
                                                    "Actualización de precios",
                                                    "Se han actualizado los precios del hotel " + nombre
                                            );

                                        }).addOnFailureListener(e -> Log.e("ResumenReserva", "Error leyendo usuario", e));
                            }).addOnFailureListener(e -> Log.e("ResumenReserva", "Error leyendo usuario", e));
                }).addOnFailureListener(e -> Log.e("ResumenReserva", "Error verificando costos", e));

        finish();
    }

    private void updatePagoDocument(double serviciosExtras, double cargosPorDanhos) {
        String uidUsuario = reserva.getIdUsuario();
        String reservaId = reserva.getIdreserva();

        // Ruta al doc Pago
        DocumentReference pagoRef = db.collection("usuarios")
                .document(uidUsuario)
                .collection("Reservas")
                .document(reservaId)
                .collection("PagosRealizados")
                .document("Pago");

        // Primero leemos el PrecioHabitacion para recalcular el total
        pagoRef.get()
                .addOnSuccessListener(pagoSnap -> {
                    if (!pagoSnap.exists()) {
                        Toast.makeText(this,
                                "Documento de Pago no encontrado",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Double precioHab = pagoSnap.getDouble("PrecioHabitacion");
                    if (precioHab == null) precioHab = 0.0;

                    double nuevoTotal = precioHab + serviciosExtras + cargosPorDanhos;

                    // Preparamos el map de campos a actualizar
                    Map<String,Object> updates = new HashMap<>();
                    updates.put("ServiciosExtras", serviciosExtras);
                    updates.put("CargosPorDanhos", cargosPorDanhos);
                    updates.put("PrecioTotal", nuevoTotal);

                    // Actualizamos el documento Pago
                    pagoRef.update(updates)
                            .addOnSuccessListener(a -> Toast.makeText(this,
                                    "Pago actualizado correctamente",
                                    Toast.LENGTH_SHORT).show()
                            )
                            .addOnFailureListener(e -> Toast.makeText(this,
                                    "Error al actualizar Pago: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show()
                            );
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Error al leer PrecioHabitacion: " + e.getMessage(),
                        Toast.LENGTH_LONG).show()
                );
    }


}
