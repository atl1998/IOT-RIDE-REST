package com.example.hotelreservaapp.AdminHotel.Fragments;

import static com.google.common.reflect.Reflection.getPackageName;

import static java.lang.Double.min;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;

import androidx.core.content.FileProvider;
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

import com.example.hotelreservaapp.AdminHotel.Adapter.ServicioAdapter;
import com.example.hotelreservaapp.AdminHotel.MainActivity;
import com.example.hotelreservaapp.AdminHotel.Model.Habitacion;
import com.example.hotelreservaapp.AdminHotel.Model.Hotel;
import com.example.hotelreservaapp.AdminHotel.Model.HotelSinListas;
import com.example.hotelreservaapp.AdminHotel.Model.Servicio;
import com.example.hotelreservaapp.AdminHotel.RegistroHotelActivity;
import com.example.hotelreservaapp.AdminHotel.ViewModel.RegistroViewModel;
import com.example.hotelreservaapp.LogManager;
import com.example.hotelreservaapp.databinding.AdminhotelRegistro5FragmentBinding;
import com.example.hotelreservaapp.model.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
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

    private FirebaseStorage storage;

    private String hotelId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = AdminhotelRegistro5FragmentBinding.inflate(inflater, container, false);
        registroViewModel = new ViewModelProvider(requireActivity()).get(RegistroViewModel.class);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
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


        //logica para pasar a otro fragmento
        btnTerminar = binding.btnTerminar;
        btnTerminar.setOnClickListener(v -> {
            if (listaServicios != null) {
                // Guardar en database
                guardarHotel();
                startActivity(new Intent(getActivity(), MainActivity.class));
            } else {
                Toast.makeText(getContext(), "Ingresa una foto",     Toast.LENGTH_SHORT).show();
            }
        });

        //logica para añadir un cuarto
        btnAdd = binding.btnAdd;
        btnAdd.setOnClickListener(v -> {
            ((RegistroHotelActivity) requireActivity()).irASiguientePaso(new Registro6AddServicio_fragment());
        });


        return binding.getRoot();
    }


    private void guardarHotel(){
        Hotel hotel = registroViewModel.getHotel().getValue();
        if (hotel == null) return;

        double precioMin = 1e9;
        for (Habitacion ha : hotel.getHabitaciones()) {
            precioMin = min(precioMin, ha.getPrecio());
        }

        HotelSinListas hotelSinListas = new HotelSinListas(
                hotel.getNombre(),
                hotel.getDescripcion(),
                hotel.getDepartamento(),
                hotel.getProvincia(),
                hotel.getDistrito(),
                hotel.getDireccion(),
                hotel.getUrlFotoHotel(),
                hotel.getValoracion(),
                hotel.getContacto(),
                precioMin,
                hotel.getServicioTaxi(),
                usuarioActual.getUid()
        );

        // Guarda todo el objeto Proyecto, incluyendo la lista de Tarea
        db.collection("Hoteles")
                .add(hotelSinListas)
                .addOnSuccessListener(aVoid -> {
                    //Guardado de subcolecciones
                    hotelId = aVoid.getId();

                    CollectionReference habitacionesRef = db.collection("Hoteles")
                            .document(hotelId)
                            .collection("habitaciones");

                    CollectionReference serviciosRef = db.collection("Hoteles")
                            .document(hotelId)
                            .collection("servicios");

                    int contador1 = 1;
                    // Luego guardas habitaciones como subcolección
                    for (Habitacion hab : hotel.getHabitaciones()) {
                        // 1️⃣ Crear un DocumentReference con ID aleatorio
                        DocumentReference habitacionDoc = habitacionesRef.document();
                        String habitacionId = habitacionDoc.getId();
                        String nombreInterno   = "foto_habitacion_" + contador1 + ".jpg";

                        // Este método sube la foto y crea el documento en /Hoteles/{hotelId}/habitaciones/{habitacionId}
                        guardarFotoHabitacion(hotelId, habitacionId, nombreInterno, hab);
                        contador1++;
                    }

                    int contador2 = 1;
                    // Y también los servicios como subcolección
                    for (Servicio srv : hotel.getServicios()) {
                        // 1️⃣ Crear un DocumentReference con ID aleatorio
                        DocumentReference servicioDoc = serviciosRef.document();
                        String servicioId = servicioDoc.getId();
                        String nombreInterno   = "foto_servicio_" + contador2 + ".jpg";

                        // Este método sube la foto y crea el documento en /Hoteles/{hotelId}/habitaciones/{habitacionId}
                        guardarFotoServicio(hotelId, servicioId, nombreInterno, srv);
                        contador2++;
                    }

                    // 2️⃣ foto
                    guardarFotoHotel(hotelId, hotel.getUrlFotoHotel());

                    // 3️⃣ actualizar usuario
                    actualizarAdminHotel(hotelId);

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
                                                "El administrador de hotel " + nombreCompleto + "registró el hotel " + hotel.getNombre()
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

    private void guardarFotoHotel(String hotelId, String nombreInterno) {
        Context ctx = requireContext();
        File foto = new File(ctx.getFilesDir(), nombreInterno);
        if (!foto.exists()) {
            Log.w("FOTO", "No se encontró la imagen interna");
            return;
        }

        Uri uri = FileProvider.getUriForFile(
                ctx, ctx.getPackageName() + ".provider", foto);

        StorageReference ref = FirebaseStorage.getInstance()
                .getReference()
                .child("fotos_hotel")
                .child(hotelId)          // carpeta propia del hotel
                .child("fotoHotel.jpg");

        ref.putFile(uri)
                .continueWithTask(t -> {
                    if (!t.isSuccessful()) throw t.getException();
                    return ref.getDownloadUrl();
                })
                .addOnSuccessListener(url -> {
                    db.collection("Hoteles").document(hotelId)
                            .update("urlFotoHotel", url.toString())
                            .addOnSuccessListener(v -> {
                                foto.delete();      // limpia la copia interna
                                Toast.makeText(ctx, "Hotel creado correctamente", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ctx, MainActivity.class));
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ctx, "Error subiendo foto: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void guardarFotoHabitacion(String hotelId,
                                       String habitacionId,
                                       String nombreInternoFoto,
                                       Habitacion hab) {
        Context ctx = requireContext();
        File foto = new File(ctx.getFilesDir(), nombreInternoFoto);
        if (!foto.exists()) {
            Log.w("FOTO", "No se encontró la imagen interna: " + nombreInternoFoto);
            return;
        }

        Uri uri = FileProvider.getUriForFile(
                ctx, ctx.getPackageName() + ".provider", foto);

        // 1️⃣ Ruta Storage: hoteles/{hotelId}/habitaciones/{habitacionId}.jpg
        StorageReference ref = storage
                .getReference()
                .child("fotos_hotel")
                .child(hotelId)
                .child("habitaciones")
                .child(nombreInternoFoto);

        // 2️⃣ Subida + getDownloadUrl
        ref.putFile(uri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return ref.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> {
                    // 3️⃣ Crear/actualizar doc en la subcolección "habitaciones"
                    hab.setUrl(downloadUri.toString());
                    FirebaseFirestore.getInstance()
                            .collection("Hoteles")
                            .document(hotelId)
                            .collection("habitaciones")
                            .document(habitacionId)
                            .set(hab)
                            .addOnSuccessListener(a ->
                                    Log.d("Firestore", "Habitación guardada con foto: " + habitacionId))
                            .addOnFailureListener(e ->
                                    Log.e("Firestore", "Error guardando habitación", e));
                    foto.delete(); // opcional: limpiar cache local
                })
                .addOnFailureListener(e ->
                        Log.e("Storage", "Error subiendo foto de habitación", e));
    }


    private void guardarFotoServicio(String hotelId,
                                       String servicioId,
                                       String nombreInternoFoto,
                                       Servicio srv) {
        Context ctx = requireContext();
        File foto = new File(ctx.getFilesDir(), nombreInternoFoto);
        if (!foto.exists()) {
            Log.w("FOTO", "No se encontró la imagen interna: " + nombreInternoFoto);
            return;
        }

        Uri uri = FileProvider.getUriForFile(
                ctx, ctx.getPackageName() + ".provider", foto);

        // 1️⃣ Ruta Storage: fotos_hotel/{hotelId}/servicios/{servicioId}.jpg
        StorageReference ref = storage
                .getReference()
                .child("fotos_hotel")
                .child(hotelId)
                .child("servicios")
                .child(nombreInternoFoto);

        // 2️⃣ Subida + getDownloadUrl
        ref.putFile(uri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return ref.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> {
                    // 3️⃣ Crear/actualizar doc en la subcolección "habitaciones"
                    srv.setUrl(downloadUri.toString());
                    FirebaseFirestore.getInstance()
                            .collection("Hoteles")
                            .document(hotelId)
                            .collection("servicios")
                            .document(servicioId)
                            .set(srv)
                            .addOnSuccessListener(a ->
                                    Log.d("Firestore", "Servicio guardada con foto: " + servicioId))
                            .addOnFailureListener(e ->
                                    Log.e("Firestore", "Error guardando servicio", e));
                    foto.delete(); // opcional: limpiar cache local
                })
                .addOnFailureListener(e ->
                        Log.e("Storage", "Error subiendo foto de servicio", e));
    }





    private void actualizarAdminHotel(String hotelId){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String,Object> map = new HashMap<>();
        map.put("hotelAsignado", true);
        map.put("idHotel", hotelId);

        db.collection("usuarios").document(uid).update(map);
    }



}