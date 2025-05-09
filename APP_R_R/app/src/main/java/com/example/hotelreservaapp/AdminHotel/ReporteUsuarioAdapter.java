package com.example.hotelreservaapp.AdminHotel;

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

public class ReporteUsuarioAdapter extends RecyclerView.Adapter<com.example.hotelreservaapp.AdminHotel.ReporteUsuarioAdapter.MyViewHolder> {
    private List<UsuarioListaSuperAdmin> itemList;

    Context context;

    // Constructor del adapter
    public ReporteUsuarioAdapter(Context context, List<UsuarioListaSuperAdmin> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public com.example.hotelreservaapp.AdminHotel.ReporteUsuarioAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflar el layout de cada item (puede ser un TextView o un CardView)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adminhotel_item_reporte_usuario, parent, false);
        return new com.example.hotelreservaapp.AdminHotel.ReporteUsuarioAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.hotelreservaapp.AdminHotel.ReporteUsuarioAdapter.MyViewHolder holder, int position) {
        // Asignar el valor del item a la vista correspondiente
        UsuarioListaSuperAdmin usuario = itemList.get(position);
        holder.tvNombre.setText(usuario.getNombre());
        holder.tvCheck.setText(usuario.getCorreo());
        holder.tvFecha.setText(usuario.getRol());
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
        TextView tvNombre, tvCheck, tvFecha;
        ImageView ivFoto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCheck = itemView.findViewById(R.id.tvCheck);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            ivFoto = itemView.findViewById(R.id.tvFoto);
        }
    }
}

