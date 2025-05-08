package com.example.hotelreservaapp.superadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.model.Reporte;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DetalleReserva extends BottomSheetDialogFragment {

    private final Reporte reporte;

    public DetalleReserva(Reporte reporte) {
        this.reporte = reporte;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.superadmin_detalle_reserva, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView tvTituloHotel = view.findViewById(R.id.tvTituloHotel);
        ImageView ivFotoHotel = view.findViewById(R.id.ivFotoHotel);
        ivFotoHotel.setImageResource(reporte.getImagenResId());
        TextView tvCliente = view.findViewById(R.id.tvCliente);
        TextView tvFechaReserva = view.findViewById(R.id.tvFechaReserva);
        TextView tvCheckIn = view.findViewById(R.id.tvCheckIn);
        TextView tvCheckOut = view.findViewById(R.id.tvCheckOut);
        TextView tvHabitacion = view.findViewById(R.id.tvHabitacion);
        TextView tvEstado = view.findViewById(R.id.tvEstado);
        String estado = reporte.getEstado();

        tvTituloHotel.setText(reporte.getHotel());
        tvCliente.setText("Cliente: " + reporte.getCliente());
        tvFechaReserva.setText("Fecha de reserva: " + reporte.getFecha());
        tvCheckIn.setText("Check-in: 12/05/2025");
        tvCheckOut.setText("Check-out: 14/05/2025");
        tvHabitacion.setText("Habitación: Doble superior");
        tvEstado.setText("Estado: " + estado);

        if (estado.equalsIgnoreCase("Confirmada")) {
            tvEstado.setTextColor(getResources().getColor(R.color.estado_confirmado));
        } else if (estado.equalsIgnoreCase("Cancelada")) {
            tvEstado.setTextColor(getResources().getColor(R.color.estado_cancelado));
        } else {
            tvEstado.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }
}

