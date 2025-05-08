package com.example.hotelreservaapp.AdminHotel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.AdminHotel.Habitaciones;

import java.util.List;

public class HabitacionAdapter extends RecyclerView.Adapter<com.example.hotelreservaapp.AdminHotel.HabitacionAdapter.HabitacionViewHolder> {

    private List<Habitaciones> habitaciones;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onSeleccionCambio();
    }

    public HabitacionAdapter(List<Habitaciones> habitaciones, com.example.hotelreservaapp.AdminHotel.HabitacionAdapter.OnItemClickListener listener) {
        this.habitaciones = habitaciones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public com.example.hotelreservaapp.AdminHotel.HabitacionAdapter.HabitacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adminhotel_item_habitacion, parent, false);
        return new com.example.hotelreservaapp.AdminHotel.HabitacionAdapter.HabitacionViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.hotelreservaapp.AdminHotel.HabitacionAdapter.HabitacionViewHolder holder, int position) {
        Habitaciones habitacion = habitaciones.get(position);

        holder.tvTitulo.setText(habitacion.getTitulo());
        holder.tvDetalles.setText(habitacion.getDetalles());
        holder.tvDisponibles.setText("Habitaciones disponibles: " + habitacion.getDisponibles());
        holder.tvPrecio.setText(String.format("Precio para 2 noches: $%.2f", habitacion.getPrecio()));
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
        return habitaciones.size();
    }

    public static class HabitacionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDetalles, tvDisponibles, tvPrecio;
        Button btnSeleccionar;
        LinearLayout layoutSeleccion;
        TextView tvSeleccionadas;
        Button btnEliminar;

        public HabitacionViewHolder(@NonNull View itemView, final com.example.hotelreservaapp.AdminHotel.HabitacionAdapter.OnItemClickListener listener) {
            super(itemView);

            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDetalles = itemView.findViewById(R.id.tvDetalles);
            tvDisponibles = itemView.findViewById(R.id.tvDisponibles);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);

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
