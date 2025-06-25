package com.example.hotelreservaapp.cliente;

public class CiudadResultado {
    private String city;
    private String name;
    private String country;
    private String region;

    public CiudadResultado(String city, String name, String country, String region) {
        this.city = city;
        this.name = name;
        this.country = country;
        this.region = region;
    }

    public String getCity() { return city; }
    public String getName() { return name; }
    public String getCountry() { return country; }
    public String getRegion() { return region; }

    @Override
    public String toString() {
        return name + ", " + region + ", " + country;
    }
}
