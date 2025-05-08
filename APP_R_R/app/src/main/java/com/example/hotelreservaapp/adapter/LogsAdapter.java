package com.example.hotelreservaapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.model.LogItem;

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
        holder.tvFechaHora.setText(log.getFecha() + " - " + log.getHora());
        holder.imgUsuario.setImageResource(log.getFotoResId());
    }

    @Override
    public int getItemCount() {
        return listaLogs.size();
    }

    public void actualizarLista(List<LogItem> nuevaLista) {
        this.listaLogs = nuevaLista;
        notifyDataSetChanged();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tvAccion, tvUsuario, tvFechaHora;
        CircleImageView imgUsuario;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAccion = itemView.findViewById(R.id.tvAccion);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            imgUsuario = itemView.findViewById(R.id.imgUsuario);

        }
    }
}