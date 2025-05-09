package com.example.hotelreservaapp.AdminHotel;

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
import com.example.hotelreservaapp.R;

import java.io.IOException;
import java.util.List;

public class ReporteServicioAdapter extends RecyclerView.Adapter<com.example.hotelreservaapp.AdminHotel.ReporteServicioAdapter.ServiciosViewHolder> {

    private List<Servicios> servicios;
    private OnItemClickListener listener;
    private Context context;
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onSeleccionCambio();
    }

    public ReporteServicioAdapter(List<Servicios> Servicios,Context context) {
        this.servicios = Servicios;
        this.context = context;
    }

    @NonNull
    @Override
    public com.example.hotelreservaapp.AdminHotel.ReporteServicioAdapter.ServiciosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adminhotel_item_reporte_servicio, parent, false);
        return new com.example.hotelreservaapp.AdminHotel.ReporteServicioAdapter.ServiciosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.hotelreservaapp.AdminHotel.ReporteServicioAdapter.ServiciosViewHolder holder, int position) {
        Servicios servicio = servicios.get(position);

        holder.tvTitulo.setText(servicio.getTitulo());
        holder.tvPrecio.setText(servicio.getPrecio());

        /*
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
        }*/

    }

    @Override
    public int getItemCount() {
        return servicios.size();
    }

    public static class ServiciosViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvPrecio;


        public ServiciosViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitulo = itemView.findViewById(R.id.tvTitulo);

            tvPrecio = itemView.findViewById(R.id.tvPrecio);


        }
    }
}
