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
import java.util.List;

public class Adminhotel_UsuarioAdapter extends RecyclerView.Adapter<Adminhotel_UsuarioAdapter.MyViewHolder> {
    private List<UsuarioListaSuperAdmin> itemList;

    Context context;

    // Constructor del adapter
    public Adminhotel_UsuarioAdapter(Context context, List<UsuarioListaSuperAdmin> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflar el layout de cada item (puede ser un TextView o un CardView)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.superadmin_item_usuario, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Asignar el valor del item a la vista correspondiente
        UsuarioListaSuperAdmin usuario = itemList.get(position);
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
        return itemList.size();
    }

    // ViewHolder para cada item
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCorreo, tvRol;
        ImageView ivFoto;
        Switch switchActivo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCorreo = itemView.findViewById(R.id.tvCorreo);
            tvRol = itemView.findViewById(R.id.tvRol);
            ivFoto = itemView.findViewById(R.id.ivFoto);
            switchActivo = itemView.findViewById(R.id.switchActivo);
        }
    }
}
