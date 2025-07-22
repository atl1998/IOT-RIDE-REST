package com.example.hotelreservaapp.AdminHotel;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.hotelreservaapp.AdminHotel.Fragments.Registro2Foto_fragment;
import com.example.hotelreservaapp.AdminHotel.Model.Hotel;
import com.example.hotelreservaapp.AdminHotel.Model.ReservaInicio;
import com.example.hotelreservaapp.AdminHotel.ViewModel.RegistroViewModel;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.AdminhotelActivityResumenreservaBinding;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ResumenReservaActivity extends AppCompatActivity {

    public static final String EXTRA_RESERVA = "extra_reserva";

    private AdminhotelActivityResumenreservaBinding binding;;
    FirebaseFirestore db;
    ReservaInicio reserva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AdminhotelActivityResumenreservaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        binding.backBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Usar el método tradicional
            }
        });

        binding.btnEditarPago.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditarPagoActivity.class);
            startActivity(intent);
        });

        // 1) Recupera el objeto ReservaInicio (debe implementar Serializable o Parcelable)
        reserva = (ReservaInicio) getIntent().getSerializableExtra(EXTRA_RESERVA);

        // Cargar imagen desde URL con Glide
        String imageUrl = reserva.getUrlFoto();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.default_user_icon)
                    .error(R.drawable.default_user_icon)
                    .into(binding.ivProfileImage);
        } else {
            binding.ivProfileImage.setImageResource(R.drawable.default_user_icon);
        }

        db.collection("usuarios").document(reserva.getIdUsuario()).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        binding.valorNombre.setText(document.getString("nombre")+ " " + document.getString("apellido"));
                        binding.valorDni.setText(document.getString("numeroDocumento"));
                        binding.valorNacimiento.setText(document.getString("fechaNacimiento"));
                        binding.valorCorreo.setText(document.getString("correo"));
                        binding.valorTelefono.setText(document.getString("telefono"));


                    }
                })
                .addOnFailureListener(e ->
                        System.out.println("Detalle reserva: Error al cargar usuario " + e.getMessage())
                );

        // 2️⃣ Ahora entro a la subcolección "Reservas" de ese usuario
        db.collection("usuarios")
                .document(reserva.getIdUsuario())
                .collection("Reservas")
                .document(reserva.getIdreserva())
                .get()
                .addOnSuccessListener(resDoc -> {
                    if (!resDoc.exists()) {
                        System.out.println("Detalle reserva: Reserva no encontrada" );
                        return;
                    }
                    System.out.println("Detalle reserva: Reserva encontrada" );
                    //Valor Fecha
                    Date fechaEntrada = resDoc.getDate("fechaIni");
                    Date fechaSalida = resDoc.getDate("fechaFin");
                    SimpleDateFormat sdfInicio = new SimpleDateFormat("d MMM", new Locale("es","ES"));
                    SimpleDateFormat sdfFin    = new SimpleDateFormat("d MMM yyyy", new Locale("es","ES"));
                    String textoInicio = sdfInicio.format(fechaEntrada);  // ej. "28 Abr"
                    String textoFin    = sdfFin.format(fechaSalida);        // ej. "2 Mar 2024"
                    binding.valorFecha.setText(textoInicio + " – " + textoFin);

                    //Número de personas y tipo de habitación
                    binding.valorHabitacion.setText(resDoc.getString("tipoHab"));
                    binding.valorPersonas.setText(resDoc.getLong("personas") + "personas");
                    System.out.println("Detalle reserva: Fecha " + textoInicio + " – " + textoFin );

                })
                .addOnFailureListener(e ->
                        System.out.println("Detalle reserva: Error al detalle de la reserva " + e.getMessage()));

        // 3 Ahora entro a la subcolección "Reservas" de ese usuario
        db.collection("usuarios")
                .document(reserva.getIdUsuario())
                .collection("Reservas")
                .document(reserva.getIdreserva())
                .collection("PagosRealizados")
                .document("Pago")
                .get()
                .addOnSuccessListener(resDoc -> {
                    if (!resDoc.exists()) {
                        System.out.println("Detalle pagos no encontrado" );
                        return;
                    }
                    System.out.println("Detalle pago encontrado" );

                    //Número de personas y tipo de habitación
                    binding.valorPrecioHabitacion.setText("S/. " + resDoc.getDouble("PrecioHabitacion"));
                    binding.valorServiciosExtras.setText("S/. " + resDoc.getDouble("ServiciosExtras"));
                    binding.valorCargos.setText("S/. " + resDoc.getDouble("CargosPorDanhos"));
                    double valorTotal = resDoc.getDouble("PrecioHabitacion") + resDoc.getDouble("ServiciosExtras") + resDoc.getDouble("CargosPorDanhos");
                   binding.valorPrecioTotal.setText("S/. " + valorTotal);
                    System.out.println("Detalle pago Pago Total");

                })
                .addOnFailureListener(e ->
                        System.out.println("Detalle reserva: Error al detalle de la reserva " + e.getMessage()));


    }
}