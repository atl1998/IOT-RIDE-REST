package com.example.hotelreservaapp.cliente;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;

import java.util.List;

public class HabitacionAdapter extends RecyclerView.Adapter<HabitacionAdapter.HabitacionViewHolder> {

    private List<Habitacion> habitaciones;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onSeleccionCambio();
    }

    public HabitacionAdapter(List<Habitacion> habitaciones, OnItemClickListener listener) {
        this.habitaciones = habitaciones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cliente_item_habitacion, parent, false);
        return new HabitacionViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitacionViewHolder holder, int position) {
        Habitacion habitacion = habitaciones.get(position);

        holder.tvTitulo.setText(habitacion.getNombre());
        holder.tvDetalles.setText("- Precio para 2 adultos\n- 1 cama doble extra grande\n- Turneño 30 m2\n- WiFi de alto velocidad\n- Desayuno incluido");
        holder.tvDisponibles.setText("Habitaciones disponibles: " + habitacion.getCantDisponible());
        holder.tvPrecio.setText(String.format("Precio por noches: $%.2f", habitacion.getPrecio()));

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
        });
    }

    @Override
    public int getItemCount() {
        return habitaciones.size();
    }

    public static class HabitacionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDetalles, tvDisponibles, tvPrecio;
        Button btnSeleccionar;
        LinearLayout layoutSeleccion;
        TextView tvSeleccionadas;
        Button btnEliminar;

        public HabitacionViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDetalles = itemView.findViewById(R.id.tvDetalles);
            tvDisponibles = itemView.findViewById(R.id.tvDisponibles);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);

            btnSeleccionar = itemView.findViewById(R.id.btnSeleccionar);
            layoutSeleccion = itemView.findViewById(R.id.layoutSeleccion);
            tvSeleccionadas = itemView.findViewById(R.id.tvSeleccionadas);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);

            btnSeleccionar.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
