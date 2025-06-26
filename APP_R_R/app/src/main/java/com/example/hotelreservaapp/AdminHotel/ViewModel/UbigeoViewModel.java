package com.example.hotelreservaapp.AdminHotel.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.hotelreservaapp.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class UbigeoViewModel extends AndroidViewModel {

    private final Map<String, Map<String, Map<String, String>>> ubigeo;

    private final MutableLiveData<List<Par>> provincias = new MutableLiveData<>();
    private final MutableLiveData<List<Par>> distritos  = new MutableLiveData<>();

    /* getters */
    public LiveData<List<Par>> getProvincias() { return provincias; }
    public LiveData<List<Par>> getDistritos()  { return distritos; }

    public UbigeoViewModel(@NonNull Application app) {
        super(app);
        String json = new Scanner(app.getResources()
                .openRawResource(R.raw.ubigeo), "UTF-8").useDelimiter("\\A").next();
        Type t = new TypeToken<
                        Map<String, Map<String, Map<String,String>>>>(){}.getType();
        ubigeo = new Gson().fromJson(json, t);
    }

    /* ---- Paso 1: el usuario elige departamento ---- */
    public void setDepartamento(String depCode) {
        Map<String, Map<String, String>> provs = ubigeo.get(depCode);
        if (provs == null) { provincias.setValue(List.of()); return; }

        List<Par> lista = provs.keySet().stream()
                .map(code -> new Par(code, nombreProvincia(provs.get(code))))
                .sorted(Comparator.comparing(Par::nombre))
                .collect(Collectors.toList());
        provincias.setValue(lista);

        distritos.setValue(List.of());          // limpia distritos
    }

    /* ---- Paso 2: el usuario elige provincia ---- */
    public void setProvincia(String depCode, String provCode) {
        Map<String,String> distMap = ubigeo
                .get(depCode)
                .get(provCode);

        List<Par> lista = distMap.entrySet().stream()
                .map(e -> new Par(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(Par::nombre))
                .collect(Collectors.toList());
        distritos.setValue(lista);
    }

    /* helpers */
    private String nombreProvincia(Map<String,String> distMap) {
        return distMap.values().iterator().next(); // primer distrito
    }

    /** Código + nombre, toString() devuelve el nombre para el Spinner */
    public static class Par {
        private final String codigo, nombre;
        public Par(String c, String n) { codigo=c; nombre=n; }
        public String codigo() { return codigo; }
        public String nombre() { return nombre; }
        @Override public String toString() { return nombre; }
    }
}
