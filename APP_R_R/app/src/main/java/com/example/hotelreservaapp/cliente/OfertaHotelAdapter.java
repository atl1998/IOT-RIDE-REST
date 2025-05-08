package com.example.hotelreservaapp.cliente;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;

import java.util.List;

public class OfertaHotelAdapter extends RecyclerView.Adapter<OfertaHotelAdapter.OfertaViewHolder> {

    private List<OfertaHotel> listaOfertas;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(OfertaHotel oferta, int position);
    }

    public OfertaHotelAdapter(Context context, List<OfertaHotel> listaOfertas) {
        this.context = context;
        this.listaOfertas = listaOfertas;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public OfertaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_oferta_hotel, parent, false);
        return new OfertaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfertaViewHolder holder, int position) {
        OfertaHotel oferta = listaOfertas.get(position);

        holder.imgHotel.setImageResource(oferta.getImagenResourceId());
        holder.tvNombreHotel.setText(oferta.getNombre());
        holder.tvPuntuacion.setText(oferta.getPuntuacion());
        holder.tvCalificacion.setText(oferta.getCalificacionCompleta());
        holder.tvUbicacion.setText(oferta.getUbicacion());
        holder.tvDuracion.setText(oferta.getDuracion());
        holder.tvPrecioOriginal.setText(oferta.getPrecioOriginal());
        holder.tvPrecioFinal.setText(oferta.getPrecioFinal());

        // Mostrar u ocultar la etiqueta de oferta escapada según corresponda
        if (oferta.isTieneOfertaEscapada()) {
            holder.tvOfertaEscapada.setVisibility(View.VISIBLE);
        } else {
            holder.tvOfertaEscapada.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetallesHotel.class);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaOfertas.size();
    }

    public static class OfertaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgHotel;
        TextView tvNombreHotel, tvPuntuacion, tvCalificacion, tvUbicacion;
        TextView tvOfertaEscapada, tvDuracion, tvPrecioOriginal, tvPrecioFinal;

        public OfertaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHotel = itemView.findViewById(R.id.imgHotel);
            tvNombreHotel = itemView.findViewById(R.id.tvNombreHotel);
            tvPuntuacion = itemView.findViewById(R.id.tvPuntuacion);
            tvCalificacion = itemView.findViewById(R.id.tvCalificacion);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvOfertaEscapada = itemView.findViewById(R.id.tvOfertaEscapada);
            tvDuracion = itemView.findViewById(R.id.tvDuracion);
            tvPrecioOriginal = itemView.findViewById(R.id.tvPrecioOriginal);
            tvPrecioFinal = itemView.findViewById(R.id.tvPrecioFinal);
        }
    }
}