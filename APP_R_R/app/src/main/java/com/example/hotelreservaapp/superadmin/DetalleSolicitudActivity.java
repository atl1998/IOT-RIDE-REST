package com.example.hotelreservaapp.superadmin;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.base.BaseBottomNavActivity;
import com.example.hotelreservaapp.databinding.SuperadminDetalleSolicitudActivityBinding;
import com.example.hotelreservaapp.model.SolicitudTaxista;

public class DetalleSolicitudActivity extends BaseBottomNavActivity {
    private SuperadminDetalleSolicitudActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SuperadminDetalleSolicitudActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Botón de volver
        binding.btnBack.setOnClickListener(v -> finish());

        // Obtener la solicitud enviada desde el intent
        SolicitudTaxista solicitud = (SolicitudTaxista) getIntent().getSerializableExtra("solicitud");

        if (solicitud != null) {
            Log.d("DetalleSolicitud", "Recibido: " + solicitud.getNombre());
            binding.etNombre.setText(solicitud.getNombre());
            binding.etCorreo.setText(solicitud.getCorreo());
            binding.etPlaca.setText(solicitud.getPlaca());

            // Foto usuario
            Glide.with(this)
                    .load("file:///android_asset/" + solicitud.getFotoUsuario())
                    .placeholder(R.drawable.default_user_icon)
                    .circleCrop()
                    .into(binding.imageFotoUsuario);

            // Foto placa
            Glide.with(this)
                    .load("file:///android_asset/" + solicitud.getFotoPlaca())
                    .placeholder(R.drawable.placa_demo)
                    .into(binding.imageFotoPlaca);
        }else{
            Log.e("DetalleSolicitud", "NO SE RECIBIÓ LA SOLICITUD");
        }
    }
}