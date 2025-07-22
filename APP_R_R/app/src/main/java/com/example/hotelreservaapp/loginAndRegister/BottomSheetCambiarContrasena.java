package com.example.hotelreservaapp.loginAndRegister;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hotelreservaapp.LogManager;
import com.example.hotelreservaapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BottomSheetCambiarContrasena extends BottomSheetDialogFragment {

    private TextInputEditText etActual, etNueva, etConfirmar;
    private MaterialButton btnGuardar, btnCancelar;
    private View progress;

    public static BottomSheetCambiarContrasena newInstance() {
        return new BottomSheetCambiarContrasena();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheet_cambiar_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etActual = view.findViewById(R.id.et_current_password);
        etNueva = view.findViewById(R.id.et_new_password);
        etConfirmar = view.findViewById(R.id.et_confirm_password);
        btnGuardar = view.findViewById(R.id.btn_save);
        btnCancelar = view.findViewById(R.id.btn_cancel);
        progress = view.findViewById(R.id.progress_bar);

        btnGuardar.setEnabled(false);

        TextWatcher watcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                validarCampos();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };

        etActual.addTextChangedListener(watcher);
        etNueva.addTextChangedListener(watcher);
        etConfirmar.addTextChangedListener(watcher);

        btnGuardar.setOnClickListener(v -> cambiarContrasena());
        btnCancelar.setOnClickListener(v -> dismiss());
    }

    private void validarCampos() {
        String actual = etActual.getText().toString().trim();
        String nueva = etNueva.getText().toString().trim();
        String confirmar = etConfirmar.getText().toString().trim();

        boolean camposValidos = true;

        if (actual.isEmpty()) {
            etActual.setError("Ingresa tu contraseña actual");
            camposValidos = false;
        }

        if (nueva.length() < 8) {
            etNueva.setError("Debe tener al menos 8 caracteres");
            camposValidos = false;
        }

        if (!nueva.equals(confirmar)) {
            etConfirmar.setError("La contraseña no coincide");
            camposValidos = false;
        }

        btnGuardar.setEnabled(camposValidos);
    }

    private void cambiarContrasena() {
        String actual = etActual.getText().toString().trim();
        String nueva = etNueva.getText().toString().trim();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(getContext(), "Error de autenticación", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setVisibility(View.VISIBLE);
        btnGuardar.setEnabled(false);

        user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), actual))
                .addOnSuccessListener(aVoid -> {
                    user.updatePassword(nueva).addOnSuccessListener(unused -> {
                        Toast.makeText(getContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                        // 🔐 Registrar log de cambio de contraseña
                        LogManager.registrarLogRegistro(
                                user.getEmail(),
                                "Cambio de contraseña",
                                "El usuario cambió su contraseña"
                        );
                        dismiss();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al cambiar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        progress.setVisibility(View.GONE);
                        btnGuardar.setEnabled(true);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                    btnGuardar.setEnabled(true);
                });
    }
}