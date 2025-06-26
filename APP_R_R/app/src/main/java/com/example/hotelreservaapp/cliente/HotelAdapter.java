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

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private List<Hotel> listaHoteles;
    private Context context;

    private long fechaInicioMillis;
    private long fechaFinMillis;
    private int adultos;
    private int ninos;

    public HotelAdapter(Context context, List<Hotel> listaHoteles, long fechaInicioMillis, long fechaFinMillis, int adultos, int ninos) {
        this.context = context;
        this.listaHoteles = listaHoteles;
        this.fechaInicioMillis = fechaInicioMillis;
        this.fechaFinMillis = fechaFinMillis;
        this.adultos = adultos;
        this.ninos = ninos;
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

        String nombreCompleto = hotel.getNombre() + " " + hotel.getHotelId();
        holder.txtNombre.setText(nombreCompleto);
        holder.txtUbicacion.setText(hotel.getDireccion());
        float valoracion= hotel.getValoracion();
        holder.ratingBar.setRating(valoracion);

        // Asignar descripción según el valor de valoración
        String descripcion;
        if (valoracion < 2.0) {
            descripcion = "Malo";
        } else if (valoracion < 3.0) {
            descripcion = "Regular";
        } else if (valoracion < 4.0) {
            descripcion = "Aceptable";
        } else if (valoracion < 4.5) {
            descripcion = "Bueno";
        } else {
            descripcion = "Fabuloso";
        }

        String valoracionStr = String.format(Locale.US, "%.1f", valoracion);
        holder.txtPuntuacion.setText(valoracionStr + " " + descripcion + " - 14 comentarios");

        if (fechaInicioMillis > 0 && fechaFinMillis > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", new Locale("es", "ES"));
            String fechaInicioStr = sdf.format(new Date(fechaInicioMillis));
            String fechaFinStr = sdf.format(new Date(fechaFinMillis));
            holder.txtFechas.setText("Desde el " + fechaInicioStr + " al " + fechaFinStr);
        } else {
            holder.txtFechas.setText("Fechas no seleccionadas");
        }

        Float precioMinHotel = hotel.getPrecioMin();
        String precioFormateado = String.format("%.2f", precioMinHotel);
        holder.txtPrecio.setText("Desde S/ " + precioFormateado);

        // Cargar imagen desde URL con Glide
        Glide.with(context)
                .load(hotel.getUrlFotoHotel())
                .placeholder(R.drawable.ic_launcher_background) // imagen de espera
                .error(R.drawable.ic_error) // imagen en caso de error (agrega si quieres)
                .into(holder.imgHotel);

        // Acción del botón Ver Detalles
        holder.btnVerDetalles.setOnClickListener(v -> {
            Toast.makeText(context, "Detalles de " + hotel.getNombre(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(context, DetallesHotel.class);
            intent.putExtra("hotelId", hotel.getHotelId());
            intent.putExtra("fechaInicio", fechaInicioMillis);
            intent.putExtra("fechaFin", fechaFinMillis);
            intent.putExtra("adultos", adultos);
            intent.putExtra("ninos", ninos);

            context.startActivity(intent);
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
            imgHotel = itemView.findViewById(R.id.imgHotel); // <- este es el usado con Glide
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetalles);
        }
    }
}


