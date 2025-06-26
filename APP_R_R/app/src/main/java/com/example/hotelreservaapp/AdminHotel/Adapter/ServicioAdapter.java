package com.example.hotelreservaapp.AdminHotel.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.AdminHotel.Model.Servicio;
import com.example.hotelreservaapp.R;

import java.io.IOException;
import java.util.List;

public class ServicioAdapter extends RecyclerView.Adapter<com.example.hotelreservaapp.AdminHotel.Adapter.ServicioAdapter.ServiciosViewHolder> {

    private List<Servicio> servicios;
    private com.example.hotelreservaapp.AdminHotel.Adapter.ServicioAdapter.OnItemClickListener listener;
    private Context context;
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onSeleccionCambio();
    }

    public ServicioAdapter(List<Servicio> Servicios, Context context, com.example.hotelreservaapp.AdminHotel.Adapter.ServicioAdapter.OnItemClickListener listener) {
        this.servicios = Servicios;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public com.example.hotelreservaapp.AdminHotel.Adapter.ServicioAdapter.ServiciosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adminhotel_item_servicio, parent, false);
        return new com.example.hotelreservaapp.AdminHotel.Adapter.ServicioAdapter.ServiciosViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.hotelreservaapp.AdminHotel.Adapter.ServicioAdapter.ServiciosViewHolder holder, int position) {
        Servicio servicio = servicios.get(position);

        holder.tvNombre.setText(servicio.getNombre());
        holder.tvDescipcion.setText(servicio.getDescripcion());
        holder.tvPrecio.setText(String.format("Precio por persona: $" + servicio.getPrecio()));

        String nombreArchivo = servicio.getUrl(); // ej. "image1.png"
        String rutaAsset = "file:///android_asset/" + nombreArchivo;

        try {
            // Intenta abrir la imagen para ver si existe
            context.getAssets().open(nombreArchivo);

            // Si no lanza excepción, la imagen existe
            Glide.with(context)
                    .load(rutaAsset)
                    .placeholder(R.drawable.bedroom_parent_24dp_black)
                    .into(holder.tvImagen);

        } catch (IOException e) {
            // Si falla (no existe), carga imagen por defecto
            Glide.with(context)
                    .load(R.drawable.bedroom_parent_24dp_black)
                    .into(holder.tvImagen);
        }
         /*
        if (habitacion.getSeleccionadas() > 0) {
            holder.btnSeleccionar.setVisibility(View.GONE);
            holder.layoutSeleccion.setVisibility(View.VISIBLE);
            holder.tvSeleccionadas.setText("Seleccionadas: " + habitacion.getSeleccionadas());
        } else {
            holder.btnSeleccionar.setVisibility(View.VISIBLE);
            holder.layoutSeleccion.setVisibility(View.GONE);
        }

        holder.btnEliminar.setOnClickListener(v -> {
            habitacion.setSeleccionadas(0);
            notifyItemChanged(position);
            if (listener != null) {
                listener.onSeleccionCambio();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return servicios.size();
    }

    public static class ServiciosViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescipcion, tvPrecio;

        ImageView tvImagen;
        Button btnSeleccionar;
        LinearLayout layoutSeleccion;
        TextView tvSeleccionadas;
        Button btnEliminar;

        public ServiciosViewHolder(@NonNull View itemView, final com.example.hotelreservaapp.AdminHotel.Adapter.ServicioAdapter.OnItemClickListener listener) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDescipcion = itemView.findViewById(R.id.tvDescripcion);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvImagen = itemView.findViewById(R.id.tvImagen);

            /*
            btnSeleccionar = itemView.findViewById(R.id.btnSeleccionar);
            layoutSeleccion = itemView.findViewById(R.id.layoutSeleccion);
            tvSeleccionadas = itemView.findViewById(R.id.tvSeleccionadas);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);*/

            /*btnSeleccionar.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });*/
        }
    }


}

