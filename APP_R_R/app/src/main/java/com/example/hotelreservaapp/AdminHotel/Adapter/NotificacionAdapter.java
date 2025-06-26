package com.example.hotelreservaapp.AdminHotel.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.AdminHotel.Notificacion;
import com.example.hotelreservaapp.R;

import java.util.List;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.NotificacionViewHolder> {

    private List<Notificacion> listaNotificacion;

    public NotificacionAdapter(List<Notificacion> listaNotificacion) {
        this.listaNotificacion = listaNotificacion;
    }

    @NonNull
    @Override
    public NotificacionAdapter.NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificacionAdapter.NotificacionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionAdapter.NotificacionViewHolder holder, int position) {
        Notificacion log = listaNotificacion.get(position);
        holder.tvMensaje.setText(log.getContenido());
    }

    @Override
    public int getItemCount() {
        return listaNotificacion.size();
    }

    public void actualizarLista(List<Notificacion> nuevaLista) {
        this.listaNotificacion = nuevaLista;
        notifyDataSetChanged();
    }

    static class NotificacionViewHolder extends RecyclerView.ViewHolder {
        TextView tvMensaje;

        public NotificacionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.texto_mensaje);

        }
    }
}
