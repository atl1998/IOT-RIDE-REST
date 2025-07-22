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

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;

import java.util.List;
import java.util.Locale;

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

        holder.tvNombreHotel.setText(oferta.getNombre());
        holder.tvValoracion.setText(String.format(Locale.getDefault(), "%.1f ★", oferta.getValoracion()));
        holder.tvDepartamento.setText(oferta.getDepartamento());
        holder.tvPrecioMin.setText(String.format("Desde S/ %.2f", oferta.getPrecioMin()));

        // Cargar imagen desde URL con Glide
        Glide.with(context)
                .load(oferta.getUrlFotoHotel())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgHotel);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(oferta, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaOfertas.size();
    }

    public static class OfertaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgHotel;
        TextView tvNombreHotel, tvValoracion, tvDepartamento, tvPrecioMin;

        public OfertaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHotel = itemView.findViewById(R.id.imgHotel);
            tvNombreHotel = itemView.findViewById(R.id.tvNombreHotel);
            tvValoracion = itemView.findViewById(R.id.tvPuntuacion);
            tvDepartamento = itemView.findViewById(R.id.tvUbicacion);
            tvPrecioMin = itemView.findViewById(R.id.tvPrecioFinal);
        }
    }
}
