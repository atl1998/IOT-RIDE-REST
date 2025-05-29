package com.example.hotelreservaapp.superadmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.model.Notificacion;
import com.example.hotelreservaapp.room.AppDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificacionSAAdapter extends RecyclerView.Adapter<NotificacionSAAdapter.ViewHolder> {

    private List<Notificacion> lista;
    private Context context;

    public NotificacionSAAdapter(List<Notificacion> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, mensaje, fecha;
        View bolita;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tvTituloNotif);
            mensaje = itemView.findViewById(R.id.tvMensajeNotif);
            fecha = itemView.findViewById(R.id.tvFechaNotif);
            bolita = itemView.findViewById(R.id.bolitaLeido);
        }
    }

    @NonNull
    @Override
    public NotificacionSAAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.superadmin_item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionSAAdapter.ViewHolder holder, int position) {
        Notificacion noti = lista.get(position);

        holder.titulo.setText(noti.titulo);
        holder.mensaje.setText(noti.mensaje);

        String fechaFormateada = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(noti.timestamp));
        holder.fecha.setText(fechaFormateada);

        holder.bolita.setVisibility(noti.leido ? View.GONE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (!noti.leido) {
                noti.leido = true;
                AppDatabase.getInstance(context).notificacionDao().actualizar(noti);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    public void setLista(List<Notificacion> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }
}