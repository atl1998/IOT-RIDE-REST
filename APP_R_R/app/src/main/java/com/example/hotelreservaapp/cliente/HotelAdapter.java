package com.example.hotelreservaapp.cliente;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;

import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private List<Hotel> listaHoteles;
    private Context context;

    public HotelAdapter(Context context, List<Hotel> listaHoteles) {
        this.context = context;
        this.listaHoteles = listaHoteles;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.cliente_item_hotel, parent, false);
        return new HotelViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = listaHoteles.get(position);

        holder.txtNombre.setText(hotel.getNombre());
        holder.txtUbicacion.setText(hotel.getUbicacion());
        holder.ratingBar.setRating(hotel.getValoracion());

        // ⚠️ Estáticos por ahora
        holder.txtPuntuacion.setText("8.8 Fabuloso - 1434 comentarios"); // estático
        holder.txtFechas.setText("Desde el 28 abr al 2 mar"); // estático
        holder.txtPrecio.setText("Desde S/345.00"); // estático
        holder.imgHotel.setImageResource(R.drawable.hotel1_img1); // imagen por defecto

        // Aquí añades el nuevo campo 'contacto' o 'servicioTaxi'
        holder.btnVerDetalles.setOnClickListener(v -> {
            Toast.makeText(context, "Detalles de " + hotel.getNombre(), Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, DetallesHotel.class));
        });
    }

    @Override
    public int getItemCount() {
        return listaHoteles.size();
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtUbicacion, txtPuntuacion, txtFechas, txtPrecio;
        RatingBar ratingBar;
        Button btnVerDetalles;
        ImageView imgHotel;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtUbicacion = itemView.findViewById(R.id.txtUbicacion);
            txtPuntuacion = itemView.findViewById(R.id.txtPuntuacion);
            txtFechas = itemView.findViewById(R.id.txtFechas);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            imgHotel = itemView.findViewById(R.id.imgHotel);
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetalles);
        }
    }
}

