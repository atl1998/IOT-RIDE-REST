package com.example.hotelreservaapp.cliente;

public class Chat {
    private String nombreHotel;
    private String horaMensaje;
    private String ultimoMensaje;
    private boolean leido;
    private boolean enviadoPorMi;
    private boolean leidoPorMi;    // Si yo ya leí el mensaje recibido


    // Constructor
    public Chat(String nombreHotel, String horaMensaje, String ultimoMensaje, boolean leido, boolean enviadoPorMi, boolean leidoPorMi) {
        this.nombreHotel = nombreHotel;
        this.horaMensaje = horaMensaje;
        this.ultimoMensaje = ultimoMensaje;
        this.leido = leido;
        this.enviadoPorMi = enviadoPorMi;
        this.leidoPorMi = leidoPorMi;
    }

    // Getters
    public String getnombreHotel() {
        return nombreHotel;
    }

    public String getHoraMensaje() {
        return horaMensaje;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public boolean isLeido() {
        return leido;
    }
    public boolean isEnviadoPorMi() {
        return enviadoPorMi;
    }
    public boolean isLeidoPorMi() {
        return leidoPorMi;
    }

}

