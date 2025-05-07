package com.example.hotelreservaapp.cliente;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hotelreservaapp.R;

import java.util.List;

/**
 * Adaptador para mostrar comentarios en un RecyclerView
 */
public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder> {

    private List<Comentario> listaComentarios;
    private Context context;

    public ComentarioAdapter(Context context, List<Comentario> listaComentarios) {
        this.context = context;
        this.listaComentarios = listaComentarios;
    }

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cliente_item_comentario_hotel, parent, false);
        return new ComentarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = listaComentarios.get(position);

        // Configurar los datos en las vistas
        holder.txtNombreUsuario.setText(comentario.getNombreUsuario());
        holder.txtCalificacion.setText(String.valueOf(comentario.getCalificacion()));
        holder.txtComentario.setText(comentario.getComentario());
        holder.imgPerfil.setImageResource(comentario.getImagenPerfil());
    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    /**
     * ViewHolder para la vista de cada elemento de comentario
     */
    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPerfil;
        TextView txtNombreUsuario, txtCalificacion, txtComentario;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPerfil = itemView.findViewById(R.id.imgPerfil);
            txtNombreUsuario = itemView.findViewById(R.id.nombreUsuario);
            txtCalificacion = itemView.findViewById(R.id.calificacionUsuario);
            txtComentario = itemView.findViewById(R.id.comentarioCliente);
        }
    }

    // Método para actualizar los datos
    public void actualizarDatos(List<Comentario> nuevosComentarios) {
        this.listaComentarios = nuevosComentarios;
        notifyDataSetChanged();
    }
}
