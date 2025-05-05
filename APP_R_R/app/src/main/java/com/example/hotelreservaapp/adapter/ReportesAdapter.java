package com.example.hotelreservaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.model.Reporte;

import java.util.List;

public class ReportesAdapter extends RecyclerView.Adapter<ReportesAdapter.ReporteViewHolder> {

    private List<Reporte> lista;

    public ReportesAdapter(List<Reporte> lista) {
        this.lista = lista;
    }

    public void actualizarLista(List<Reporte> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReporteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reporte, parent, false);
        return new ReporteViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteViewHolder holder, int position) {
        Reporte reporte = lista.get(position);
        String texto = "Hotel " + reporte.getHotel() +
                ": " + reporte.getAccion() +
                "\n" + reporte.getUsuario();
        holder.tvRegistro.setText(texto);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ReporteViewHolder extends RecyclerView.ViewHolder {
        TextView tvRegistro;

        public ReporteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRegistro = itemView.findViewById(R.id.tvRegistro);
        }
    }
}