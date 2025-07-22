package com.example.hotelreservaapp.AdminHotel;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class EditarPagoActivity extends AppCompatActivity {
    private LinearLayout llServicesContainer;
    private MaterialButton backBottom;
    private FirebaseFirestore db;
    private String hotelId;

    private SuperadminActivityEditarPagoBinding binding;

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


        // Cargar hotelId y luego los servicios
        loadHotelId();
    }

    private void loadHotelId() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(this::onUserDoc)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error cargando usuario", Toast.LENGTH_SHORT).show()
                );
    }

    private void onUserDoc(DocumentSnapshot userDoc) {
        if (userDoc.exists() && userDoc.contains("idHotel")) {
            hotelId = userDoc.getString("idHotel");
            loadServices();
        } else {
            Toast.makeText(this, "No tienes hotel asignado", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadServices() {
        db.collection("Hoteles")
                .document(hotelId)
                .collection("servicios")
                .get()
                .addOnSuccessListener(this::populateServiceFields)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error cargando servicios", Toast.LENGTH_SHORT).show()
                );
    }

    private void populateServiceFields(QuerySnapshot snapshot) {
        LayoutInflater inflater = LayoutInflater.from(this);
        llServicesContainer.removeAllViews();
        View item = inflater.inflate(R.layout.adminhotel_item_costo_servicio, llServicesContainer, false);
        TextView tvName  = item.findViewById(R.id.tvServiceName);
        EditText etCost  = item.findViewById(R.id.etServiceCost);
        tvName.setText("Costos por Daños");
        etCost.setText("0");
        item.setTag("0");
        llServicesContainer.addView(item);


        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Servicio srv = doc.toObject(Servicio.class);

            View item2 = inflater.inflate(R.layout.adminhotel_item_costo_servicio, llServicesContainer, false);
            TextView tvName2 = item2.findViewById(R.id.tvServiceName);
            EditText etCost2  = item2.findViewById(R.id.etServiceCost);

            if (tvName2 == null || etCost2 == null) {
                throw new IllegalStateException(
                        "Revisa item_service_cost.xml: faltan tvServiceName o etServiceCost"
                );
            }
            String serviceId   = doc.getId();
            System.out.println("nombre del servicio: " + srv.getNombre());

            tvName2.setText(srv.getNombre());
            etCost2.setText(String.valueOf(srv.getPrecio()));
            item2.setTag(serviceId);

            llServicesContainer.addView(item2);
        }
    }

    private void saveCostsAndFinish() {
        int count = llServicesContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            View item  = llServicesContainer.getChildAt(i);
            String serviceId = (String) item.getTag();
            EditText etCost  = item.findViewById(R.id.etServiceCost);

            String text = etCost.getText().toString().trim();
            double newCost;
            try {
                newCost = Double.parseDouble(text);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Costo inválido en servicio " + (i+1), Toast.LENGTH_SHORT).show();
                return;
            }

            // Actualizar en Firestore
            db.collection("Hoteles")
                    .document(hotelId)
                    .collection("servicios")
                    .document(serviceId)
                    .update("precio", newCost);
        }
        Toast.makeText(this, "Costos actualizados", Toast.LENGTH_SHORT).show();
        finish();
    }
}
