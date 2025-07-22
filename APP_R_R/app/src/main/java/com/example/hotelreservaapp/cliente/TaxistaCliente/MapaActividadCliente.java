package com.example.hotelreservaapp.cliente.TaxistaCliente;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;

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

public class MapaActividadCliente extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker  mTaxiMarker;
    private Polyline mRoutePolyline;
    private FusedLocationProviderClient fusedLocationClient;

    private ImageView ivFotoTaxista;
    private TextView  txtNombreTaxista, txtTelefonoTaxista;
    private MaterialButton btnVolver, btnCancelar;

    private FirebaseFirestore db;
    private ListenerRegistration serviceListener;
    private String serviceId;

    private double latOrigen, lngOrigen, latDestino, lngDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_mapa_actividad);

        // Bind vistas de tarjeta inferior
        ivFotoTaxista      = findViewById(R.id.ivFotoTaxista);
        txtNombreTaxista   = findViewById(R.id.txtNombreTaxista);
        txtTelefonoTaxista = findViewById(R.id.telefonotaxista);
        btnCancelar        = findViewById(R.id.btnCancelar);
        btnVolver          = findViewById(R.id.btnVolver);

        // Leer extras del Intent
        serviceId           = getIntent().getStringExtra("serviceId");
        String nombreTaxi   = getIntent().getStringExtra("nombreTaxista");
        String telefonoTaxi = getIntent().getStringExtra("telefonoTaxista");
        String fotoTaxi     = getIntent().getStringExtra("fotoTaxista");
        latOrigen           = getIntent().getDoubleExtra("latOrigen",  0.0);
        lngOrigen           = getIntent().getDoubleExtra("lngOrigen",  0.0);
        latDestino          = getIntent().getDoubleExtra("latDestino", 0.0);
        lngDestino          = getIntent().getDoubleExtra("lngDestino", 0.0);

        // Mostrar datos del taxista en la tarjeta
        txtNombreTaxista.setText(nombreTaxi);
        txtTelefonoTaxista.setText(telefonoTaxi);
        Glide.with(this)
                .load(fotoTaxi)
                .placeholder(R.drawable.default_profile)
                .circleCrop()
                .into(ivFotoTaxista);

        // Cancelar servicio → actualiza estado y sale
        btnCancelar.setOnClickListener(v -> {
            db.collection("servicios_taxi")
                    .document(serviceId)
                    .update("estado", "Cancelado")
                    .addOnSuccessListener(u -> finish());
        });

        // Volver atrás
        btnVolver.setOnClickListener(v -> finish());

        // Inicializar Firestore y FusedLocation
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Preparar el mapa
        SupportMapFragment mapFrag = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFrag != null) {
            mapFrag.getMapAsync(this);
        }

        // Listener real-time de la ubicación del taxista y estado
        DocumentReference ref = db.collection("servicios_taxi")
                .document(serviceId);
        serviceListener = ref.addSnapshotListener((snap, e) -> {
            if (e != null || snap == null || !snap.exists()) return;

            String estado = snap.getString("estado");
            GeoPoint gp = snap.getGeoPoint("ubicacionTaxista");
            String ola = "ola";
            if (gp == null || mMap == null) return;

            LatLng taxiPos = new LatLng(gp.getLatitude(), gp.getLongitude());
            runOnUiThread(() -> {
                // Mover o crear marcador del taxi
                if (mTaxiMarker == null) {
                    Bitmap bmp = BitmapFactory.decodeResource(
                            getResources(), R.drawable.icono_auto);
                    mTaxiMarker = mMap.addMarker(new MarkerOptions()
                            .position(taxiPos)
                            .title("Taxista")
                            .icon(BitmapDescriptorFactory.fromBitmap(
                                    Bitmap.createScaledBitmap(bmp, 80, 80, false))));
                } else {
                    mTaxiMarker.setPosition(taxiPos);
                }

                // Si ya está en curso, trazamos la ruta completa cliente→destino
                if ("EnCurso".equalsIgnoreCase(estado) && mRoutePolyline == null) {
                    drawRoute(new LatLng(latOrigen, lngOrigen),
                            new LatLng(latDestino, lngDestino));
                }
            });
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Estilo de mapa
        try {
            boolean ok = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa_driver_style)
            );
            if (!ok) Log.e("MAP_STYLE", "Error applying style");
        } catch (Resources.NotFoundException ex) {
            Log.e("MAP_STYLE", ex.getMessage());
        }

        // Permiso de ubicación (opcional para cliente)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        }

        // Agregar marcador de origen (cliente)
        LatLng origin = new LatLng(latOrigen, lngOrigen);
        mMap.addMarker(new MarkerOptions()
                .position(origin)
                .title("Tú estás aquí"));

        // Ajustar cámara centrada en origen inicialmente
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 14f));
    }

    private void drawRoute(LatLng origen, LatLng destino) {
        String url = "https://maps.googleapis.com/maps/api/directions/json"
                + "?origin="     + origen.latitude  + "," + origen.longitude
                + "&destination="+ destino.latitude + "," + destino.longitude
                + "&mode=driving"
                + "&key="         + getString(R.string.google_maps_key);

        new OkHttpClient().newCall(new Request.Builder().url(url).build())
                .enqueue(new Callback() {
                    @Override public void onFailure(@NonNull Call call,
                                                    @NonNull IOException e) {
                        Log.e("DIRECTIONS", e.getMessage());
                    }
                    @Override public void onResponse(@NonNull Call call,
                                                     @NonNull Response resp)
                            throws IOException {
                        try {
                            String body = resp.body().string();
                            JSONArray routes = new JSONObject(body)
                                    .getJSONArray("routes");
                            if (routes.length() == 0) return;
                            String points = routes.getJSONObject(0)
                                    .getJSONObject("overview_polyline")
                                    .getString("points");
                            List<LatLng> decoded = PolyUtil.decode(points);

                            runOnUiThread(() -> {
                                mRoutePolyline = mMap.addPolyline(new PolylineOptions()
                                        .addAll(decoded)
                                        .width(10));
                                // Ajustar cámara para mostrar toda la ruta
                                LatLngBounds bounds = new LatLngBounds.Builder()
                                        .include(origen)
                                        .include(destino)
                                        .build();
                                mMap.animateCamera(
                                        CameraUpdateFactory.newLatLngBounds(bounds, 100)
                                );
                            });
                        } catch (JSONException ex) {
                            Log.e("DIRECTIONS", ex.getMessage());
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceListener != null) {
            serviceListener.remove();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] perms,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1001
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && mMap != null) {
            try {
                mMap.setMyLocationEnabled(true);
            } catch (SecurityException ignored) {}
        }
        super.onRequestPermissionsResult(requestCode, perms, grantResults);
    }
}
