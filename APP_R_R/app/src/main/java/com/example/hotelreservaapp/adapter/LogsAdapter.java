package com.example.hotelreservaapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.model.LogItem;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.LogViewHolder> {

    private List<LogItem> listaLogs;

    public LogsAdapter(List<LogItem> listaLogs) {
        this.listaLogs = listaLogs;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new LogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogItem log = listaLogs.get(position);
        holder.tvAccion.setText(log.getAccion());
        holder.tvUsuario.setText("Por: " + log.getUsuario());
        holder.tvFechaHora.setText(log.getFechaFormateada() + " - " + log.getHoraFormateada());
        holder.tvDetalle.setText(log.getDetalle());

        // Icono según tipo de acción
        String accion = log.getAccion().toLowerCase();
        if (accion.contains("creó") || accion.contains("agregó")) {
            holder.imgIcono.setImageResource(R.drawable.add_icon);
        } else if (accion.contains("eliminó") || accion.contains("canceló")) {
            holder.imgIcono.setImageResource(R.drawable.delete_icon);
        } else if (accion.contains("modificó") || accion
                .contains("actualizó")) {
            holder.imgIcono.setImageResource(R.drawable.edit_icon);
        } else if (accion.contains("check-out") || accion.contains("finalizó")) {
            holder.imgIcono.setImageResource(R.drawable.checkout_icon);
        } else {
            holder.imgIcono.setImageResource(R.drawable.info_icon);
        }

        holder.itemView.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(v.getContext())
                    .setTitle(log.getAccion())
                    .setMessage(log.getDetalle())
                    .setPositiveButton("Cerrar", null)
                    .show();
        });

    }

    @Override
    public int getItemCount() {
        return listaLogs.size();
    }
    public List<LogItem> getListaActual() {
        return listaLogs; // o la lista que estás usando internamente
    }

    public void actualizarLista(List<LogItem> nuevaLista) {
        this.listaLogs = nuevaLista;
        notifyDataSetChanged();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tvAccion, tvUsuario, tvFechaHora, tvDetalle;
        ImageView imgIcono;
        CircleImageView imgUsuario;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAccion = itemView.findViewById(R.id.tvAccion);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            tvDetalle = itemView.findViewById(R.id.tvDetalle);
            imgIcono = itemView.findViewById(R.id.imgIcono);

        }
    }
}