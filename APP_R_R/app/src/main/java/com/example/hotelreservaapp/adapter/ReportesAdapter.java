package com.example.hotelreservaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.model.Reporte;

import java.util.List;

public class ReportesAdapter extends RecyclerView.Adapter<ReportesAdapter.ReporteViewHolder> {

    private List<Reporte> listaReportes;

    public ReportesAdapter(List<Reporte> listaReportes) {
        this.listaReportes = listaReportes;
    }

    @NonNull
    @Override
    public ReporteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reporte_reserva, parent, false);
        return new ReporteViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteViewHolder holder, int position) {
        Reporte r = listaReportes.get(position);
        holder.tvHotel.setText(r.getHotel());
        holder.tvCliente.setText("Reserva realizada por: " + r.getCliente());
        holder.tvFecha.setText(r.getFecha());
        holder.tvEstado.setText(r.getEstado());
        switch (r.getEstado().toLowerCase()) {
            case "confirmada":
                holder.tvEstado.setTextColor(holder.itemView.getContext().getColor(R.color.estado_confirmado));
                break;
            case "cancelada":
                holder.tvEstado.setTextColor(holder.itemView.getContext().getColor(R.color.estado_cancelado));
                break;
            default:
                holder.tvEstado.setTextColor(holder.itemView.getContext().getColor(android.R.color.darker_gray));
                break;
        }
        holder.ivHotel.setImageResource(r.getImagenResId());
    }

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }

    public void actualizarLista(List<Reporte> nuevaLista) {
        this.listaReportes = nuevaLista;
        notifyDataSetChanged();
    }

    static class ReporteViewHolder extends RecyclerView.ViewHolder {
        TextView tvHotel, tvCliente, tvFecha, tvEstado;
        ImageView ivHotel;

        public ReporteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHotel = itemView.findViewById(R.id.tvHotel);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            ivHotel = itemView.findViewById(R.id.ivHotel);
        }
    }
}