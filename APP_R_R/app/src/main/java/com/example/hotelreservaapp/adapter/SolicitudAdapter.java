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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.model.SolicitudTaxista;
import com.example.hotelreservaapp.superadmin.DetalleSolicitudActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SolicitudAdapter extends RecyclerView.Adapter<SolicitudAdapter.SolicitudViewHolder> {

    private Context context;
    private Activity activity;

    private List<SolicitudTaxista> listaCompleta;
    private List<SolicitudTaxista> listaFiltrada;

    public SolicitudAdapter(Activity activity, List<SolicitudTaxista> lista) {
        this.activity = activity;
        this.context = activity;
        this.listaCompleta = new ArrayList<>(lista);
        this.listaFiltrada = new ArrayList<>(lista);
    }

    public void setListaCompleta(List<SolicitudTaxista> nuevaLista) {
        this.listaCompleta = new ArrayList<>(nuevaLista);
        this.listaFiltrada = new ArrayList<>(nuevaLista);
        notifyDataSetChanged();
    }

    public void filtrar(String texto) {
        listaFiltrada.clear();
        texto = texto.toLowerCase().trim();
        for (SolicitudTaxista s : listaCompleta) {
            if (s.getNombre().toLowerCase().contains(texto) || s.getCorreo().toLowerCase().contains(texto)) {
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
        SolicitudTaxista s = listaFiltrada.get(position);

        holder.tvNombre.setText(s.getNombre() + " " + s.getApellido());
        holder.tvCorreo.setText(s.getCorreo());
        holder.tvPlaca.setText("Placa: " + s.getPlaca());

        // Imagen de usuario
        String rutaFotoUsuario = "file:///android_asset/" + s.getFotoUsuario();
        try {
            context.getAssets().open(s.getFotoUsuario());
            Glide.with(context)
                    .load(rutaFotoUsuario)
                    .placeholder(R.drawable.default_user_icon)
                    .circleCrop()
                    .into(holder.ivFoto);
        } catch (IOException e) {
            Glide.with(context)
                    .load(R.drawable.default_user_icon)
                    .circleCrop()
                    .into(holder.ivFoto);
        }

        // Imagen de placa
        String rutaFotoPlaca = "file:///android_asset/" + s.getFotoPlaca();
        try {
            context.getAssets().open(s.getFotoPlaca());
            Glide.with(context)
                    .load(rutaFotoPlaca)
                    .placeholder(R.drawable.placa_demo)
                    .into(holder.ivPlaca);
        } catch (IOException e) {
            Glide.with(context)
                    .load(R.drawable.placa_demo)
                    .into(holder.ivPlaca);
        }

        // 👉 Evento clic: abrir nueva actividad
        holder.itemView.setOnClickListener(v -> {
            Log.d("SolicitudAdapter", "Card clickeado: " + s.getNombre());
            Intent intent = new Intent(activity, DetalleSolicitudActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("solicitud", s);
            activity.startActivity(intent);
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