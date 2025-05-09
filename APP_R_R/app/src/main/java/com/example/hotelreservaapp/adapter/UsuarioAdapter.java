package com.example.hotelreservaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.model.UsuarioListaSuperAdmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {
    private Context context;
    private List<UsuarioListaSuperAdmin> listaCompleta;
    private List<UsuarioListaSuperAdmin> listaFiltrada;

    public UsuarioAdapter(Context context, List<UsuarioListaSuperAdmin> listaUsuarios) {
        this.context = context;
        this.listaCompleta = new ArrayList<>(listaUsuarios);
        this.listaFiltrada = new ArrayList<>(listaUsuarios);
    }

    public void setListaCompleta(List<UsuarioListaSuperAdmin> nuevaLista) {
        this.listaCompleta = new ArrayList<>(nuevaLista);
        this.listaFiltrada = new ArrayList<>(nuevaLista);
        notifyDataSetChanged();
    }

    public void filtrar(String texto, String rolFiltro) {
        listaFiltrada.clear();
        for (UsuarioListaSuperAdmin u : listaCompleta) {
            boolean coincideRol = rolFiltro.equals("Todos")
                    || (rolFiltro.equals("Administradores de hotel") && u.getRol().equalsIgnoreCase("Administrador de hotel"))
                    || (rolFiltro.equals("Taxistas") && u.getRol().equalsIgnoreCase("Taxista"))
                    || (rolFiltro.equals("Clientes") && u.getRol().equalsIgnoreCase("Cliente"));

            boolean coincideTexto = u.getNombre().toLowerCase().contains(texto)
                    || u.getCorreo().toLowerCase().contains(texto);

            if (coincideRol && coincideTexto) {
                listaFiltrada.add(u);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.superadmin_item_usuario, parent, false);
        return new UsuarioViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        UsuarioListaSuperAdmin usuario = listaFiltrada.get(position);
        holder.tvNombre.setText(usuario.getNombre());
        holder.tvCorreo.setText(usuario.getCorreo());
        holder.tvRol.setText(usuario.getRol());
        holder.switchActivo.setChecked(usuario.isActivo());
        String nombreArchivo = usuario.getUrlFoto(); // ej. "image1.png"
        String rutaAsset = "file:///android_asset/" + nombreArchivo;

        try {
            // Intenta abrir la imagen para ver si existe
            context.getAssets().open(nombreArchivo);

            // Si no lanza excepción, la imagen existe
            Glide.with(context)
                    .load(rutaAsset)
                    .placeholder(R.drawable.default_user_icon)
                    .circleCrop()
                    .into(holder.ivFoto);

        } catch (IOException e) {
            // Si falla (no existe), carga imagen por defecto
            Glide.with(context)
                    .load(R.drawable.default_user_icon)
                    .circleCrop()
                    .into(holder.ivFoto);
        }
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCorreo, tvRol;
        ImageView ivFoto;
        Switch switchActivo;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCorreo = itemView.findViewById(R.id.tvCorreo);
            tvRol = itemView.findViewById(R.id.tvRol);
            ivFoto = itemView.findViewById(R.id.ivFoto);
            switchActivo = itemView.findViewById(R.id.switchActivo);
        }
    }
}
