package com.example.hotelreservaapp.superadmin;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.adapter.UsuarioAdapter;
import com.example.hotelreservaapp.base.BaseBottomNavActivity;
import com.example.hotelreservaapp.databinding.ActivitySuperadminBinding;
import com.example.hotelreservaapp.model.Usuario;
import com.example.hotelreservaapp.model.UsuarioListaSuperAdmin;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuperAdminActivity extends BaseBottomNavActivity {

    private ActivitySuperadminBinding binding;
    private UsuarioAdapter adapter;
    private List<UsuarioListaSuperAdmin> listaOriginal = new ArrayList<>();
    private String rolSeleccionado = "Todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar RecyclerView
        adapter = new UsuarioAdapter(this, new ArrayList<>());
        binding.recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerUsuarios.setAdapter(adapter);

        // Cargar roles en dropdown
        String[] roles = {"Todos", "Administradores de hotel", "Taxistas", "Clientes"};
        ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, roles);
        binding.filtroRol.setAdapter(adapterRoles);

        // 👇 SELECCIONAR "Todos" POR DEFECTO
        binding.filtroRol.setText("Todos", false); // Selecciona sin disparar el evento
        rolSeleccionado = "Todos";                 // Asegura coherencia
        aplicarFiltros();

        // Listener del filtro de rol
        binding.filtroRol.setOnItemClickListener((parent, view, position, id) -> {
            rolSeleccionado = parent.getItemAtPosition(position).toString();

            if (rolSeleccionado.equals("Administradores de hotel")) {
                binding.btnAccionContextual.setVisibility(View.VISIBLE);
                binding.btnAccionContextual.setText("Registrar administrador");
                binding.btnAccionContextual.setIconResource(R.drawable.person_add_icon);
                binding.btnAccionContextual.setOnClickListener(v -> {
                    startActivity(new Intent(this, RegistrarAdmHotelActivity.class));
                });

            } else if (rolSeleccionado.equals("Taxistas")) {
                binding.btnAccionContextual.setVisibility(View.VISIBLE);
                binding.btnAccionContextual.setText("Solicitudes de acceso");
                binding.btnAccionContextual.setIconResource(R.drawable.pending_actions_icon);
                binding.btnAccionContextual.setOnClickListener(v -> {
                    startActivity(new Intent(this, SolicitudesActivity.class));
                });

            } else {
                binding.btnAccionContextual.setVisibility(View.GONE);
            }

            aplicarFiltros();
        });

        // Buscar por nombre o correo
        binding.etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarFiltros();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Cargar datos de prueba
        cargarUsuariosDeEjemplo();

        // Configurar bottom nav (usa lógica de BaseBottomNavActivity)
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        binding.bottomNavigationView.setItemRippleColor(ColorStateList.valueOf(Color.TRANSPARENT));
        binding.bottomNavigationView.setItemBackground(null);
        binding.bottomNavigationView.setStateListAnimator(null);
        configurarBottomNavigation(bottomNav);
    }

    private void aplicarFiltros() {
        String texto = binding.etBuscar.getText().toString().toLowerCase().trim();
        adapter.filtrar(texto, rolSeleccionado);
    }

    private void cargarUsuariosDeEjemplo() {
        listaOriginal.add(new UsuarioListaSuperAdmin("Jorge Coronado", "coronadomaxwell@pucp.edu.pe", "Administrador de hotel","coronado.png" , true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Lucía Quispe", "lucia@email.com", "Taxista", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Ana Pérez", "ana@email.com", "Cliente", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Giorgio Maxwell", "gmaxwell@gmail.com", "Taxista", "coronado.png", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Nilo Cori", "nilocori@pucp.edu.pe", "Administrador de hotel", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Juan Pérez", "juanexample@hotmail.com", "Administrador de hotel", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Adrian Tipo", "adriantipo@pucp.edu.pe", "Administrador de hotel", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Adrian López", "adrianlopez@pucp.edu.pe", "Administrador de hotel", "", true));
        listaOriginal.add(new UsuarioListaSuperAdmin("Pedro BM", "pedro@pucp.edu.pe", "Administrador de hotel", "pedro.png", true));
        adapter.setListaCompleta(listaOriginal);
        aplicarFiltros();
    }
}