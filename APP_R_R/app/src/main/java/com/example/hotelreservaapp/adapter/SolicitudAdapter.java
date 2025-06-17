package com.example.hotelreservaapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.model.PostulacionTaxista;
import com.example.hotelreservaapp.superadmin.DetalleSolicitudActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SolicitudAdapter extends RecyclerView.Adapter<SolicitudAdapter.SolicitudViewHolder> {

    private Context context;
    private Activity activity;
    private ActivityResultLauncher<Intent> launcher; // Nuevo campo para el launcher
    private List<PostulacionTaxista> listaCompleta;
    private List<PostulacionTaxista> listaFiltrada;
    // --- CONSTRUCTOR CORREGIDO ---
    // Ahora el constructor acepta el ActivityResultLauncher
    public SolicitudAdapter(Activity activity, List<PostulacionTaxista> lista, ActivityResultLauncher<Intent> launcher) {
        this.activity = activity;
        this.context = activity; // 'activity' ya es un 'Context'
        this.listaCompleta = new ArrayList<>(lista);
        this.listaFiltrada = new ArrayList<>(lista);
        this.launcher = launcher; // Asignamos el launcher aquí
    }

    public void setListaCompleta(List<PostulacionTaxista> nuevaLista) {
        this.listaCompleta = new ArrayList<>(nuevaLista);
        this.listaFiltrada = new ArrayList<>(nuevaLista);
        notifyDataSetChanged();
    }

    public void filtrar(String texto) {
        listaFiltrada.clear();
        texto = texto.toLowerCase().trim();
        for (PostulacionTaxista s : listaCompleta) {
            // Ampliando el filtro para incluir más campos (si deseas más flexibilidad en la búsqueda)
            if (s.getNombres().toLowerCase().contains(texto) ||
                    s.getApellidos().toLowerCase().contains(texto) || // Agregado para filtrar por apellido
                    s.getCorreo().toLowerCase().contains(texto) ||
                    (s.getNumeroPlaca() != null && s.getNumeroPlaca().toLowerCase().contains(texto))) { // Agregado para filtrar por placa
                listaFiltrada.add(s);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_solicitud_taxista, parent, false);
        return new SolicitudViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudViewHolder holder, int position) {
        PostulacionTaxista s = listaFiltrada.get(position);

        holder.tvNombre.setText(s.getNombres() + " " + s.getApellidos());
        holder.tvCorreo.setText(s.getCorreo());
        // Asegúrate de manejar el caso si getNumeroPlaca() puede ser null desde Firestore
        holder.tvPlaca.setText("Placa: " + (s.getNumeroPlaca() != null ? s.getNumeroPlaca() : "N/A"));

        // --- Carga de imágenes con Glide (CORREGIDO y SIN try-catch de IOException) ---
        Glide.with(context)
                .load(s.getUrlFotoPerfil()) // Carga la URL directamente
                .placeholder(R.drawable.default_user_icon) // Imagen mientras carga
                .error(R.drawable.default_user_icon)     // Imagen si la URL es null/inválida/falla
                .circleCrop()
                .into(holder.ivFoto);

        Glide.with(context)
                .load(s.getFotoPlacaURL()) // Carga la URL directamente
                .placeholder(R.drawable.placa_demo) // Imagen mientras carga
                .error(R.drawable.placa_demo)     // Imagen si la URL es null/inválida/falla
                .into(holder.ivPlaca);

        // 👉 Evento clic: abrir nueva actividad usando el launcher
        holder.itemView.setOnClickListener(v -> {
            Log.d("SolicitudAdapter", "Card clickeado: " + s.getNombres());
            Intent intent = new Intent(activity, DetalleSolicitudActivity.class);
            intent.putExtra("solicitud", s);

            // IMPORTANTE: NO usar FLAG_ACTIVITY_NEW_TASK con ActivityResultLauncher
            // a menos que sea estrictamente necesario y entiendas las implicaciones.
            // Para un flujo simple de "abrir detalle y regresar", no es recomendable.
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Lanzamos la actividad usando el launcher que recibimos
            if (launcher != null) {
                launcher.launch(intent);
            } else {
                // Fallback: Si por alguna razón el launcher no se pasó (no debería pasar),
                // iniciamos la actividad de la forma tradicional.
                activity.startActivity(intent);
                Log.e("SolicitudAdapter", "ActivityResultLauncher es null. Se usó startActivity directamente.");
                Toast.makeText(context, "Error interno: la lista no se actualizará automáticamente.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    public static class SolicitudViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCorreo, tvPlaca;
        ImageView ivFoto, ivPlaca;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCorreo = itemView.findViewById(R.id.tvCorreo);
            tvPlaca = itemView.findViewById(R.id.tvPlaca);
            ivFoto = itemView.findViewById(R.id.ivFoto);
            ivPlaca = itemView.findViewById(R.id.ivPlaca);
        }
    }
}