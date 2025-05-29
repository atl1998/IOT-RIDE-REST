package com.example.hotelreservaapp.taxista.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.taxista.MapaActividad;
import com.example.hotelreservaapp.taxista.DetallesViajeActivity;
import com.example.hotelreservaapp.taxista.model.TarjetaModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TarjetaTaxistaAdapter extends RecyclerView.Adapter<TarjetaTaxistaAdapter.ViewHolder> {

    public interface OnNotificacionListener {
        void onViajeAceptado(TarjetaModel tarjeta);
        void onViajeCancelado(TarjetaModel tarjeta);
    }

    public static List<TarjetaModel> listaCompartida = new ArrayList<>();
    private Context context;
    private OnNotificacionListener notificacionListener;

    public TarjetaTaxistaAdapter(List<TarjetaModel> todas, Context context, OnNotificacionListener listener) {
        this.context = context;
        this.notificacionListener = listener;
        listaCompartida = ordenarPorPrioridad(todas);
    }

    private List<TarjetaModel> ordenarPorPrioridad(List<TarjetaModel> listaOriginal) {
        List<TarjetaModel> enProgreso = new ArrayList<>();
        List<TarjetaModel> solicitados = new ArrayList<>();

        for (TarjetaModel item : listaOriginal) {
            if ("En progreso".equalsIgnoreCase(item.getEstado())) {
                enProgreso.add(item);
            } else if ("Solicitado".equalsIgnoreCase(item.getEstado())) {
                solicitados.add(item);
            }
        }

        Collections.reverse(solicitados);
        List<TarjetaModel> resultado = new ArrayList<>();
        resultado.addAll(enProgreso);
        resultado.addAll(solicitados);
        return resultado;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.taxista_tarjeta_solicitudes, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TarjetaModel item = listaCompartida.get(position);

        holder.nombreUsuario.setText(item.getNombreUsuario());
        holder.fecha.setText(item.getFecha());
        holder.hora.setText(item.getHora());
        holder.ubicacion.setText(item.getUbicacion());
        holder.estado.setText(item.getEstado());

        switch (item.getEstado()) {
            case "En progreso":
                holder.estado.setTextColor(context.getResources().getColor(R.color.verde_aceptar));
                holder.btnAceptar.setVisibility(View.GONE);
                holder.btnCancelar.setVisibility(View.VISIBLE);
                break;
            case "Solicitado":
                holder.estado.setTextColor(context.getResources().getColor(R.color.azul));
                holder.btnAceptar.setVisibility(View.VISIBLE);
                holder.btnCancelar.setVisibility(View.GONE);
                break;
            case "Cancelado":
                holder.estado.setTextColor(context.getResources().getColor(R.color.error_red));
                holder.btnAceptar.setVisibility(View.GONE);
                holder.btnCancelar.setVisibility(View.GONE);
                break;
            case "Finalizado":
                holder.estado.setTextColor(Color.GRAY);
                holder.btnAceptar.setVisibility(View.GONE);
                holder.btnCancelar.setVisibility(View.GONE);
                break;
        }

        holder.btnAceptar.setOnClickListener(v -> {
            boolean yaEnCurso = false;
            for (TarjetaModel t : listaCompartida) {
                if ("En progreso".equalsIgnoreCase(t.getEstado())) {
                    yaEnCurso = true;
                    break;
                }
            }

            if (yaEnCurso) {
                Toast.makeText(context, "No puedes aceptar el viaje porque tienes uno en curso", Toast.LENGTH_SHORT).show();
            } else {
                item.setEstado("En progreso");
                listaCompartida = ordenarPorPrioridad(listaCompartida);
                notifyDataSetChanged();

                if (notificacionListener != null) {
                    notificacionListener.onViajeAceptado(item);
                }

                Intent intent = new Intent(context, MapaActividad.class);
                intent.putExtra("nombre", item.getNombreUsuario());
                intent.putExtra("telefono", item.getTelefono());
                context.startActivity(intent);
            }
        });

        holder.btnCancelar.setOnClickListener(v -> {
            item.setEstado("Cancelado");
            listaCompartida.remove(position);
            notifyDataSetChanged();

            if (notificacionListener != null) {
                notificacionListener.onViajeCancelado(item);
            }
        });

        holder.btnVerMapa.setOnClickListener(v -> {
            Intent intent = new Intent(context, MapaActividad.class);
            intent.putExtra("ubicacion", item.getUbicacion());
            intent.putExtra("nombre", item.getNombreUsuario());
            intent.putExtra("telefono", item.getTelefono());
            intent.putExtra("viajeEnCurso", "En progreso".equalsIgnoreCase(item.getEstado()));
            context.startActivity(intent);
        });

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallesViajeActivity.class);
            intent.putExtra("nombre", item.getNombreUsuario());
            intent.putExtra("correo", item.getCorreo());
            intent.putExtra("telefono", item.getTelefono());
            intent.putExtra("fecha", item.getFecha());
            intent.putExtra("hora", item.getHora());
            intent.putExtra("ubicacion", item.getUbicacion());
            intent.putExtra("destino", item.getDestino());
            intent.putExtra("estado", item.getEstado());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaCompartida.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreUsuario, fecha, hora, ubicacion, estado;
        Button btnAceptar, btnCancelar;
        View btnVerMapa;
        androidx.cardview.widget.CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            nombreUsuario = itemView.findViewById(R.id.nombreUsuario);
            fecha = itemView.findViewById(R.id.fechaSolicitud);
            hora = itemView.findViewById(R.id.horaSolicitud);
            ubicacion = itemView.findViewById(R.id.ubicacionSolicitud);
            estado = itemView.findViewById(R.id.status);
            btnAceptar = itemView.findViewById(R.id.btnAccionSolicitud);
            btnCancelar = itemView.findViewById(R.id.btnCancelarSolicitud);
            btnVerMapa = itemView.findViewById(R.id.btnVerEnELMapa);
            cardView = itemView.findViewById(R.id.cardCompleto);
        }
    }
}
