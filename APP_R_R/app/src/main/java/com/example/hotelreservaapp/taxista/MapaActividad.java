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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.taxista.fragments.TaxiInicioFragment;
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

    private TextView nombreCliente, telefonoviajero;
    private MaterialButton btnVolver, btnConfirmarRecojo;
    private GoogleMap mMap;
    private Marker markerActual, markerCliente;
    private Polyline rutaPolyline;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private double clienteLat = -12.0219;
    private double clienteLng = -77.1126;

    private double destinoLat = -12.0087;     // <- ejemplo predeterminado
    private double destinoLng = -77.0574;

    private String nombre, telefono;
    private boolean redirigirAViajeEnCurso = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_mapa);

        nombreCliente = findViewById(R.id.txtNombreCliente);
        telefonoviajero = findViewById(R.id.telefonoviajero);
        btnVolver = findViewById(R.id.btnVolver);
        btnConfirmarRecojo = findViewById(R.id.btnConfirmarRecojo);

        Intent intent = getIntent();
        nombre = intent.getStringExtra("nombre");
        telefono = intent.getStringExtra("telefono");
        redirigirAViajeEnCurso = intent.getBooleanExtra("viajeEnCurso", false);
        clienteLat = intent.getDoubleExtra("latOrigen", -12.0219);
        clienteLng = intent.getDoubleExtra("lngOrigen", -77.1126);

        if (nombre != null) nombreCliente.setText("Nombre: " + nombre);
        if (telefono != null) telefonoviajero.setText("Contacto: " + telefono);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnVolver.setOnClickListener(v -> {
            startActivity(new Intent(this, TaxistaMain.class));
            finish();
        });


        btnConfirmarRecojo.setOnClickListener(v -> {
            Intent i = new Intent(this, ViajeEnCursoActivity.class);
            i.putExtra("nombre", nombre);
            i.putExtra("telefono", telefono);
            i.putExtra("latOrigen", clienteLat);
            i.putExtra("lngOrigen", clienteLng);
            i.putExtra("latDestino", destinoLat);
            i.putExtra("lngDestino", destinoLng);
            startActivity(i);
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa_driver_style)
            );
            if (!success) {
                Log.e("MAPA_ESTILO", "Error al aplicar estilo");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MAPA_ESTILO", "No se encontró el archivo de estilo. " + e.getMessage());
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }

        mMap.setMyLocationEnabled(true);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng ubicacionActual = new LatLng(location.getLatitude(), location.getLongitude());
                mostrarRuta(ubicacionActual, new LatLng(clienteLat, clienteLng));
            } else {
                Toast.makeText(this, "Ubicación actual no disponible", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void mostrarRuta(LatLng origen, LatLng destino) {
        if (mMap == null) return;

        if (markerActual != null) markerActual.remove();
        if (markerCliente != null) markerCliente.remove();
        if (rutaPolyline != null) rutaPolyline.remove();

        Bitmap iconTaxi = BitmapFactory.decodeResource(getResources(), R.drawable.icono_auto);
        Bitmap smallTaxi = Bitmap.createScaledBitmap(iconTaxi, 80, 80, false);

        Bitmap iconCliente = BitmapFactory.decodeResource(getResources(), R.drawable.icono_cliente);
        Bitmap smallCliente = Bitmap.createScaledBitmap(iconCliente, 80, 80, false);

        markerActual = mMap.addMarker(new MarkerOptions()
                .position(origen)
                .title("Tu ubicación")
                .icon(BitmapDescriptorFactory.fromBitmap(smallTaxi)));

        markerCliente = mMap.addMarker(new MarkerOptions()
                .position(destino)
                .title("Cliente")
                .icon(BitmapDescriptorFactory.fromBitmap(smallCliente)));

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
                Log.e("RUTA_ERROR", "Fallo HTTP: " + e.getMessage());
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
                            int colorRuta = ContextCompat.getColor(getApplicationContext(), R.color.dorado);

                            rutaPolyline = mMap.addPolyline(new PolylineOptions()
                                    .addAll(points)
                                    .width(10)
                                    .color(colorRuta));

                            LatLngBounds bounds = new LatLngBounds.Builder()
                                    .include(origen)
                                    .include(destino)
                                    .build();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
                        });

                    }
                } catch (JSONException e) {
                    Log.e("RUTA_JSON", "Error al parsear JSON: " + e.getMessage());
                }
            }
        });
    }
}
