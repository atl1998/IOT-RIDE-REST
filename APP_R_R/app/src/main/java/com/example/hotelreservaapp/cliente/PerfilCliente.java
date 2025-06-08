package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
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

public class PerfilCliente extends AppCompatActivity {
    private MaterialButton btnNotificaciones;
    private ImageView btnEditar;
    private TextInputEditText etNombre, etApellido, etCorreo, etDni, etTelefono, etDireccion;
    private Button btn_cerrar_sesion;
    private boolean enModoEdicion = false;

    private static final String PREFS_NAME = "PerfilClientePrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_perfil);

        NotificacionesStorageHelper helper = new NotificacionesStorageHelper(this);

        BottomNavigationView bottomNav = findViewById(R.id.bottonNavigationView);
        bottomNav.setSelectedItemId(R.id.perfilCliente);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.inicioCliente) {
                startActivity(new Intent(this, HomeCliente.class));
            } else if (id == R.id.chat_cliente) {
                startActivity(new Intent(this, ClienteChat.class));
            } else if (id == R.id.historialCliente) {
                startActivity(new Intent(this, HistorialEventos.class));
            }

            return true;
        });

        // Inicializar vistas
        btnNotificaciones = findViewById(R.id.notificaciones_cliente);
        btnEditar = findViewById(R.id.iv_edit_profile);
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etCorreo = findViewById(R.id.etCorreo);
        etDni = findViewById(R.id.etDni);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        btn_cerrar_sesion = findViewById(R.id.btn_cerrar_sesion);

        // Cargar datos guardados
        cargarDatos();

        // Cerrar sesión
        btn_cerrar_sesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // 🔐 Cierra sesión
            Toast.makeText(PerfilCliente.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

            //  Redirige a LoginActivity
            Intent intent = new Intent(PerfilCliente.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Notificaciones
        btnNotificaciones.setOnClickListener(v -> {
            Intent intent = new Intent(this, ClienteNotificaciones.class);
            startActivity(intent);
        });

        // Botón Editar / Guardar
        btnEditar.setOnClickListener(v -> {
            enModoEdicion = !enModoEdicion;

            etNombre.setEnabled(enModoEdicion);
            etApellido.setEnabled(enModoEdicion);
            etDni.setEnabled(enModoEdicion);
            etTelefono.setEnabled(enModoEdicion);
            etDireccion.setEnabled(enModoEdicion);
            etCorreo.setEnabled(false); // El correo es fijo

            if (enModoEdicion) {
                btnEditar.setImageResource(R.drawable.save_icon); // Cambiar a ícono de guardar
            } else {
                btnEditar.setImageResource(R.drawable.edit_square_24dp_black); // Cambiar a ícono de editar
                guardarDatos(); // Guardar al salir del modo edición
                Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarDatos() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("nombre", etNombre.getText().toString().trim());
        editor.putString("apellido", etApellido.getText().toString().trim());
        editor.putString("dni", etDni.getText().toString().trim());
        editor.putString("telefono", etTelefono.getText().toString().trim());
        editor.putString("direccion", etDireccion.getText().toString().trim());
        editor.apply();
    }

    private void cargarDatos() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        etNombre.setText(preferences.getString("nombre", ""));
        etApellido.setText(preferences.getString("apellido", ""));
        etCorreo.setText(preferences.getString("correo", "adrianbala@pucp.edu.pe")); // correo predeterminado
        etDni.setText(preferences.getString("dni", ""));
        etTelefono.setText(preferences.getString("telefono", ""));
        etDireccion.setText(preferences.getString("direccion", ""));

        // Campos desactivados al inicio
        etNombre.setEnabled(false);
        etApellido.setEnabled(false);
        etCorreo.setEnabled(false);
        etDni.setEnabled(false);
        etTelefono.setEnabled(false);
        etDireccion.setEnabled(false);
    }
}
