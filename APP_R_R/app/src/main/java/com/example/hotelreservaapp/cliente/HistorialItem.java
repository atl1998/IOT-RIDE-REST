package com.example.hotelreservaapp.cliente;

import com.google.firebase.Timestamp;

public class HistorialItem {
    private String idReserva;
    private String estado;
    private String nombreHotel;
    private String fechas;
    private String ubicacion;
    private Timestamp fechaIni;
    private Timestamp fechaFin;
    private int imagenResId;
    // atributos que ya tienes...
    private boolean checkoutEnabled = true; // por defecto activo
    private boolean cuentaConTaxi = false;
    private String taxistaEnabled = "No Disponible";

    public HistorialItem(String idReserva, String estado, String nombreHotel, String fechas,
                         String ubicacion, int imagenResId, String taxistaEnabled, boolean checkoutEnabled,
                         boolean cuentaConTaxi, Timestamp fechaIni, Timestamp fechaFin) {
        this.idReserva = idReserva;
        this.nombreHotel = nombreHotel;
        this.fechas = fechas;
        this.ubicacion = ubicacion;
        this.imagenResId = imagenResId;
        this.taxistaEnabled = taxistaEnabled;
        this.checkoutEnabled = checkoutEnabled;
        this.cuentaConTaxi = cuentaConTaxi;
        this.estado = estado;
        this.fechaFin=fechaFin;
        this.fechaIni=fechaIni;
    }

    public String getEstado() {
        return estado;
    }

    public String getIdReserva() {
        return idReserva;
    }

    public boolean isCheckoutEnabled() {
        return checkoutEnabled;
    }
    public void setCheckoutEnabled(boolean enabled) {
        this.checkoutEnabled = enabled;
    }
    public boolean isTaxiEnabled() {
        // Puede ser No solicitado, Terminado, No disponible tmb
        return "Solicitado".equalsIgnoreCase(taxistaEnabled);
    }

    public String getNombreHotel() {
        return nombreHotel;
    }

    public String getFechas() {
        return fechas;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public int getImagenResId() {
        return imagenResId;
    }
}

