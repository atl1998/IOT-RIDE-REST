package com.example.hotelreservaapp.AdminHotel;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hotelreservaapp.AdminHotel.Adapter.HabitacionAdapter;
import com.example.hotelreservaapp.AdminHotel.Adapter.ServicioAdapter;
import com.example.hotelreservaapp.AdminHotel.Model.Habitacion;
import com.example.hotelreservaapp.AdminHotel.Model.Hotel;
import com.example.hotelreservaapp.AdminHotel.Model.Servicio;
import com.example.hotelreservaapp.AdminHotel.ViewModel.RegistroViewModel;
import com.example.hotelreservaapp.LogManager;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.databinding.AdminhotelRegistro3FragmentBinding;
import com.example.hotelreservaapp.databinding.AdminhotelRegistro5FragmentBinding;
import com.example.hotelreservaapp.model.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Registro5Servicios_fragment extends Fragment {
    private AdminhotelRegistro5FragmentBinding binding;
    private RegistroViewModel registroViewModel;
    private RecyclerView rvServicios;
    private ServicioAdapter adapter;

    private List<Servicio> listaServicios;

    private MaterialButton btnTerminar;
    private ImageView btnAdd;
    private FirebaseUser usuarioActual;
    private FirebaseFirestore db;

    private String hotelId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = AdminhotelRegistro5FragmentBinding.inflate(inflater, container, false);
        registroViewModel = new ViewModelProvider(requireActivity()).get(RegistroViewModel.class);
        db = FirebaseFirestore.getInstance();
        usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        listaServicios = new ArrayList<>();

        // Configurar el RecyclerView
        rvServicios = binding.listaServicios;
        rvServicios.setLayoutManager(new LinearLayoutManager(getContext()));
        rvServicios.setHasFixedSize(true); // Usa un LinearLayoutManager
        adapter = new ServicioAdapter(listaServicios,getContext(),  new ServicioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Servicio servicio = listaServicios.get(position);
                /*mostrarPopupSeleccion(habitacion);*/
            }

            @Override
            public void onSeleccionCambio() {
                /*verificarSeleccion();*/
            }
        });
        rvServicios.setAdapter(adapter);

        // Observa LiveData para actualizar lista y adaptador
        registroViewModel.getHotel().observe(getViewLifecycleOwner(), hotel -> {
            if (hotel != null && hotel.getServicios() != null) {
                listaServicios.clear();
                listaServicios.addAll(hotel.getServicios());
                adapter.notifyDataSetChanged();
            }
        });

        // Si está vacío, carga datos iniciales (solo una vez)
        if (registroViewModel.getHotel().getValue().getServicios() == null || registroViewModel.getHotel().getValue().getServicios().isEmpty()) {
            Hotel hotel =  registroViewModel.getHotel().getValue();
            hotel.setServicios(cargarData());
            registroViewModel.setHotel(hotel);
        }

        //logica para pasar a otro fragmento
        btnTerminar = binding.btnTerminar;
        btnTerminar.setOnClickListener(v -> {
            if (listaServicios != null) {
                // Guardar en database
                guardarHotel();
                actualizarAdminHotel();
                startActivity(new Intent(getActivity(), MainActivity.class));
            } else {
                Toast.makeText(getContext(), "Ingresa una foto", Toast.LENGTH_SHORT).show();
            }
        });

        //logica para añadir un cuarto
        btnAdd = binding.btnAdd;
        btnAdd.setOnClickListener(v -> {
            ((RegistroHotelActivity) requireActivity()).irASiguientePaso(new Registro6AddServicio_fragment());
        });


        return binding.getRoot();
    }

    private List<Servicio> cargarData() {
        List<Servicio> lista = new ArrayList<>();
        lista.add(new Servicio(
                "Buffet Almuerzo",
                "El buffet incluye todo tipo de comida criolla; nuestra carta incluye segundos como el arroz con pollo, carapulcra, entre otros; bebidas y postras como la chicha, mazamorra, agua de maracuyá, arroz con leche, causa rellena y más",
                123,
                "adminhotel_servicio_buffet.jpg"
        ));
        return lista;
    }

    private void guardarHotel(){
        Hotel hotel = registroViewModel.getHotel().getValue();
        if (hotel == null) return;

        Hotel hotelSinListas = new Hotel(
                hotel.getNombre(),
                hotel.getDescripcion(),
                hotel.getDepartamento(),
                hotel.getProvincia(),
                hotel.getDistrito(),
                hotel.getDireccion(),
                hotel.getUrlImage(),
                hotel.getValoracion()
        );

        // Guarda todo el objeto Proyecto, incluyendo la lista de Tarea
        db.collection("Hoteles")
                .add(hotelSinListas)
                .addOnSuccessListener(aVoid -> {
                    //Guardado de subcolecciones
                    hotelId = aVoid.getId();

                    // Luego guardas habitaciones como subcolección
                    for (Habitacion hab : hotel.getHabitaciones()) {
                        db.collection("Hoteles")
                                .document(hotelId)
                                .collection("habitaciones")
                                .add(hab);
                    }

                    // Y también los servicios como subcolección
                    for (Servicio srv : hotel.getServicios()) {
                        db.collection("Hoteles")
                                .document(hotelId)
                                .collection("servicios")
                                .add(srv);
                    }

                    //Parte del log
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();

                        FirebaseFirestore.getInstance()
                                .collection("usuarios")
                                .document(uid)
                                .get()
                                .addOnSuccessListener(doc -> {
                                    if (doc.exists()) {
                                        Usuario usuario = doc.toObject(Usuario.class);
                                        String nombres = usuario.getNombre();
                                        String apellidos = usuario.getApellido();
                                        String nombreCompleto = nombres + " " + apellidos;

                                        LogManager.registrarLogRegistro(
                                                nombreCompleto,
                                                "Registro de hotel",
                                                "El administrador de hotel" + nombreCompleto + "registró el hotel " + hotel.getNombre()
                                        );

                                        Log.d("Firestore", "Proyecto guardado correctamente");
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error guardando proyecto", e);
                });

    }

    private void actualizarAdminHotel(){
        Hotel hotel = registroViewModel.getHotel().getValue();
        // Cargar datos del usuario desde Firestore
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();  // o cualquier UID válido

        Map<String, Object> nuevosDatos = new HashMap<>();
        nuevosDatos.put("hotelAsignado", true);
        nuevosDatos.put("idHotel", hotelId);

        db.collection("usuarios").document(uid)
                .update(nuevosDatos)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Hotel creado correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al crear hotel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}