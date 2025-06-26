package com.example.hotelreservaapp.cliente;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private List<HistorialItem> historialList;
    private Context context;
    private HistorialItemListener listener;

    public HistorialAdapter(Context context, List<HistorialItem> historialList, HistorialItemListener listener) {
        this.context = context;
        this.historialList = historialList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreHotel, fechas, ubicacion, status;
        ImageView imageHotel;
        Button btnCheckout, btnTaxista;
        View cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            nombreHotel = itemView.findViewById(R.id.nombreHotel);
            status = itemView.findViewById(R.id.status);
            fechas = itemView.findViewById(R.id.fechas);
            ubicacion = itemView.findViewById(R.id.ubicacion);
            imageHotel = itemView.findViewById(R.id.imageHotel);
            btnCheckout = itemView.findViewById(R.id.btnCheckout);
            btnTaxista = itemView.findViewById(R.id.btnTaxista);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }

    @Override
    public HistorialAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_historial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistorialAdapter.ViewHolder holder, int position) {
        HistorialItem item = historialList.get(position);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        String hotelId = item.getHotelId();
        StorageReference imageRef = firebaseStorage.getReference().child("fotos_hotel"+"/"+hotelId+"/"+hotelId+".jpg");
        Glide.with(holder.itemView.getContext()).load(imageRef).into(holder.imageHotel);
        holder.nombreHotel.setText(item.getNombreHotel());
        holder.status.setText(" "+item.getEstado());
        if (item.getEstado().equals("En Progreso")) {
            holder.status.setTextColor(Color.parseColor("#1B9923"));
        } else if (item.getEstado().equals("Terminado")) {
            holder.status.setTextColor(Color.parseColor("#666668"));
        }
        holder.fechas.setText(item.getFechas());
        holder.ubicacion.setText(item.getUbicacion());

        // Habilitar/deshabilitar btnCheckout según el campo del modelo
        holder.btnCheckout.setEnabled(item.isCheckoutEnabled());
        holder.btnCheckout.setAlpha(item.isCheckoutEnabled() ? 1f : 0.5f);

        // Igual para btnTaxista si tienes campo en modelo (ejemplo: isTaxiEnabled)
        holder.btnTaxista.setEnabled(item.isTaxiEnabled());
        holder.btnTaxista.setAlpha(item.isTaxiEnabled() ? 1f : 0.5f);

        holder.btnCheckout.setOnClickListener(v -> listener.onCheckoutClicked(item));
        holder.btnTaxista.setOnClickListener(v -> listener.onTaxiClicked(item));
        holder.itemView.setOnClickListener(v -> listener.onItemClicked(item));
    }

    @Override
    public int getItemCount() {
        return historialList.size();
    }
}

