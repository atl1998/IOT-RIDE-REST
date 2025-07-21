package com.example.hotelreservaapp.taxista.adapter;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.taxista.MapaActividad;
import com.example.hotelreservaapp.taxista.DetallesViajeActivity;
import com.example.hotelreservaapp.taxista.TaxistaMain;
import com.example.hotelreservaapp.taxista.model.TarjetaModel;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private static final String CHANNEL_ID = "ride_and_rest_channel";

    private boolean esHistorial = false;

    public TarjetaTaxistaAdapter(List<TarjetaModel> todas, Context context, OnNotificacionListener listener, boolean esHistorial) {
        this.context = context;
        this.notificacionListener = listener;
        this.esHistorial = esHistorial;
        listaCompartida = esHistorial ? todas : ordenarPorPrioridad(todas); // si es historial, no ordenar ni filtrar
        createNotificationChannel();
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Ride and Rest Notifications";
            String description = "Canal para notificaciones Ride and Rest";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void sendNotification(String title, String message) {
        Intent intent = new Intent(context, TaxistaMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        int notificationId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        notificationManager.notify(notificationId, builder.build());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.taxista_tarjeta_solicitudes, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TarjetaModel item = listaCompartida.get(position);

        holder.nombreUsuario.setText(item.getNombreCliente());
        holder.fecha.setText(item.getFecha());
        holder.hora.setText(item.getHora());
        holder.ubicacion.setText(item.getUbicacionOrigen());
        holder.estado.setText(item.getEstado());

        switch (item.getEstado()) {
            case "En progreso":
                holder.estado.setTextColor(context.getResources().getColor(R.color.verde_aceptar));
                holder.btnAceptar.setVisibility(View.GONE);
                holder.btnCancelar.setVisibility(View.VISIBLE);
                holder.btnVerMapa.setVisibility(View.VISIBLE);
                break;
            case "Solicitado":
                holder.estado.setTextColor(context.getResources().getColor(R.color.azul));
                holder.btnAceptar.setVisibility(View.VISIBLE);
                holder.btnCancelar.setVisibility(View.GONE);
                holder.btnVerMapa.setVisibility(View.VISIBLE);
                break;
            case "Cancelado":
                holder.estado.setTextColor(context.getResources().getColor(R.color.error_red));
                holder.btnAceptar.setVisibility(View.GONE);
                holder.btnCancelar.setVisibility(View.GONE);
                holder.btnVerMapa.setVisibility(View.GONE);
                break;
            case "Finalizado":
                holder.estado.setTextColor(Color.GRAY);
                holder.btnAceptar.setVisibility(View.GONE);
                holder.btnCancelar.setVisibility(View.GONE);
                holder.btnVerMapa.setVisibility(View.GONE);
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
                FirebaseFirestore.getInstance()
                        .collection("servicios_taxi")
                        .document(item.getIdCliente())
                        .update("estado", "En progreso")
                        .addOnSuccessListener(unused -> {
                            listaCompartida = ordenarPorPrioridad(listaCompartida);
                            notifyDataSetChanged();
                        });

                if (notificacionListener != null) {
                    notificacionListener.onViajeAceptado(item);
                }

                sendNotification("Solicitud aceptada", "Has aceptado el pedido de " + item.getNombreCliente());

                Intent intent = new Intent(context, MapaActividad.class);
                intent.putExtra("nombre", item.getNombreCliente());
                intent.putExtra("telefono", item.getTelefonoCliente());
                context.startActivity(intent);
            }
        });

        holder.btnCancelar.setOnClickListener(v -> {
            item.setEstado("Cancelado");
            FirebaseFirestore.getInstance()
                    .collection("servicios_taxi")
                    .document(item.getIdCliente())
                    .update("estado", "Cancelado")
                    .addOnSuccessListener(unused -> {
                        listaCompartida.remove(position);
                        notifyDataSetChanged();
                    });

            if (notificacionListener != null) {
                notificacionListener.onViajeCancelado(item);
            }

            sendNotification("Viaje cancelado", "Has cancelado el viaje con " + item.getNombreCliente());
        });

        holder.btnVerMapa.setOnClickListener(v -> {
            Intent intent = new Intent(context, MapaActividad.class);
            intent.putExtra("nombre", item.getNombreCliente());
            intent.putExtra("telefono", item.getTelefonoCliente());
            intent.putExtra("latOrigen", item.getLatOrigen());
            intent.putExtra("lngOrigen", item.getLngOrigen());
            intent.putExtra("latDestino", item.getLatDestino());
            intent.putExtra("lngDestino", item.getLngDestino());
            intent.putExtra("viajeEnCurso", "En progreso".equalsIgnoreCase(item.getEstado()));
            context.startActivity(intent);
        });


        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallesViajeActivity.class);
            intent.putExtra("nombre", item.getNombreCliente());
            intent.putExtra("correo", item.getCorreoCliente());
            intent.putExtra("telefono", item.getTelefonoCliente());
            intent.putExtra("fecha", item.getFecha());
            intent.putExtra("hora", item.getHora());
            intent.putExtra("ubicacion", item.getUbicacionOrigen());
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
        CardView cardView;

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
