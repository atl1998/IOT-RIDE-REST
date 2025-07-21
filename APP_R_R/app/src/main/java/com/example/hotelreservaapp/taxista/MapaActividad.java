package com.example.hotelreservaapp.taxista;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.hotelreservaapp.R;
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

public class MapaActividad extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView ivFotoCliente;
    private TextView tvNombreCliente, tvTelefonoCliente;
    private MaterialButton btnVolver, btnConfirmarRecojo;
    private GoogleMap mMap;
    private Marker markerTaxi, markerCliente;
    private Polyline rutaPolyline;
    private FusedLocationProviderClient fusedLocationClient;

    // Datos del cliente
    private String nombreCliente, telefonoCliente, fotoUrlCliente;
    private double clienteLat, clienteLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_mapa);

        // Bind vistas
        ivFotoCliente      = findViewById(R.id.ivFotoCliente);
        tvNombreCliente    = findViewById(R.id.txtNombreCliente);
        tvTelefonoCliente  = findViewById(R.id.telefonoviajero);
        btnVolver          = findViewById(R.id.btnVolver);
        btnConfirmarRecojo = findViewById(R.id.btnConfirmarRecojo);

        // Leer extras (exactos los mismos keys que en el adapter)
        Intent intent = getIntent();
        nombreCliente   = intent.getStringExtra("nombreCliente");
        telefonoCliente = intent.getStringExtra("telefonoCliente");
        fotoUrlCliente  = intent.getStringExtra("fotoCliente");
        clienteLat      = intent.getDoubleExtra("latOrigen", 0);
        clienteLng      = intent.getDoubleExtra("lngOrigen", 0);

        // Mostrar datos
        tvNombreCliente.setText("Nombre: " + nombreCliente);
        tvTelefonoCliente.setText("Contacto: " + telefonoCliente);

        if (fotoUrlCliente != null && !fotoUrlCliente.isEmpty()) {
            Glide.with(this)
                    .load(fotoUrlCliente)
                    .placeholder(R.drawable.default_profile)
                    .into(ivFotoCliente);
        } else {
            ivFotoCliente.setImageResource(R.drawable.default_profile);
        }

        // Mapa y ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFrag = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFrag != null) mapFrag.getMapAsync(this);

        // Botón “volver”
        btnVolver.setOnClickListener(v -> finish());

        // Confirmar recogida → ViajeEnCursoActivity
        btnConfirmarRecojo.setOnClickListener(v -> {
            Intent i = new Intent(this, ViajeEnCursoActivity.class);
            i.putExtra("nombreCliente",   nombreCliente);
            i.putExtra("telefonoCliente", telefonoCliente);
            i.putExtra("fotoCliente",     fotoUrlCliente);
            i.putExtra("latOrigen", clienteLat);
            i.putExtra("lngOrigen", clienteLng);
            startActivity(i);
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // aplicar estilo
        try {
            boolean ok = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa_driver_style)
            );
            if (!ok) Log.e("MAP_STYLE", "Error aplicando estilo");
        } catch (Resources.NotFoundException e) {
            Log.e("MAP_STYLE", e.getMessage());
        }

        // permiso ubicación
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                    1001
            );
            return;
        }

        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation().addOnSuccessListener(loc -> {
            if (loc == null) {
                Toast.makeText(this, "Ubicación no disponible", Toast.LENGTH_SHORT).show();
                return;
            }
            LatLng taxiPos = new LatLng(loc.getLatitude(), loc.getLongitude());
            LatLng cliPos  = new LatLng(clienteLat, clienteLng);
            drawRoute(taxiPos, cliPos);
        });
    }

    private void drawRoute(LatLng origen, LatLng destino) {
        // limpiar
        if (markerTaxi   != null) markerTaxi.remove();
        if (markerCliente!= null) markerCliente.remove();
        if (rutaPolyline != null) rutaPolyline.remove();

        // iconos
        Bitmap bmpTaxi   = BitmapFactory.decodeResource(getResources(), R.drawable.icono_auto);
        Bitmap smallTaxi = Bitmap.createScaledBitmap(bmpTaxi, 80, 80, false);
        Bitmap bmpCli    = BitmapFactory.decodeResource(getResources(), R.drawable.icono_cliente);
        Bitmap smallCli  = Bitmap.createScaledBitmap(bmpCli, 80, 80, false);

        markerTaxi = mMap.addMarker(new MarkerOptions()
                .position(origen)
                .title("Tu ubicación")
                .icon(BitmapDescriptorFactory.fromBitmap(smallTaxi)));

        markerCliente = mMap.addMarker(new MarkerOptions()
                .position(destino)
                .title("Cliente")
                .icon(BitmapDescriptorFactory.fromBitmap(smallCli)));

        // Directions API
        String url = "https://maps.googleapis.com/maps/api/directions/json"
                + "?origin="    + origen.latitude  + "," + origen.longitude
                + "&destination="+ destino.latitude + "," + destino.longitude
                + "&mode=driving"
                + "&key=" + getString(R.string.google_maps_key);

        new OkHttpClient().newCall(new Request.Builder().url(url).build())
                .enqueue(new Callback() {
                    @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("DIRECTIONS", "HTTP fallo: " + e.getMessage());
                    }
                    @Override public void onResponse(@NonNull Call call, @NonNull Response resp)
                            throws IOException {
                        try {
                            String body = resp.body().string();
                            JSONArray routes = new JSONObject(body).getJSONArray("routes");
                            if (routes.length()==0) return;
                            String points = routes.getJSONObject(0)
                                    .getJSONObject("overview_polyline")
                                    .getString("points");
                            List<LatLng> decoded = PolyUtil.decode(points);

                            runOnUiThread(() -> {
                                rutaPolyline = mMap.addPolyline(new PolylineOptions()
                                        .addAll(decoded)
                                        .width(10)
                                        .color(ContextCompat.getColor(MapaActividad.this, R.color.dorado)));

                                LatLngBounds bounds = new LatLngBounds.Builder()
                                        .include(origen)
                                        .include(destino)
                                        .build();
                                mMap.animateCamera(
                                        CameraUpdateFactory.newLatLngBounds(bounds, 120)
                                );
                            });
                        } catch (JSONException e) {
                            Log.e("DIRECTIONS", "JSON error: " + e.getMessage());
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] perms,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, perms, grantResults);
        if (requestCode==1001
                && grantResults.length>0
                && grantResults[0]==PackageManager.PERMISSION_GRANTED
                && mMap!=null) {
            onMapReady(mMap);
        }
    }
}
