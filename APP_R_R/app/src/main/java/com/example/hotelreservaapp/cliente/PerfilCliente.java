package com.example.hotelreservaapp.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.Objetos.NotificacionesStorageHelper;
import com.example.hotelreservaapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class PerfilCliente extends AppCompatActivity {
    private MaterialButton btnNotificaciones;

    private ImageView btnEditar;
    private TextInputEditText etNombre, etApellido, etCorreo, etDni, etTelefono, etDireccion;
    private Button btn_cerrar_sesion;
    private boolean enModoEdicion = false;

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
            } else if (id == R.id.perfilCliente) {
                //startActivity(new Intent(this, PerfilCliente.class));
            }

            return true;
        });


        btnNotificaciones = findViewById(R.id.notificaciones_cliente);
        btnNotificaciones.setOnClickListener(v -> {
            //por ahora directamente al mio bala
            Intent intent = new Intent(this, ClienteNotificaciones.class);
            startActivity(intent);
        });



        // Enlazar vistas
        btnEditar = findViewById(R.id.iv_edit_profile);
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etCorreo = findViewById(R.id.etCorreo);
        etDni = findViewById(R.id.etDni);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        btn_cerrar_sesion = findViewById(R.id.btn_cerrar_sesion);

        btn_cerrar_sesion.setOnClickListener(v -> {
            boolean borrado = helper.borrarArchivoNotificaciones();
            if (borrado) {
                Log.d("Notificaciones", "Archivo de notificaciones borrado correctamente.");
            } else {
                Log.d("Notificaciones", "No se encontró el archivo o no se pudo borrar.");
            }
        });


        // Listener para el botón de edición
        btnEditar.setOnClickListener(v -> {
            enModoEdicion = !enModoEdicion;

            etNombre.setEnabled(enModoEdicion);
            etApellido.setEnabled(enModoEdicion);
            etDni.setEnabled(enModoEdicion);
            etTelefono.setEnabled(enModoEdicion);
            etDireccion.setEnabled(enModoEdicion);
            etCorreo.setEnabled(false); // El correo no se edita

            if (enModoEdicion) {
                btnEditar.setImageResource(R.drawable.save_icon);
            } else {
                btnEditar.setImageResource(R.drawable.edit_square_24dp_black);

                // Obtener datos
                String nombre = etNombre.getText().toString().trim();
                String apellido = etApellido.getText().toString().trim();
                String dni = etDni.getText().toString().trim();
                String telefono = etTelefono.getText().toString().trim();
                String direccion = etDireccion.getText().toString().trim();

                // Aquí puedes hacer el update real en la base de datos o con ViewModel

                Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show();
            }
        });
    }
}