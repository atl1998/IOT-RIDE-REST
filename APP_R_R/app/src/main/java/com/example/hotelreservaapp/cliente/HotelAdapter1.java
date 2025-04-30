package com.example.hotelreservaapp.cliente;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;

import java.util.List;

public class HotelAdapter1 extends RecyclerView.Adapter<HotelAdapter1.HotelViewHolder> {
    private List<Hotel> hotelList;
    private Context context;


    public HotelAdapter1(Context context, List<Hotel> hotelList) {
        this.context = context;
        this.hotelList = hotelList;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cliente_item_carrusel, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotelList.get(position);
        holder.bind(hotel, position);
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    public class HotelViewHolder extends RecyclerView.ViewHolder {
        private ImageView hotelImage;
        private TextView hotelName;
        private TextView hotelLocation;
        private TextView hotelPrice;
        private TextView hotelRating;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            hotelImage = itemView.findViewById(R.id.image_hotel);
            hotelName = itemView.findViewById(R.id.text_hotel_name);
            hotelLocation = itemView.findViewById(R.id.text_hotel_location);
            hotelPrice = itemView.findViewById(R.id.text_hotel_price);
            hotelRating = itemView.findViewById(R.id.text_hotel_rating);
        }

        public void bind(final Hotel hotel, final int position) {
            hotelName.setText(hotel.getNombre());
            hotelLocation.setText(hotel.getUbicacion());
            hotelPrice.setText(hotel.getPrecio());
            hotelRating.setText(String.valueOf(hotel.getRating()));
            hotelImage.setImageResource(hotel.getImagen());
        }
    }
}
