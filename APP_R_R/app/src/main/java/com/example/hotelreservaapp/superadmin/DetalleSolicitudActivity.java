package com.example.hotelreservaapp.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.SuperadminDetalleSolicitudActivityBinding;
import com.example.hotelreservaapp.model.PostulacionTaxista;
import com.example.hotelreservaapp.model.SolicitudTaxista;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetalleSolicitudActivity extends AppCompatActivity  {
    private SuperadminDetalleSolicitudActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SuperadminDetalleSolicitudActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Botón de volver
        binding.btnBack.setOnClickListener(v -> finish());

        // Obtener la solicitud enviada desde el intent
        PostulacionTaxista solicitud = (PostulacionTaxista) getIntent().getSerializableExtra("solicitud");

        if (solicitud != null) {
            // Setear campos visibles en pantalla
            binding.etNombre.setText(solicitud.getNombres());
            binding.etApellido.setText(solicitud.getApellidos());
            binding.etTipoDoc.setText(solicitud.getTipoDocumento());
            binding.etNumDoc.setText(solicitud.getNumeroDocumento());
            binding.etFechaNacimiento.setText(solicitud.getFechaNacimiento());
            binding.etTelefono.setText(solicitud.getTelefono());
            binding.etDomicilio.setText(solicitud.getDireccion());
            binding.etCorreo.setText(solicitud.getCorreo());
            binding.etPlaca.setText(solicitud.getNumeroPlaca());

            // Foto usuario
            Glide.with(this)
                    .load("file:///android_asset/" + solicitud.getUrlFotoPerfil())
                    .placeholder(R.drawable.default_user_icon)
                    .circleCrop()
                    .into(binding.imageFotoUsuario);

            // Foto placa
            Glide.with(this)
                    .load("file:///android_asset/" + solicitud.getFotoPlacaURL())
                    .placeholder(R.drawable.placa_demo)
                    .into(binding.imageFotoPlaca);
        }else{
            Log.e("DetalleSolicitud", "NO SE RECIBIÓ LA SOLICITUD");
        }
        binding.btnHabilitar.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("¿Habilitar taxista?")
                    .setMessage("Esta acción permitirá al taxista usar la aplicación.")
                    .setPositiveButton("Sí, Habilitar", (dialog, which) -> {
                        mostrarSweetDialogExito("Taxista habilitado exitosamente", "habilitado");
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        binding.btnRechazar.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("¿Rechazar solicitud?")
                    .setMessage("Esta acción eliminará la solicitud del taxista.")
                    .setPositiveButton("Sí, Rechazar", (dialog, which) -> {
                        mostrarSweetDialogError("Solicitud rechazada", "rechazado");
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }
    private void mostrarSweetDialogExito(String mensaje, String accion) {
        SweetAlertDialog successDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        successDialog.setTitleText("¡Éxito!");
        successDialog.setContentText(mensaje);
        successDialog.setConfirmText("OK");
        successDialog.setConfirmClickListener(sweetAlertDialog -> {
            sweetAlertDialog.dismissWithAnimation();
            regresarALista(accion);
        });
        successDialog.show();
    }

    private void mostrarSweetDialogError(String mensaje, String accion) {
        SweetAlertDialog errorDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        errorDialog.setTitleText("Acción realizada");
        errorDialog.setContentText(mensaje);
        errorDialog.setConfirmText("OK");
        errorDialog.setConfirmClickListener(sweetAlertDialog -> {
            sweetAlertDialog.dismissWithAnimation();
            regresarALista(accion);
        });
        errorDialog.show();
    }
    private void regresarALista(String accion) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("accion", accion);
        setResult(RESULT_OK, resultIntent);
        finish();  // Regresa inmediatamente a la lista
    }

}