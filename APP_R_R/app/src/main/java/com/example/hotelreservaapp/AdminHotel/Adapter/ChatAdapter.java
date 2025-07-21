package com.example.hotelreservaapp.AdminHotel.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservaapp.AdminHotel.Model.*;
import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.AdminHotel.DetallesChatActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<com.example.hotelreservaapp.AdminHotel.Adapter.ChatAdapter.ChatViewHolder> {

    private List<Chat> chatList;

    public ChatAdapter(List<Chat> chatList) {
        this.chatList = chatList;
    }

    // Método para actualizar la lista con los resultados filtrados
    public void updateList(List<Chat> newChatList) {
        chatList = new ArrayList<>();
        chatList.addAll(newChatList);
        notifyDataSetChanged(); // Notificar a RecyclerView que la lista ha cambiado
    }

    @NonNull
    @Override
    public com.example.hotelreservaapp.AdminHotel.Adapter.ChatAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout del item (el layout de cada chat)
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_cliente, parent, false);
        return new com.example.hotelreservaapp.AdminHotel.Adapter.ChatAdapter.ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.hotelreservaapp.AdminHotel.Adapter.ChatAdapter.ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);

        holder.nombreHotel.setText(chat.getnombreHotel());
        holder.horaMensaje.setText(chat.getHoraMensaje());
        holder.ultimoMensaje.setText(chat.getUltimoMensaje());

        if (chat.isEnviadoPorMi()) {
            // Si el mensaje lo envié yo, mostrar checks según si fue leído
            holder.chatNoLeido.setVisibility(View.GONE);
            holder.dobleCheck.setVisibility(View.VISIBLE);
            if (chat.isLeido()) {
                holder.dobleCheck.setImageResource(R.drawable.chat_leido); // check azul
            } else {
                holder.dobleCheck.setImageResource(R.drawable.chat_enviado); // check gris
            }
        } else {
            // El mensaje me lo enviaron a mí
            if (chat.isLeidoPorMi()) {
                holder.chatNoLeido.setVisibility(View.GONE);
                holder.dobleCheck.setVisibility(View.GONE);
            } else {
                holder.chatNoLeido.setVisibility(View.VISIBLE);
                holder.dobleCheck.setVisibility(View.GONE);
            }
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetallesChatActivity.class);
            intent.putExtra("chatId", chat.getChatId());
            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }

    // Clase ViewHolder que mantiene las vistas de cada elemento del RecyclerView
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView nombreHotel, horaMensaje, ultimoMensaje;
        ImageView dobleCheck, chatNoLeido;

        public ChatViewHolder(View itemView) {
            super(itemView);
            nombreHotel = itemView.findViewById(R.id.nombreHotel);
            horaMensaje = itemView.findViewById(R.id.hora_mensaje);
            ultimoMensaje = itemView.findViewById(R.id.ultimo_mensaje);
            dobleCheck = itemView.findViewById(R.id.doble_check);
            chatNoLeido = itemView.findViewById(R.id.chat_no_leido);
        }
    }
}
