package com.example.hotelreservaapp.AdminHotel.Adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.AdminHotel.Model.ReservaInicio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {
    private List<ReservaInicio> reservas;
    private Context context;

    public ReservaAdapter(Context context) {
        this.context = context;
        this.reservas = new ArrayList<>();
        cargarReservas();
    }

    private void cargarReservas() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        System.out.println("ReservaAdapter – UID actual: " + uid);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(userDoc -> {
                    String hotelId = userDoc.getString("idHotel");
                    System.out.println("ReservaAdapter – hotelId obtenido: " + hotelId);
                    if (hotelId == null) {
                        System.out.println("ReservaAdapter – El usuario no tiene hotel asignado");
                        Toast.makeText(context, "No tienes hotel asignado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    db.collection("reservaas").document(hotelId).get()
                            .addOnSuccessListener(resDoc -> {
                                if (!resDoc.exists()) {
                                    System.out.println("ReservaAdapter – No existe documento de reservas para hotel " + hotelId);
                                    return;
                                }
                                List<ReservaInicio> list = resDoc.toObject(ReservasWrapper.class).listareservas;
                                System.out.println("ReservaAdapter – Reservas totales: " + (list != null ? list.size() : "null"));

                                Date hoy = new Date();
                                List<ReservaInicio> filtradas = new ArrayList<>();
                                for (ReservaInicio r : list) {
                                    if (r.getFechaInicioCheckIn().before(hoy)) {
                                        filtradas.add(r);
                                    }
                                }
                                System.out.println("ReservaAdapter – Reservas filtradas (< hoy): " + filtradas.size());

                                Collections.sort(filtradas, (a, b) ->
                                        b.getFechaInicioCheckIn().compareTo(a.getFechaInicioCheckIn()));
                                System.out.println("ReservaAdapter – Orden descendente aplicado");

                                for (ReservaInicio r : filtradas) {
                                    System.out.println("ReservaAdapter – Pidiendo nombre para usuario " + r.getIdUsuario());
                                    db.collection("usuarios").document(r.getIdUsuario()).get()
                                            .addOnSuccessListener(user -> {
                                                String nombre = user.getString("nombre");
                                                String apellido = user.getString("apellido");
                                                String url = user.getString("urlFotoPerfil");
                                                r.setNombreCompleto(nombre + " " + apellido);
                                                r.setUrlFoto(url);
                                                System.out.println("ReservaAdapter – Nombre completo seteado: " + r.getNombreCompleto());
                                                notifyDataSetChanged();
                                            })
                                            .addOnFailureListener(e -> {
                                                System.out.println("ReservaAdapter – Error al cargar usuario " + r.getIdUsuario());
                                                e.printStackTrace();
                                            });
                                }

                                reservas.clear();
                                reservas.addAll(filtradas);
                                System.out.println("ReservaAdapter – Adapter actualizado con " + reservas.size() + " ítems");
                                notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                System.out.println("ReservaAdapter – Error al leer reservas");
                                e.printStackTrace();
                            });
                })
                .addOnFailureListener(e -> {
                    System.out.println("ReservaAdapter – Error al leer usuario");
                    e.printStackTrace();
                });
    }


    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adminhotel_item_inicio    , parent, false);
        return new ReservaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        ReservaInicio r = reservas.get(position);
        holder.tvNombre.setText(r.getNombreCompleto());
        String fecha = DateFormat.getDateInstance().format(r.getFechaInicioCheckIn());
        holder.tvFecha.setText("Check In: " + fecha);

        // Cargar imagen desde URL con Glide
        String imageUrl = r.getUrlFoto();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.default_user_icon)
                    .error(R.drawable.default_user_icon)
                    .into(holder.ivFoto);
        } else {
            holder.ivFoto.setImageResource(R.drawable.default_user_icon);
        }


    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }

    static class ReservaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvFecha;
        ImageView ivFoto;

        ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            ivFoto = itemView.findViewById(R.id.ivFoto);
        }
    }

    // Wrapper para mapear el array de reservas en Firestore
    public static class ReservasWrapper {
        public List<ReservaInicio> listareservas;
        public ReservasWrapper() {}
    }
}
