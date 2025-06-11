package com.example.hotelreservaapp.cliente;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistorialItem implements Serializable {
    private String idReserva;
    private String estado;
    private String nombreHotel;
    private String fechas;
    private String rangoFechaBonito;
    private String ubicacion;
    private String tipoHab;
    private Timestamp fechaIni;
    private Timestamp fechaFin;
    private int imagenResId;
    // atributos que ya tienes...
    private boolean checkoutEnabled = true; // por defecto activo
    private boolean cuentaConTaxi = false;
    private int personas;
    private Double valoracion;
    private String contacto;
    private String taxistaEnabled = "No Disponible";
    private String checkInHora;
    private String checkOutHora;

    public HistorialItem(String idReserva, String estado, String nombreHotel,
                         String ubicacion, int imagenResId, String taxistaEnabled, boolean checkoutEnabled,
                         boolean cuentaConTaxi, Timestamp fechaIni, Timestamp fechaFin) {
        this.idReserva = idReserva;
        this.nombreHotel = nombreHotel;
        this.ubicacion = ubicacion;
        this.imagenResId = imagenResId;
        this.taxistaEnabled = taxistaEnabled;
        this.checkoutEnabled = checkoutEnabled;
        this.cuentaConTaxi = cuentaConTaxi;
        this.estado = estado;
        this.fechaFin=fechaFin;
        this.fechaIni=fechaIni;
        this.fechas=getRangoFechasBonito();

    }

    public void setFechaIni(Timestamp fechaIni) {
        this.fechaIni = fechaIni;
    }

    public void setFechaFin(Timestamp fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getCheckOutHora() {
        return checkOutHora;
    }

    public void setCheckOutHora(String checkOutHora) {
        this.checkOutHora = checkOutHora;
    }

    public String getCheckInHora() {
        return checkInHora;
    }

    public void setCheckInHora(String checkInHora) {
        this.checkInHora = checkInHora;
    }

    public String getTipoHab() {
        return tipoHab;
    }

    public Timestamp getFechaIni() {
        return fechaIni;
    }

    public Timestamp getFechaFin() {
        return fechaFin;
    }

    public boolean isCuentaConTaxi() {
        return cuentaConTaxi;
    }

    public int getPersonas() {
        return personas;
    }

    public Double getValoracion() {
        return valoracion;
    }

    public String getContacto() {
        return contacto;
    }

    public String getTaxistaEnabled() {
        return taxistaEnabled;
    }

    public void setTipoHab(String tipoHab) {
        this.tipoHab = tipoHab;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public void setValoracion(Double valoracion) {
        this.valoracion = valoracion;
    }

    public void setPersonas(int personas) {
        this.personas = personas;
    }

    public String getEstado() {
        return estado;
    }

    public String getIdReserva() {
        return idReserva;
    }
    public String getRangoFechasBonito() {
        Date fechaInicio = fechaIni.toDate();
        Date fechaFinal = fechaFin.toDate();

        SimpleDateFormat sdfDia = new SimpleDateFormat("d", new Locale("es", "ES"));
        SimpleDateFormat sdfMes = new SimpleDateFormat("MMM", new Locale("es", "ES"));  // Mes abreviado con punto
        SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy", new Locale("es", "ES"));

        String diaInicio = sdfDia.format(fechaInicio);
        String mesInicio = sdfMes.format(fechaInicio);
        String diaFin = sdfDia.format(fechaFinal);
        String mesFin = sdfMes.format(fechaFinal);
        String anio = sdfAnio.format(fechaFinal); // Usa fechaFin para el año

        return diaInicio + " " + capitalize(mesInicio) + ". - " + diaFin + " " + capitalize(mesFin) + ". " + anio;
    }

    public String getFechaIniBonito() {
        Date fechaInicio = fechaIni.toDate();

        SimpleDateFormat sdfDia = new SimpleDateFormat("d", new Locale("es", "ES"));
        SimpleDateFormat sdfMes = new SimpleDateFormat("MMMM", new Locale("es", "ES"));

        String diaInicio = sdfDia.format(fechaInicio);
        String mesInicio = sdfMes.format(fechaInicio);
        String mesFormateado;

        if (mesInicio.length() > 5) {
            mesFormateado = capitalize(mesInicio.substring(0, 5)) + "."; // "Septie."
        } else {
            mesFormateado = capitalize(mesInicio); // "Marzo"
        }

        return diaInicio + " de " + mesFormateado;
    }
    public String getFechaFinBonito() {
        Date fechaFinal = fechaFin.toDate();

        SimpleDateFormat sdfDia = new SimpleDateFormat("d", new Locale("es", "ES"));
        SimpleDateFormat sdfMes = new SimpleDateFormat("MMMM", new Locale("es", "ES"));

        String diaFinal = sdfDia.format(fechaFinal);
        String mesFinal = sdfMes.format(fechaFinal);
        String mesFormateado;

        if (mesFinal.length() > 5) {
            mesFormateado = capitalize(mesFinal.substring(0, 5)) + "."; // "Septie."
        } else {
            mesFormateado = capitalize(mesFinal); // "Marzo"
        }

        return diaFinal + " de " + mesFormateado;
    }

    // Para capitalizar la primera letra del mes
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
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

