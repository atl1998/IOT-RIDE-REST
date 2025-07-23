package com.example.hotelreservaapp.AdminHotel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotelreservaapp.AdminHotel.Model.ReporteUsuario;
import com.example.hotelreservaapp.R;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReporteUsuarioAdapter extends RecyclerView.Adapter<ReporteUsuarioAdapter.ViewHolder> {

    private final List<ReporteUsuario> lista;
    private final SimpleDateFormat dateFmt =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public ReporteUsuarioAdapter(List<ReporteUsuario> lista) {
        this.lista = lista;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adminhotel_item_reporte_usuario, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        ReporteUsuario r = lista.get(pos);
        h.tvNombre.setText(r.getNombre());
        h.tvTotal .setText(String.format("S/. %.2f", r.getTotalGastado()));
        h.tvFecha .setText(dateFmt.format(r.getCreadoEn()));
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvTotal, tvFecha;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvTotal  = itemView.findViewById(R.id.tvCosto);
            tvFecha  = itemView.findViewById(R.id.tvCheck);
        }
    }
}
