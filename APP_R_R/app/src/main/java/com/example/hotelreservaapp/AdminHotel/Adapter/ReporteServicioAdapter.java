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

public class ReporteServicioAdapter extends RecyclerView.Adapter<ReporteServicioAdapter.ServiciosViewHolder> {

    private List<Servicio> servicios;
    private OnItemClickListener listener;
    private Context context;
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onSeleccionCambio();
    }

    public ReporteServicioAdapter(List<Servicio> Servicios, Context context) {
        this.servicios = Servicios;
        this.context = context;
    }

    @NonNull
    @Override
    public ReporteServicioAdapter.ServiciosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adminhotel_item_reporte_servicio, parent, false);
        return new ReporteServicioAdapter.ServiciosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteServicioAdapter.ServiciosViewHolder holder, int position) {
        Servicio servicio = servicios.get(position);

        holder.tvTitulo.setText(servicio.getNombre());
        holder.tvPrecio.setText(Double.toString(servicio.getPrecio()));

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
