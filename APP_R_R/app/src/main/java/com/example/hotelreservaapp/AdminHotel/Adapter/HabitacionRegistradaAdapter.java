package com.example.hotelreservaapp.AdminHotel.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.hotelreservaapp.AdminHotel.DetalleHabitacionActivity;
import com.example.hotelreservaapp.AdminHotel.Model.Habitacion;
import com.example.hotelreservaapp.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HabitacionRegistradaAdapter extends RecyclerView.Adapter<HabitacionRegistradaAdapter.HabitacionViewHolder> {

    private List<Habitacion> habitaciones;
    private OnItemClickListener listener;
    private Context context;
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onSeleccionCambio();
    }

    public HabitacionRegistradaAdapter(List<Habitacion> habitaciones, Context context, HabitacionRegistradaAdapter.OnItemClickListener listener) {
        this.habitaciones = habitaciones;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public HabitacionRegistradaAdapter.HabitacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adminhotel_item_habitacion, parent, false);
        return new HabitacionRegistradaAdapter.HabitacionViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitacionRegistradaAdapter.HabitacionViewHolder holder, int position) {
        Habitacion habitacion = habitaciones.get(position);

        holder.tvTipo.setText(habitacion.getTipo());
        holder.tvDetalles.setText(String.format(
                "- Capacidad para %s personas\n- Tamaño en m2: %.2f",
                habitacion.getCapacidad(),
                habitacion.getTamano()
        ));


        holder.tvPrecio.setText(String.format("Precio para 2 noches: $%.2f", habitacion.getPrecio()));

        // Cargar imagen desde URL con Glide
        String imageUrl = habitacion.getUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.bedroom_parent_24dp_black)
                    .error(R.drawable.bedroom_parent_24dp_black)
                    .into(holder.tvImagen);
        } else {
            holder.tvImagen.setImageResource(R.drawable.bedroom_parent_24dp_black);
        }

        /*
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetalleHabitacionActivity.class);
            v.getContext().startActivity(intent);
        });

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
        return habitaciones.size();
    }

    public static class HabitacionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTipo, tvDetalles, tvPrecio;

        ImageView tvImagen;
        Button btnSeleccionar;
        LinearLayout layoutSeleccion;
        TextView tvSeleccionadas;
        Button btnEliminar;

        public HabitacionViewHolder(@NonNull View itemView, final HabitacionRegistradaAdapter.OnItemClickListener listener) {
            super(itemView);

            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvDetalles = itemView.findViewById(R.id.tvDetalles);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvImagen = itemView.findViewById(R.id.imgHotel);

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
