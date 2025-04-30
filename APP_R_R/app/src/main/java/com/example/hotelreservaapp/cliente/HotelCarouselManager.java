package com.example.hotelreservaapp.cliente;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;

import java.util.ArrayList;
import java.util.List;

public class HotelCarouselManager {
    private Context context;
    private RecyclerView recyclerView;
    private HotelAdapter adapter;
    private List<Hotel> hotelList;
    private TextView titleTextView;
    private TextView seeAllButton;

    public HotelCarouselManager(Context context) {
        this.context = context;
        hotelList = new ArrayList<>();
    }

    public void setupCarousel(View rootView, int recyclerViewId, int titleId, int seeAllId) {
        // Inicializar RecyclerView
        recyclerView = rootView.findViewById(recyclerViewId);

        // Configurar el layout manager (horizontal para el carrusel)
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Agregar espacio entre items

        recyclerView.setAdapter(adapter);

        // Configurar el título y el botón "See All" si se proporcionan
        if (titleId != 0) {
            titleTextView = rootView.findViewById(titleId);
            titleTextView.setText("Most Popular");
        }

        if (seeAllId != 0) {
            seeAllButton = rootView.findViewById(seeAllId);
            seeAllButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Esta lógica puede ser sobreescrita por una implementación personalizada
                }
            });
        }
    }

    // Configurar un listener personalizado para el botón "Ver todos"
    public void setSeeAllClickListener(View.OnClickListener listener) {
        if (seeAllButton != null) {
            seeAllButton.setOnClickListener(listener);
        }
    }

    // Configurar un listener personalizado para el botón de favorito


    // Agregar un hotel a la lista
    public void addHotel(Hotel hotel) {
        hotelList.add(hotel);
        if (adapter != null) {
            adapter.notifyItemInserted(hotelList.size() - 1);
        }
    }

    // Establecer la lista completa de hoteles
    public void setHotels(List<Hotel> hotels) {
        hotelList.clear();
        hotelList.addAll(hotels);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    // Obtener la lista actual de hoteles
    public List<Hotel> getHotels() {
        return hotelList;
    }

    // Clase para añadir espacio entre elementos
    public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int horizontalSpaceWidth;

        public HorizontalSpaceItemDecoration(int horizontalSpaceWidth) {
            this.horizontalSpaceWidth = horizontalSpaceWidth;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.right = horizontalSpaceWidth;
        }
    }
}

