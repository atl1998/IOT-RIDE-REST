package com.example.hotelreservaapp.cliente;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.Objetos.Notificaciones;
import com.example.hotelreservaapp.R;

import java.util.List;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.NotificacionViewHolder> {

    private List<Notificaciones> listaNotificaciones;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Notificaciones notificacion);
    }

    public NotificacionAdapter(List<Notificaciones> lista, Context context, OnItemClickListener listener) {
        this.listaNotificaciones = lista;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionViewHolder holder, int position) {
        Notificaciones n = listaNotificaciones.get(position);
        holder.titulo.setText(n.getTitulo());
        holder.mensaje.setText(n.getMensaje());

        // Puedes usar n.isLeido() para ocultar o mostrar el ícono
        holder.icono.setVisibility(n.isLeido() ? View.INVISIBLE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(n);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaNotificaciones.size();
    }

    public static class NotificacionViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, mensaje;
        ImageView icono;

        public NotificacionViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.texto_title);
            mensaje = itemView.findViewById(R.id.texto_mensaje);
            icono = itemView.findViewById(R.id.icono_notificacion);
        }
    }
}