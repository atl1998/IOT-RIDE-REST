package com.example.hotelreservaapp.taxista;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.maps.android.PolyUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapaActividad extends AppCompatActivity implements OnMapReadyCallback {

    // Estados
    private static final String EST_SOLICITADO = "Solicitado";
    private static final String EST_ACEPTADO   = "Aceptado";
    private static final String EST_EN_CURSO   = "EnCurso";
    private static final String EST_CANCELADO  = "Cancelado";
    private static final String EST_FINALIZADO = "Finalizado";

    // Views
    private ImageView ivFotoCliente;
    private TextView tvNombreCliente, tvTelefonoCliente;
    private com.google.android.material.button.MaterialButton btnConfirmarRecojo;

    // Mapa
    private GoogleMap mMap;
    private Marker markerTaxi, markerCliente;
    private Polyline rutaPolyline;

    // Ubicaciones
    private double clienteLat, clienteLng;
    private double destinoLat, destinoLng;

    // Datos extras
    private String serviceId, estadoActual;
    private String nombreCliente, telefonoCliente, fotoUrlCliente;

    // Location & DB
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference rideRef;
    private ValueEventListener taxiLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_mapa);

        // 1) Bind vistas
        ivFotoCliente      = findViewById(R.id.ivFotoCliente);
        tvNombreCliente    = findViewById(R.id.txtNombreCliente);
        tvTelefonoCliente  = findViewById(R.id.telefonoviajero);
        btnConfirmarRecojo = findViewById(R.id.btnConfirmarRecojo);

        // 2) Leer extras
        Intent intent = getIntent();
        serviceId       = intent.getStringExtra("serviceId");
        estadoActual    = intent.getStringExtra("estado");
        nombreCliente   = intent.getStringExtra("nombreCliente");
        telefonoCliente = intent.getStringExtra("telefonoCliente");
        fotoUrlCliente  = intent.getStringExtra("fotoCliente");
        clienteLat      = intent.getDoubleExtra("latOrigen", 0);
        clienteLng      = intent.getDoubleExtra("lngOrigen", 0);
        destinoLat      = intent.getDoubleExtra("latDestino", 0);
        destinoLng      = intent.getDoubleExtra("lngDestino", 0);

        // 3) Mostrar datos
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

        // 4) Inicializar Location & DB
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        rideRef = FirebaseDatabase.getInstance()
                .getReference("servicios_taxi")
                .child(serviceId);

        // 5) Configurar botón dinámico
        setupButtonsForState(estadoActual);

        // 6) Inicializar mapa
        SupportMapFragment mapFrag = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFrag != null) mapFrag.getMapAsync(this);
    }

    private void setupButtonsForState(String estado) {
        btnConfirmarRecojo.setVisibility(android.view.View.GONE);

        switch (estado) {
            case EST_SOLICITADO:
                btnConfirmarRecojo.setText("Aceptar Solicitud");
                btnConfirmarRecojo.setVisibility(android.view.View.VISIBLE);
                btnConfirmarRecojo.setOnClickListener(v -> transitionState(EST_ACEPTADO));
                break;

            case EST_ACEPTADO:
                btnConfirmarRecojo.setText("Confirmar Recojo");
                btnConfirmarRecojo.setVisibility(android.view.View.VISIBLE);
                btnConfirmarRecojo.setOnClickListener(v -> transitionState(EST_EN_CURSO));
                break;

            case EST_EN_CURSO:
                btnConfirmarRecojo.setText("Finalizar Viaje");
                btnConfirmarRecojo.setVisibility(android.view.View.VISIBLE);
                btnConfirmarRecojo.setOnClickListener(v -> transitionState(EST_FINALIZADO));
                break;

            default: // Cancelado o Finalizado
                btnConfirmarRecojo.setVisibility(android.view.View.GONE);
                break;
        }
    }

    private void transitionState(String nuevoEstado) {
        rideRef.child("estado").setValue(nuevoEstado)
                .addOnSuccessListener(_unused -> {
                    estadoActual = nuevoEstado;
                    setupButtonsForState(nuevoEstado);
                    Toast.makeText(this, "Estado → " + nuevoEstado, Toast.LENGTH_SHORT).show();

                    // Forzar primer redraw de ruta con el nuevo target
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(loc -> {
                                if (loc == null) return;
                                LatLng taxiPos = new LatLng(loc.getLatitude(), loc.getLongitude());
                                LatLng target = estadoActual.equals(EST_EN_CURSO)
                                        ? new LatLng(destinoLat, destinoLng)
                                        : new LatLng(clienteLat, clienteLng);
                                drawRoute(taxiPos, target);
                            });
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
            if (!ok) Log.e("MAP_STYLE","Error aplicando estilo");
        } catch (Resources.NotFoundException e) {
            Log.e("MAP_STYLE", e.getMessage());
        }

        // permisos
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

        // 1) Primera ruta estática
        fusedLocationClient.getLastLocation().addOnSuccessListener(loc -> {
            if (loc == null) {
                Toast.makeText(this, "Ubicación no disponible", Toast.LENGTH_SHORT).show();
                return;
            }
            LatLng taxiPos = new LatLng(loc.getLatitude(), loc.getLongitude());
            LatLng target = estadoActual.equals(EST_EN_CURSO)
                    ? new LatLng(destinoLat, destinoLng)
                    : new LatLng(clienteLat, clienteLng);
            drawRoute(taxiPos, target);
        });

        // 2) Publicar ubicación del taxi cada 5s
        LocationRequest req = LocationRequest.create()
                .setInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(req, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult result) {
                if (result == null) return;
                double lat = result.getLastLocation().getLatitude();
                double lng = result.getLastLocation().getLongitude();
                // publicar en Realtime DB
                Map<String,Object> m = new HashMap<>();
                m.put("lat", lat);
                m.put("lng", lng);
                rideRef.child("taxiLocation").setValue(m);
            }
        }, Looper.getMainLooper());

        // 3) Escuchar cambios en taxiLocation y redibujar
        taxiLocationListener = rideRef.child("taxiLocation")
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snap) {
                        if (!snap.exists()) return;
                        double lat = snap.child("lat").getValue(Double.class);
                        double lng = snap.child("lng").getValue(Double.class);
                        LatLng taxiPos = new LatLng(lat, lng);
                        LatLng target = estadoActual.equals(EST_EN_CURSO)
                                ? new LatLng(destinoLat, destinoLng)
                                : new LatLng(clienteLat, clienteLng);
                        drawRoute(taxiPos, target);
                    }
                    @Override public void onCancelled(DatabaseError e) { }
                });
    }

    private void drawRoute(LatLng origen, LatLng destino) {
        // limpiar anteriores
        if (markerTaxi    != null) markerTaxi.remove();
        if (markerCliente != null) markerCliente.remove();
        if (rutaPolyline  != null) rutaPolyline.remove();

        // iconos
        Bitmap taxiBmp = BitmapFactory.decodeResource(getResources(), R.drawable.icono_auto);
        Bitmap smallTaxi = Bitmap.createScaledBitmap(taxiBmp, 80,80,false);
        Bitmap cliBmp  = BitmapFactory.decodeResource(getResources(), R.drawable.icono_cliente);
        Bitmap smallCli = Bitmap.createScaledBitmap(cliBmp,80,80,false);

        markerTaxi = mMap.addMarker(new MarkerOptions()
                .position(origen)
                .title("Taxi")
                .icon(BitmapDescriptorFactory.fromBitmap(smallTaxi)));

        String title = estadoActual.equals(EST_EN_CURSO) ? "Destino" : "Cliente";
        markerCliente = mMap.addMarker(new MarkerOptions()
                .position(destino)
                .title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(smallCli)));

        // Directions API
        String url = "https://maps.googleapis.com/maps/api/directions/json"
                + "?origin=" + origen.latitude + "," + origen.longitude
                + "&destination=" + destino.latitude + "," + destino.longitude
                + "&mode=driving"
                + "&key=" + getString(R.string.google_maps_key);

        new OkHttpClient().newCall(new Request.Builder().url(url).build())
                .enqueue(new Callback() {
                    @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("DIRECTIONS","HTTP fallo: "+e.getMessage());
                    }
                    @Override public void onResponse(@NonNull Call call, @NonNull Response resp)
                            throws IOException {
                        try {
                            String body = resp.body().string();
                            List<LatLng> pts = PolyUtil.decode(
                                    new org.json.JSONObject(body)
                                            .getJSONArray("routes")
                                            .getJSONObject(0)
                                            .getJSONObject("overview_polyline")
                                            .getString("points")
                            );
                            runOnUiThread(() -> {
                                rutaPolyline = mMap.addPolyline(new PolylineOptions()
                                        .addAll(pts)
                                        .width(10)
                                        .color(ContextCompat.getColor(MapaActividad.this, R.color.dorado)));
                                LatLngBounds bounds = new LatLngBounds.Builder()
                                        .include(origen)
                                        .include(destino)
                                        .build();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
                            });
                        } catch(Exception ex) {
                            Log.e("DIRECTIONS","JSON error: "+ex.getMessage());
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (taxiLocationListener != null) {
            rideRef.child("taxiLocation")
                    .removeEventListener(taxiLocationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] perms,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, perms, grantResults);
        if (requestCode == 1001
                && grantResults.length>0
                && grantResults[0]==PackageManager.PERMISSION_GRANTED
                && mMap!=null) {
            onMapReady(mMap);
        }
    }
}
