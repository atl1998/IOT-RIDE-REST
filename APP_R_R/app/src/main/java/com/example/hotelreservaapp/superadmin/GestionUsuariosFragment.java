package com.example.hotelreservaapp.superadmin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hotelreservaapp.AdminHotel.NotificacionesActivity;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.adapter.UsuarioAdapter;
import com.example.hotelreservaapp.databinding.SuperadminGestionUsuariosFragmentBinding;
import com.example.hotelreservaapp.model.UsuarioListaSuperAdmin;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class GestionUsuariosFragment extends Fragment {

    private SuperadminGestionUsuariosFragmentBinding binding;
    private UsuarioAdapter adapter;
    private List<UsuarioListaSuperAdmin> listaOriginal = new ArrayList<>();
    private String rolSeleccionado = "Todos";

    public GestionUsuariosFragment() {
        super(R.layout.superadmin_gestion_usuarios_fragment);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SuperadminGestionUsuariosFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar RecyclerView
        adapter = new UsuarioAdapter(requireContext(), new ArrayList<>());
        binding.recyclerUsuarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerUsuarios.setAdapter(adapter);

        ImageView iconHelp = view.findViewById(R.id.iconHelp);
        iconHelp.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Gestión de usuarios")
                    .setMessage("Aquí puedes filtrar, buscar y gestionar a todos los usuarios registrados en el sistema.")
                    .setPositiveButton("Entendido", null)
                    .show();
        });
        //Notificaciones :D
        ImageView campana = binding.iconNotificaciones;  // o view.findViewById(...)

        campana.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NotificacionesSAActivity.class);
            startActivity(intent);
        });

        // Cargar roles en dropdown
        String[] roles = {"Todos", "Administradores de hotel", "Taxistas", "Clientes"};
        ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, roles);
        binding.filtroRol.setAdapter(adapterRoles);

        // Seleccionar "Todos" por defecto
        binding.filtroRol.setText("Todos", false);
        rolSeleccionado = "Todos";
        aplicarFiltros();

        // Listener del filtro de rol
        binding.filtroRol.setOnItemClickListener((parent, v, position, id) -> {
            rolSeleccionado = parent.getItemAtPosition(position).toString();

            if (rolSeleccionado.equals("Administradores de hotel")) {
                binding.btnAccionContextual.setVisibility(View.VISIBLE);
                binding.btnAccionContextual.setText("Registrar administrador");
                binding.btnAccionContextual.setIconResource(R.drawable.person_add_icon);
                binding.btnAccionContextual.setOnClickListener(vv -> {
                    startActivity(new Intent(requireContext(), RegistrarAdmHotelActivity.class));
                });

            } else if (rolSeleccionado.equals("Taxistas")) {
                binding.btnAccionContextual.setVisibility(View.VISIBLE);
                binding.btnAccionContextual.setText("Solicitudes de acceso");
                binding.btnAccionContextual.setIconResource(R.drawable.pending_actions_icon);
                binding.btnAccionContextual.setOnClickListener(vv -> {
                    startActivity(new Intent(requireContext(), SolicitudesActivity.class));
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
        cargarUsuarios();
    }

    private void aplicarFiltros() {
        String texto = binding.etBuscar.getText().toString().toLowerCase().trim();
        adapter.filtrar(texto, rolSeleccionado);
    }

    private void cargarUsuarios() {
        FirebaseFirestore.getInstance().collection("usuarios")
                .whereNotEqualTo("rol", "superadmin")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    listaOriginal.clear();  // Limpia la lista antes de llenarla

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String nombre = doc.getString("nombre") != null ? doc.getString("nombre") : "";
                        String apellido = doc.getString("apellido") != null ? doc.getString("apellido") : "";
                        String correo = doc.getString("correo");
                        String rol = doc.getString("rol");
                        if (rol != null && !rol.isEmpty()) {
                            if (rol.equals("adminHotel")){
                                rol = "Administrador de Hotel";
                            }else {
                                rol = rol.substring(0, 1).toUpperCase() + rol.substring(1).toLowerCase();
                            }
                        }
                        String imagen = doc.contains("urlFotoPerfil") ? doc.getString("urlFotoPerfil") : "";
                        boolean estado = Boolean.TRUE.equals(doc.getBoolean("estado"));

                        UsuarioListaSuperAdmin usuario = new UsuarioListaSuperAdmin(
                                nombre + " " + apellido,
                                correo,
                                rol,
                                imagen,
                                estado
                        );
                        listaOriginal.add(usuario);
                    }

                    adapter.setListaCompleta(listaOriginal);
                    aplicarFiltros();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
                );
    }
}
