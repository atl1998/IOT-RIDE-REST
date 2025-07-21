package com.example.hotelreservaapp.AdminHotel.Model;

public class Chat {
    private String nombreHotel;
    private String horaMensaje;
    private String ultimoMensaje;
    private boolean enviadoPorMi;
    private boolean leido;
    private boolean leidoPorMi;
    private String chatId; // ¡IMPORTANTE!

    public Chat() {} // Necesario para Firestore

    public Chat(String nombreHotel, String horaMensaje, String ultimoMensaje,
                boolean enviadoPorMi, boolean leido, boolean leidoPorMi) {
        this.nombreHotel = nombreHotel;
        this.horaMensaje = horaMensaje;
        this.ultimoMensaje = ultimoMensaje;
        this.enviadoPorMi = enviadoPorMi;
        this.leido = leido;
        this.leidoPorMi = leidoPorMi;
    }

    public String getnombreHotel() {
        return nombreHotel;
    }

    public void setNombreHotel(String nombreHotel) {
        this.nombreHotel = nombreHotel;
    }

    public String getHoraMensaje() {
        return horaMensaje;
    }

    public void setHoraMensaje(String horaMensaje) {
        this.horaMensaje = horaMensaje;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(String ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }

    public boolean isEnviadoPorMi() {
        return enviadoPorMi;
    }

    public void setEnviadoPorMi(boolean enviadoPorMi) {
        this.enviadoPorMi = enviadoPorMi;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }

    public boolean isLeidoPorMi() {
        return leidoPorMi;
    }

    public void setLeidoPorMi(boolean leidoPorMi) {
        this.leidoPorMi = leidoPorMi;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}

