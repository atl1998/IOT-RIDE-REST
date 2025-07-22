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

    private static final String ARG_HOTEL = "hotel";
    private static final String ARG_CLIENTE = "cliente";
    private static final String ARG_FECHA = "fecha";
    private static final String ARG_ESTADO = "estado";
    private static final String ARG_IMAGEN = "imagenResId";
    private static final String ARG_CHECKIN = "checkIn";
    private static final String ARG_CHECKOUT = "checkOut";
    private static final String ARG_HABITACION = "habitacion";

    public static DetalleReserva newInstance(Reporte reporte) {
        DetalleReserva fragment = new DetalleReserva();
        Bundle args = new Bundle();
        args.putString(ARG_HOTEL, reporte.getHotel());
        args.putString(ARG_CLIENTE, reporte.getCliente());
        args.putString(ARG_FECHA, reporte.getFecha());
        args.putString(ARG_ESTADO, reporte.getEstado());
        args.putInt(ARG_IMAGEN, reporte.getImagenResId());
        args.putString(ARG_CHECKIN, reporte.getCheckIn());
        args.putString(ARG_CHECKOUT, reporte.getCheckOut());
        args.putString(ARG_HABITACION, reporte.getHabitacion());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.superadmin_detalle_reserva, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) return;

        String hotel = args.getString(ARG_HOTEL);
        String cliente = args.getString(ARG_CLIENTE);
        String fecha = args.getString(ARG_FECHA);
        String estado = args.getString(ARG_ESTADO);
        int imagenResId = args.getInt(ARG_IMAGEN);
        String checkIn = args.getString(ARG_CHECKIN);
        String checkOut = args.getString(ARG_CHECKOUT);
        String habitacion = args.getString(ARG_HABITACION);

        TextView tvTituloHotel = view.findViewById(R.id.tvTituloHotel);
        ImageView ivFotoHotel = view.findViewById(R.id.ivFotoHotel);
        TextView tvCliente = view.findViewById(R.id.tvCliente);
        TextView tvFechaReserva = view.findViewById(R.id.tvFechaReserva);
        TextView tvCheckIn = view.findViewById(R.id.tvCheckIn);
        TextView tvCheckOut = view.findViewById(R.id.tvCheckOut);
        TextView tvHabitacion = view.findViewById(R.id.tvHabitacion);
        TextView tvEstado = view.findViewById(R.id.tvEstado);

        tvTituloHotel.setText(hotel);
        ivFotoHotel.setImageResource(imagenResId);
        tvCliente.setText("Cliente: " + cliente);
        tvFechaReserva.setText("Fecha de reserva: " + fecha);
        tvCheckIn.setText("Check-in: " + checkIn);
        tvCheckOut.setText("Check-out: " + checkOut);
        tvHabitacion.setText("Habitación: " + habitacion);
        tvEstado.setText("Estado: " + estado);

        int color = R.color.black;
        if (estado != null) {
            if (estado.equalsIgnoreCase("Confirmada")) {
                color = R.color.estado_confirmado;
            } else if (estado.equalsIgnoreCase("Cancelada")) {
                color = R.color.estado_cancelado;
            }
        }
        tvEstado.setTextColor(requireContext().getColor(color));
    }
}