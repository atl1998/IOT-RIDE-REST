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

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TarjetaTaxistaAdapter extends RecyclerView.Adapter<TarjetaTaxistaAdapter.ViewHolder> {

    public interface OnNotificacionListener {
        void onViajeAceptado(TarjetaModel tarjeta);
        void onViajeCancelado(TarjetaModel tarjeta);
    }

    // Estados canonizados
    private static final String EST_SOLICITADO = "Solicitado";
    private static final String EST_ACEPTADO    = "Aceptado";
    private static final String EST_EN_CURSO    = "EnCurso";
    private static final String EST_CANCELADO   = "Cancelado";
    private static final String EST_FINALIZADO  = "Finalizado";

    public static List<TarjetaModel> listaCompartida = new ArrayList<>();
    private final Context context;
    private final OnNotificacionListener notificacionListener;
    private final boolean esHistorial;
    private static final String CHANNEL_ID = "ride_and_rest_channel";

    String uidTaxista    = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String nombreTaxista = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();


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
        List<TarjetaModel> top       = new ArrayList<>();
        List<TarjetaModel> resto     = new ArrayList<>();

        for (TarjetaModel item : listaOriginal) {
            String st = item.getEstado();
            if (EST_ACEPTADO.equals(st) || EST_EN_CURSO.equals(st)) {
                top.add(item);
            } else {
                resto.add(item);
            }
        }

        // Ordenar el resto por timestamp ascendente (la más nueva al final)
        Collections.sort(resto, (a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));

        List<TarjetaModel> resultado = new ArrayList<>(top.size() + resto.size());
        resultado.addAll(top);
        resultado.addAll(resto);
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TarjetaModel item = listaCompartida.get(position);

        // 1) Textos y foto
        holder.nombreUsuario.setText(item.getNombreCliente());
        holder.fecha.setText(item.getFecha());
        holder.hora.setText(item.getHora());
        holder.ubicacion.setText(item.getUbicacionOrigen());
        holder.estado.setText(item.getEstado());
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

        // 2) Reset visibilidad botones
        holder.btnAceptar.setVisibility(View.GONE);
        holder.btnCancelar.setVisibility(View.GONE);
        holder.btnVerMapa.setVisibility(View.GONE);

        // 3) Configuración según estado
        switch (item.getEstado()) {
            case EST_SOLICITADO:
                holder.estado.setTextColor(context.getResources().getColor(R.color.azul));
                holder.btnAceptar.setVisibility(View.VISIBLE);
                holder.btnAceptar.setText("Aceptar Solicitud");
                holder.btnVerMapa.setVisibility(View.VISIBLE);
                break;
            case EST_ACEPTADO:
                holder.estado.setTextColor(context.getResources().getColor(R.color.verde_aceptar));
                holder.btnAceptar.setVisibility(View.VISIBLE);
                holder.btnAceptar.setText("Confirmar Recojo");
                holder.btnCancelar.setVisibility(View.VISIBLE);
                holder.btnCancelar.setText("Cancelar Solicitud");
                holder.btnVerMapa.setVisibility(View.VISIBLE);
                break;
            case EST_EN_CURSO:
                holder.estado.setTextColor(context.getResources().getColor(R.color.verde_aceptar));
                holder.btnCancelar.setVisibility(View.VISIBLE);
                holder.btnCancelar.setText("Cancelar Viaje");
                holder.btnVerMapa.setVisibility(View.VISIBLE);
                break;
            case EST_CANCELADO:
                holder.estado.setTextColor(context.getResources().getColor(R.color.error_red));
                break;
            case EST_FINALIZADO:
                holder.estado.setTextColor(Color.GRAY);
                break;
        }


        holder.btnAceptar.setOnClickListener(v -> {
            String est = item.getEstado();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (EST_SOLICITADO.equals(est)) {
                // 1) Comprueba que no haya otra carrera activa
                boolean conflicto = false;
                for (TarjetaModel t : listaCompartida) {
                    if (EST_ACEPTADO.equalsIgnoreCase(t.getEstado()) ||
                            EST_EN_CURSO.equalsIgnoreCase(t.getEstado())) {
                        conflicto = true;
                        break;
                    }
                }
                if (conflicto) {
                    Toast.makeText(context,
                            "Ya tienes un viaje aceptado o en curso",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2) Lee el perfil del taxista que acepta
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) return;
                String uidTaxista = currentUser.getUid();

                db.collection("usuarios")
                        .document(uidTaxista)
                        .get()
                        .addOnSuccessListener(doc -> {
                            if (!doc.exists()) return;

                            // 3) Extrae los campos que necesitas
                            String nombre    = doc.getString("nombre");
                            String apellido  = doc.getString("apellido");
                            String telefono  = doc.getString("telefono");
                            String fotoUrl   = doc.getString("urlFotoPerfil");
                            String nombreCompleto = ((nombre != null? nombre:"") + " " +
                                    (apellido != null? apellido:"")).trim();

                            // 4) Prepara todo el mapa de actualizaciones
                            Map<String,Object> updates = new HashMap<>();
                            updates.put("estado",          EST_ACEPTADO);
                            updates.put("taxistaId",       uidTaxista);
                            updates.put("taxistaNombre",   nombreCompleto);
                            updates.put("taxistaTelefono", telefono != null? telefono:"");
                            updates.put("taxistaFotoUrl",  fotoUrl != null? fotoUrl:"");

                            // 5) Aplica la actualización
                            db.collection("servicios_taxi")
                                    .document(item.getIdDocument())
                                    .update(updates)
                                    .addOnSuccessListener(u -> {
                                        // 6) Refleja el cambio en la UI
                                        item.setEstado(EST_ACEPTADO);
                                        notifyItemChanged(position);
                                        sendNotification(
                                                "Solicitud aceptada",
                                                "Has aceptado el viaje de " + item.getNombreCliente()
                                        );

                                        // 7) Ahora envía la push al cliente
                                        obtenerFCMTokenUsuario(item.getIdCliente(), new FCMTokenCallback() {
                                            @Override
                                            public void onTokenObtenido(String fcmToken) {
                                                Log.d("FCM", "El token es: " + fcmToken);
                                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                                if (currentUser == null) {
                                                    Log.e("FCM", "❌ Usuario no autenticado");
                                                    return;
                                                }

                                                String uidTaxista = currentUser.getUid(); // ← Aquí obtengo el UID directamente

                                                FirebaseFirestore.getInstance().collection("usuarios").document(uidTaxista)
                                                        .get()
                                                        .addOnSuccessListener(documentSnapshot -> {
                                                            if (documentSnapshot.exists()) {
                                                                String nombre = documentSnapshot.getString("nombre");
                                                                String apellido = documentSnapshot.getString("apellido");

                                                                if (nombre != null && apellido != null) {
                                                                    String nombreCompleto = nombre + " " + apellido;
                                                                    Log.d("FCM", "✅ Nombre del taxista: " + nombreCompleto);

                                                                    TaxistaPushNotification taxistaPushNotification = new TaxistaPushNotification();
                                                                    taxistaPushNotification.enviarNotificacionAlCliente(fcmToken, nombreCompleto);
                                                                } else {
                                                                    Log.w("FCM", "⚠️ Nombre o apellido faltante");
                                                                }
                                                            } else {
                                                                Log.e("FCM", "❌ Documento de taxista no encontrado");
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> Log.e("FCM", "❌ Error al obtener datos del taxista", e));
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Log.e("FCM", "Error al obtener token", e);
                                            }
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context,
                                                "Error al aceptar la solicitud",
                                                Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context,
                                    "Error cargando perfil del taxista",
                                    Toast.LENGTH_SHORT).show();
                        });
            }
            else if (EST_ACEPTADO.equals(est)) {
                // Aquí tu código para Aceptado → EnCurso, igual que antes...
                db.collection("servicios_taxi")
                        .document(item.getIdDocument())
                        .update("estado", EST_EN_CURSO)
                        .addOnSuccessListener(u -> {
                            item.setEstado(EST_EN_CURSO);
                            notifyItemChanged(position);
                            sendNotification(
                                    "Viaje en curso",
                                    "Recojo confirmado: " + item.getNombreCliente()
                            );
                        });
            }
        });


        // 5) btnCancelar: cancelar Solicitud, Aceptado o EnCurso
        holder.btnCancelar.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("servicios_taxi")
                    .document(item.getIdDocument())
                    .update("estado", EST_CANCELADO)
                    .addOnSuccessListener(u -> {
                        item.setEstado(EST_CANCELADO);
                        listaCompartida.remove(position);
                        notifyItemRemoved(position);
                        sendNotification("Viaje cancelado", "Has cancelado el viaje con " + item.getNombreCliente());
                    });
        });

        // 6) btnVerMapa: abre MapaActividad con todos los extras, incluyendo estado
        holder.btnVerMapa.setOnClickListener(v -> {
            Intent intent = new Intent(context, MapaActividad.class);
            intent.putExtra("serviceId", item.getIdDocument());
            intent.putExtra("estado",          item.getEstado());
            intent.putExtra("nombreCliente",   item.getNombreCliente());
            intent.putExtra("telefonoCliente", item.getTelefonoCliente());
            intent.putExtra("fotoCliente",     item.getFotoCliente());
            intent.putExtra("latOrigen",       item.getLatOrigen());
            intent.putExtra("lngOrigen",       item.getLngOrigen());
            intent.putExtra("latDestino",      item.getLatDestino());
            intent.putExtra("lngDestino",      item.getLngDestino());
            context.startActivity(intent);
        });

        // 7) cardView → DetallesViajeActivity (igual que antes)
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallesViajeActivity.class);
            intent.putExtra("idDocument", item.getIdDocument());
            intent.putExtra("nombreCliente",   item.getNombreCliente());
            intent.putExtra("correo",          item.getCorreoCliente());
            intent.putExtra("telefono",        item.getTelefonoCliente());
            intent.putExtra("fotoCliente",     item.getFotoCliente());
            intent.putExtra("fecha",           item.getFecha());
            intent.putExtra("hora",            item.getHora());
            intent.putExtra("ubicacionText",   item.getUbicacionOrigen());
            intent.putExtra("destinoText",     item.getDestino());
            intent.putExtra("estado",          item.getEstado());
            intent.putExtra("origen",   new LatLng(item.getLatOrigen(),  item.getLngOrigen()));
            intent.putExtra("destino",  new LatLng(item.getLatDestino(), item.getLngDestino()));
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

    public void obtenerFCMTokenUsuario(String idUsuario, FCMTokenCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios").document(idUsuario)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fcmToken = documentSnapshot.getString("fcmToken");
                        if (fcmToken != null) {
                            Log.d("Firestore", "✅ FCM Token: " + fcmToken);
                            callback.onTokenObtenido(fcmToken);
                        } else {
                            Log.w("Firestore", "⚠️ El campo fcmToken no está presente");
                            callback.onError(new Exception("Token vacío"));
                        }
                    } else {
                        callback.onError(new Exception("Documento no encontrado"));
                    }
                })
                .addOnFailureListener(callback::onError);
    }
    public interface FCMTokenCallback {
        void onTokenObtenido(String fcmToken);
        void onError(Exception e);
    }

}
