package com.example.hotelreservaapp.taxista.adapter;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.taxista.MapaActividad;
import com.example.hotelreservaapp.taxista.DetallesViajeActivity;
import com.example.hotelreservaapp.taxista.TaxistaMain;
import com.example.hotelreservaapp.taxista.TaxistaPushNotification;
import com.example.hotelreservaapp.taxista.ViajeEnCursoActivity;
import com.example.hotelreservaapp.taxista.model.TarjetaModel;
import com.google.android.gms.maps.model.LatLng;
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
    private final Context context;
    private final OnNotificacionListener notificacionListener;
    private final boolean esHistorial;
    private static final String CHANNEL_ID = "ride_and_rest_channel";

    public TarjetaTaxistaAdapter(List<TarjetaModel> todas,
                                 Context context,
                                 OnNotificacionListener listener,
                                 boolean esHistorial) {
        this.context = context;
        this.notificacionListener = listener;
        this.esHistorial = esHistorial;
        listaCompartida = esHistorial
                ? todas
                : ordenarPorPrioridad(todas);
        createNotificationChannel();
    }

    private List<TarjetaModel> ordenarPorPrioridad(List<TarjetaModel> listaOriginal) {
        List<TarjetaModel> enProgreso = new ArrayList<>();
        List<TarjetaModel> solicitados  = new ArrayList<>();
        for (TarjetaModel item : listaOriginal) {
            if ("En progreso".equalsIgnoreCase(item.getEstado()))
                enProgreso.add(item);
            else if ("Solicitado".equalsIgnoreCase(item.getEstado()))
                solicitados.add(item);
        }
        Collections.reverse(solicitados);
        List<TarjetaModel> resultado = new ArrayList<>();
        resultado.addAll(enProgreso);
        resultado.addAll(solicitados);
        return resultado;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name  = "Ride and Rest Notifications";
            String description = "Canal para notificaciones Ride and Rest";
            int importance     = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID, name, importance);
            ch.setDescription(description);
            NotificationManager mgr = context.getSystemService(NotificationManager.class);
            if (mgr != null) mgr.createNotificationChannel(ch);
        }
    }

    @SuppressLint("MissingPermission")
    private void sendNotification(String title, String message) {
        Intent intent = new Intent(context, TaxistaMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pi);
        NotificationManagerCompat.from(context)
                .notify((int)(System.currentTimeMillis() % Integer.MAX_VALUE),
                        builder.build());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.taxista_tarjeta_solicitudes, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TarjetaModel item = listaCompartida.get(position);

        // Textos
        holder.nombreUsuario.setText(item.getNombreCliente());
        holder.fecha.setText(item.getFecha());
        holder.hora.setText(item.getHora());
        holder.ubicacion.setText(item.getUbicacionOrigen());
        holder.estado.setText(item.getEstado());

        // Foto de perfil cliente
        String urlFoto = item.getFotoCliente();
        if (urlFoto != null && !urlFoto.isEmpty()) {
            Glide.with(context)
                    .load(urlFoto)
                    .placeholder(R.drawable.default_profile)
                    .circleCrop()
                    .into(holder.ivFotoCliente);
        } else {
            holder.ivFotoCliente.setImageResource(R.drawable.default_profile);
        }

        // Estados de botones
        switch (item.getEstado()) {
            case "En progreso":
                holder.estado.setTextColor(
                        context.getResources().getColor(R.color.verde_aceptar));
                holder.btnAceptar.setVisibility(View.GONE);
                holder.btnCancelar.setVisibility(View.VISIBLE);
                holder.btnVerMapa.setVisibility(View.VISIBLE);
                break;
            case "Solicitado":
                holder.estado.setTextColor(
                        context.getResources().getColor(R.color.azul));
                holder.btnAceptar.setVisibility(View.VISIBLE);
                holder.btnCancelar.setVisibility(View.GONE);
                holder.btnVerMapa.setVisibility(View.VISIBLE);
                break;
            case "Cancelado":
                holder.estado.setTextColor(
                        context.getResources().getColor(R.color.error_red));
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

        // Aceptar viaje
        holder.btnAceptar.setOnClickListener(v -> {
            boolean yaEnCurso = false;
            for (TarjetaModel t : listaCompartida) {
                if ("En progreso".equalsIgnoreCase(t.getEstado())) {
                    yaEnCurso = true;
                    break;
                }
            }
            if (yaEnCurso) {
                Toast.makeText(context,
                        "No puedes aceptar el viaje porque tienes uno en curso",
                        Toast.LENGTH_SHORT).show();
            } else {
                item.setEstado("En progreso");
                FirebaseFirestore.getInstance()
                        .collection("servicios_taxi")
                        .document(item.getIdDocument())
                        .update("estado", "En progreso")
                        .addOnSuccessListener(unused -> {
                            listaCompartida = ordenarPorPrioridad(listaCompartida);
                            notifyDataSetChanged();
                        });
                if (notificacionListener != null)
                    notificacionListener.onViajeAceptado(item);
                sendNotification("Solicitud aceptada",
                        "Has aceptado el pedido de " + item.getNombreCliente());

                //
                String idCliente = item.getIdCliente();
                String ola = "ola";

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("usuarios")
                        .document(idCliente)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String fcmTokenCliente = documentSnapshot.getString("fcmToken");
                                if (fcmTokenCliente != null) {
                                    TaxistaPushNotification tn = new TaxistaPushNotification();
                                    tn.enviarNotificacionAlCliente(fcmTokenCliente, "Nilo Cori");
                                } else {
                                    Log.w("FCM_TOKEN", "El cliente no tiene token FCM guardado");
                                }
                            } else {
                                Log.w("FCM_TOKEN", "Documento de cliente no existe");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FCM_TOKEN", "Error al obtener token FCM", e);
                        });
                //

                Intent i = new Intent(context, MapaActividad.class);
                i.putExtra("latOrigen",  item.getLatOrigen());
                i.putExtra("lngOrigen",  item.getLngOrigen());
                i.putExtra("latDestino", item.getLatDestino());
                i.putExtra("lngDestino", item.getLngDestino());
                context.startActivity(i);
            }
        });

        // Cancelar viaje
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
            if (notificacionListener != null)
                notificacionListener.onViajeCancelado(item);
            sendNotification("Viaje cancelado",
                    "Has cancelado el viaje con " + item.getNombreCliente());
        });

        holder.btnVerMapa.setOnClickListener(v -> {
            String est = item.getEstado();
            Intent intent;
            if ("En progreso".equalsIgnoreCase(est)) {
                intent = new Intent(context, ViajeEnCursoActivity.class);
            } else {
                intent = new Intent(context, MapaActividad.class);
            }

            // **Muy importante:** usar siempre la misma clave al poner y al leer
            intent.putExtra("nombreCliente",   item.getNombreCliente());
            intent.putExtra("telefonoCliente", item.getTelefonoCliente());
            intent.putExtra("fotoCliente",     item.getFotoCliente());
            intent.putExtra("latOrigen",       item.getLatOrigen());
            intent.putExtra("lngOrigen",       item.getLngOrigen());
            intent.putExtra("latDestino",      item.getLatDestino());
            intent.putExtra("lngDestino",      item.getLngDestino());

            context.startActivity(intent);
        });


        // Detalles completos
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallesViajeActivity.class);
            intent.putExtra("nombreCliente",       item.getNombreCliente());
            intent.putExtra("correo",       item.getCorreoCliente());
            intent.putExtra("telefono",     item.getTelefonoCliente());
            intent.putExtra("fotoCliente",  item.getFotoCliente());
            intent.putExtra("fecha",        item.getFecha());
            intent.putExtra("hora",         item.getHora());
            intent.putExtra("ubicacionText",item.getUbicacionOrigen());
            intent.putExtra("destinoText",  item.getDestino());
            intent.putExtra("estado",       item.getEstado());
            intent.putExtra("origen",       new LatLng(item.getLatOrigen(),  item.getLngOrigen()));
            intent.putExtra("destino",      new LatLng(item.getLatDestino(), item.getLngDestino()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaCompartida.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivFotoCliente;
        final TextView nombreUsuario, fecha, hora, ubicacion, estado;
        final Button btnAceptar, btnCancelar;
        final View btnVerMapa;
        final CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            ivFotoCliente = itemView.findViewById(R.id.iv_foto_cliente);
            nombreUsuario = itemView.findViewById(R.id.nombreUsuario);
            fecha         = itemView.findViewById(R.id.fechaSolicitud);
            hora          = itemView.findViewById(R.id.horaSolicitud);
            ubicacion     = itemView.findViewById(R.id.ubicacionSolicitud);
            estado        = itemView.findViewById(R.id.status);
            btnAceptar    = itemView.findViewById(R.id.btnAccionSolicitud);
            btnCancelar   = itemView.findViewById(R.id.btnCancelarSolicitud);
            btnVerMapa    = itemView.findViewById(R.id.btnVerEnELMapa);
            cardView      = itemView.findViewById(R.id.cardCompleto);
        }
    }
}
