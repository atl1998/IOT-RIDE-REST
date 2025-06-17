package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.Objetos.NotificacionesStorageHelper;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.loginAndRegister.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilCliente extends AppCompatActivity {
    private MaterialButton btnNotificaciones;
    private ImageView btnEditar;
    private TextInputEditText etNombre, etApellido, etCorreo, etDni, etTelefono, etDireccion;
    private Button btn_cerrar_sesion;
    private boolean enModoEdicion = false;
    private BottomNavigationView bottomNav;

    private TextView tvUserName, tvUserHandle;

    private FirebaseFirestore db;
    private FirebaseUser usuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_perfil);

        db = FirebaseFirestore.getInstance();
        usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        // Inicializar vistas
        bottomNav = findViewById(R.id.bottonNavigationView);
        btnNotificaciones = findViewById(R.id.notificaciones_cliente);
        btnEditar = findViewById(R.id.iv_edit_profile);
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etCorreo = findViewById(R.id.etCorreo);
        etDni = findViewById(R.id.etDni);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        btn_cerrar_sesion = findViewById(R.id.btn_cerrar_sesion);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserHandle = findViewById(R.id.tv_user_handle);

        configurarBottomNav();

        // Cargar datos desde Firebase
        cargarDatosDesdeFirebase();

        // Cerrar sesión
        btn_cerrar_sesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Ir a notificaciones
        btnNotificaciones.setOnClickListener(v -> {
            Intent intent = new Intent(this, ClienteNotificaciones.class);
            startActivity(intent);
        });

        // Editar / Guardar
        btnEditar.setOnClickListener(v -> {
            enModoEdicion = !enModoEdicion;

            etNombre.setEnabled(enModoEdicion);
            etApellido.setEnabled(enModoEdicion);
            etDni.setEnabled(enModoEdicion);
            etTelefono.setEnabled(enModoEdicion);
            etDireccion.setEnabled(enModoEdicion);
            etCorreo.setEnabled(false); // No editable

            if (enModoEdicion) {
                btnEditar.setImageResource(R.drawable.save_icon);
            } else {
                btnEditar.setImageResource(R.drawable.edit_square_24dp_black);
                guardarDatosEnFirebase();
            }
        });
    }

    private void cargarDatosDesdeFirebase() {
        if (usuarioActual != null) {
            db.collection("usuarios").document(usuarioActual.getUid()).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            etNombre.setText(document.getString("nombre"));
                            etApellido.setText(document.getString("apellido"));
                            etCorreo.setText(usuarioActual.getEmail());
                            etDni.setText(document.getString("numeroDocumento"));
                            etTelefono.setText(document.getString("telefono"));
                            etDireccion.setText(document.getString("direccion"));
                            tvUserName.setText(document.getString("nombre") + " " + document.getString("apellido"));
                            tvUserHandle.setText(usuarioActual.getEmail());
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private void guardarDatosEnFirebase() {
        if (usuarioActual == null) return;

        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || telefono.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos antes de guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("usuarios").document(usuarioActual.getUid())
                .update(
                        "nombre", nombre,
                        "apellido", apellido,
                        "numeroDocumento", dni,
                        "telefono", telefono,
                        "direccion", direccion
                )
                .addOnSuccessListener(unused -> Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show());
    }

    private void configurarBottomNav() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.perfilCliente) return true;
            if (id == R.id.inicioCliente) startActivity(new Intent(this, HomeCliente.class));
            if (id == R.id.historialCliente) startActivity(new Intent(this, HistorialEventos.class));
            if (id == R.id.chat_cliente) startActivity(new Intent(this, ClienteChat.class));
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.perfilCliente);
    }
}

