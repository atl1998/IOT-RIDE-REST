package com.example.hotelreservaapp.taxista.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.Objetos.Notificaciones;
import com.example.hotelreservaapp.R;

import java.util.List;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.NotificacionViewHolder> {

    private List<Notificaciones> listaNotificaciones;
    private Context context;

    public NotificacionesAdapter(Context context, List<Notificaciones> listaNotificaciones) {
        this.context = context;
        this.listaNotificaciones = listaNotificaciones;
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.taxista_item_notificaciones, parent, false);
        return new NotificacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionViewHolder holder, int position) {
        Notificaciones n = listaNotificaciones.get(position);

        holder.tvTituloNotif.setText(n.getTituloAmigable() != null ? n.getTituloAmigable() : n.getTitulo());
        holder.tvMensajeNotif.setText(n.getMensaje());
        holder.tvFechaNotif.setText(n.getFechaBonita() + " - " + n.getHoraBonita());

        holder.bolitaLeido.setVisibility(n.isLeido() ? View.GONE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (!n.isLeido()) {
                n.setLeido(true);
                notifyItemChanged(position);
                // Opcional: persistir cambio estado leído
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaNotificaciones.size();
    }

    public static class NotificacionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTituloNotif, tvMensajeNotif, tvFechaNotif;
        View bolitaLeido;

        public NotificacionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTituloNotif = itemView.findViewById(R.id.tvTituloNotif);
            tvMensajeNotif = itemView.findViewById(R.id.tvMensajeNotif);
            tvFechaNotif = itemView.findViewById(R.id.tvFechaNotif);
            bolitaLeido = itemView.findViewById(R.id.bolitaLeido);
        }
    }
}
