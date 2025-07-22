// ReporteServicioAdapter.java
package com.example.hotelreservaapp.AdminHotel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotelreservaapp.AdminHotel.Model.Servicio;
import com.example.hotelreservaapp.R;

import java.util.List;

public class ReporteServicioAdapter
        extends RecyclerView.Adapter<ReporteServicioAdapter.ServiciosViewHolder> {

    private final List<Servicio> servicios;
    private final Context context;

    public ReporteServicioAdapter(List<Servicio> servicios, Context context) {
        this.servicios = servicios;
        this.context = context;
    }

    @NonNull
    @Override
    public ServiciosViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adminhotel_item_reporte_servicio, parent, false);
        return new ServiciosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ServiciosViewHolder holder, int position) {

        Servicio s = servicios.get(position);
        holder.tvTitulo.setText(s.getNombre());
        holder.tvPrecio.setText(String.format("S/. %.2f", s.getPrecio()));
    }

    @Override
    public int getItemCount() {
        return servicios.size();
    }

    static class ServiciosViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvPrecio;

        public ServiciosViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
        }
    }
}
