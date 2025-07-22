package com.example.hotelreservaapp.taxista;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.taxista.adapter.TarjetaTaxistaAdapter;
import com.example.hotelreservaapp.taxista.model.TarjetaModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViajeEnCursoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    TextView nombreCliente, telefonoviajero;
    MaterialButton btnVolver, btnFinalizarViaje, btnCancelarViaje;

    private String nombre, telefono;
    private double latOrigen, lngOrigen, latDestino, lngDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_viaje_en_curso);

        nombreCliente = findViewById(R.id.txtNombreCliente);
        telefonoviajero = findViewById(R.id.telefonoviajero);
        btnVolver = findViewById(R.id.btnVolver);
        btnFinalizarViaje = findViewById(R.id.btnFinalizarViaje);
        btnCancelarViaje = findViewById(R.id.btnCancelarViaje);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        nombre     = getIntent().getStringExtra("nombreCliente");
        telefono   = getIntent().getStringExtra("telefonoCliente");
        latOrigen  = getIntent().getDoubleExtra("latOrigen",  0.0);
        lngOrigen  = getIntent().getDoubleExtra("lngOrigen",  0.0);
        latDestino = getIntent().getDoubleExtra("latDestino", 0.0);
        lngDestino = getIntent().getDoubleExtra("lngDestino", 0.0);

        if (nombre != null) nombreCliente.setText("Nombre: " + nombre);
        if (telefono != null) telefonoviajero.setText("Contacto: " + telefono);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapaFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnVolver.setOnClickListener(v -> {
            startActivity(new Intent(this, TaxistaMain.class));
            finish();
        });

        btnFinalizarViaje.setOnClickListener(v -> {
            Intent intent = new Intent(this, QrLecturaActivity.class);
            intent.putExtra("nombreCliente",   nombre);
            intent.putExtra("telefonoCliente", telefono);
            intent.putExtra("latOrigen",       latOrigen);
            intent.putExtra("lngOrigen",       lngOrigen);
            intent.putExtra("latDestino",      latDestino);
            intent.putExtra("lngDestino",      lngDestino);
            startActivity(intent);
        });



        btnCancelarViaje.setOnClickListener(v -> {
            boolean actualizado = false;
            for (TarjetaModel t : TarjetaTaxistaAdapter.listaCompartida) {
                if (t.getNombreCliente().equals(nombre) && t.getTelefonoCliente().equals(telefono)) {
                    t.setEstado("Cancelado");
                    actualizado = true;
                    break;
                }
            }
            if (actualizado) Toast.makeText(this, "Viaje cancelado", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, TaxistaMain.class));
            finish();
        });
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa_driver_style)
            );
            if (!success) {
                Log.e("MAPA_ESTILO", "No se pudo aplicar estilo al mapa.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MAPA_ESTILO", "Estilo no encontrado. " + e.getMessage());
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            LatLng origen = new LatLng(latOrigen, lngOrigen);
            LatLng destino = new LatLng(latDestino, lngDestino);

            if (location != null) {
                LatLng actual = new LatLng(location.getLatitude(), location.getLongitude());
                Bitmap taxiIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icono_auto);
                mMap.addMarker(new MarkerOptions()
                        .position(actual)
                        .title("Tu ubicación actual")
                        .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(taxiIcon, 80, 80, false))));
            }

            Bitmap clienteIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icono_ubicacion_cliente);
            Bitmap destinoIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icono_bandera_llegada);

            mMap.addMarker(new MarkerOptions()
                    .position(origen)
                    .title("Cliente")
                    .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(clienteIcon, 80, 80, false))));

            mMap.addMarker(new MarkerOptions()
                    .position(destino)
                    .title("Destino")
                    .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(destinoIcon, 80, 80, false))));

            // Llama Directions API para trazar ruta entre origen y destino
            String url = "https://maps.googleapis.com/maps/api/directions/json"
                    + "?origin=" + origen.latitude + "," + origen.longitude
                    + "&destination=" + destino.latitude + "," + destino.longitude
                    + "&mode=driving"
                    + "&key=" + getString(R.string.google_maps_key);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("RUTA_ERROR", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        String json = response.body().string();
                        JSONObject obj = new JSONObject(json);
                        JSONArray routes = obj.getJSONArray("routes");

                        if (routes.length() > 0) {
                            String encoded = routes.getJSONObject(0)
                                    .getJSONObject("overview_polyline")
                                    .getString("points");
                            List<LatLng> points = PolyUtil.decode(encoded);

                            runOnUiThread(() -> {
                                mMap.addPolyline(new PolylineOptions()
                                        .addAll(points)
                                        .width(10)
                                        .color(ContextCompat.getColor(getApplicationContext(), R.color.dorado)));

                                LatLngBounds bounds = new LatLngBounds.Builder()
                                        .include(origen)
                                        .include(destino)
                                        .build();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                            });
                        }
                    } catch (JSONException e) {
                        Log.e("RUTA_JSON", e.getMessage());
                    }
                }
            });
        });

    }
}
