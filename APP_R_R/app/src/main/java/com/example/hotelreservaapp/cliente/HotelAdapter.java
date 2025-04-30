package com.example.hotelreservaapp.cliente;

import android.content.Context;
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
        holder.ratingBar.setRating(hotel.getRating());
        holder.txtPuntuacion.setText(hotel.getPuntuacion());
        holder.txtUbicacion.setText(hotel.getUbicacion());
        holder.txtFechas.setText(hotel.getFechas());
        holder.txtPrecio.setText(hotel.getPrecio());
        holder.imgHotel.setImageResource(hotel.getImagen());

        // Puedes agregar un listener al botón si lo necesitas
        holder.btnVerDetalles.setOnClickListener(v -> {
            Toast.makeText(context, "Detalles de " + hotel.getNombre(), Toast.LENGTH_SHORT).show();
            // Aquí puedes abrir otra actividad o mostrar un dialog
        });
    }

    @Override
    public int getItemCount() {
        return listaHoteles.size();
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView imgHotel, imgFavorito;
        TextView txtNombre, txtPuntuacion, txtUbicacion, txtFechas, txtPrecio;
        RatingBar ratingBar;
        Button btnVerDetalles;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHotel = itemView.findViewById(R.id.imgHotel);
            imgFavorito = itemView.findViewById(R.id.imgFavorito);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPuntuacion = itemView.findViewById(R.id.txtPuntuacion);
            txtUbicacion = itemView.findViewById(R.id.txtUbicacion);
            txtFechas = itemView.findViewById(R.id.txtFechas);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetalles);
        }
    }
}
