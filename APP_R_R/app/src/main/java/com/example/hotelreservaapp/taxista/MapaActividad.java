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
import android.view.View;
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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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

    private String estado;

    // Views
    private ImageView ivFotoCliente;
    private TextView tvNombreCliente, tvTelefonoCliente;
    private com.google.android.material.button.MaterialButton btnConfirmarRecojo;

    // Datos taxista sacados de Auth+Perfil
    private String taxistaUid;
    private String taxistaNombre, taxistaTelefono, taxistaPlaca;

    // Mapa
    private GoogleMap mMap;
    private Marker markerTaxi, markerCliente;
    private Polyline rutaPolyline;
    private TextView tvHeader;


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
    private MaterialButton btnAccion, btnSecundario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxista_activity_mapa);

        tvHeader = findViewById(R.id.actividad3);


        // 1) Bind vistas
        ivFotoCliente      = findViewById(R.id.ivFotoCliente);
        tvNombreCliente    = findViewById(R.id.txtNombreCliente);
        tvTelefonoCliente  = findViewById(R.id.telefonoviajero);

        // Bind de vistas
        btnAccion     = findViewById(R.id.btnAccion);
        btnSecundario = findViewById(R.id.btnSecundario);

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

        // 0) Obtener UID de taxista y cargar su perfil
        taxistaUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("taxistas")
                .document(taxistaUid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        taxistaNombre   = doc.getString("nombre");
                        taxistaTelefono = doc.getString("telefono");
                        taxistaPlaca    = doc.getString("placa");
                    }
                });

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

        // inicializa mapa + RealtimeDB
        SupportMapFragment mf = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mf != null) mf.getMapAsync(this);
        rideRef = FirebaseDatabase.getInstance()
                .getReference("servicios_taxi")
                .child(serviceId);

        MaterialButton btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> {
            // Devolvemos RESULT_OK para que el fragmento sepa que debe recargar
            setResult(RESULT_OK);
            finish();
        });

    }


    private void setupButtonsForState(String estado) {
        btnAccion.setVisibility(View.GONE);
        btnSecundario.setVisibility(View.GONE);

        switch (estado) {
            case EST_SOLICITADO:
                tvHeader.setText("Solicitud de viaje");
                btnAccion.setText("Aceptar Solicitud");
                btnAccion.setVisibility(View.VISIBLE);
                btnAccion.setOnClickListener(v -> {
<<<<<<< HEAD
                    // Primero comprobamos que no haya otros viajes activos...
=======
                    //Aceptado (Taxista asignado)
>>>>>>> 836103e0219d9e574eb694df064185a976c99525
                    FirebaseFirestore.getInstance()
                            .collection("servicios_taxi")
                            .whereIn("estado", List.of(EST_ACEPTADO, EST_EN_CURSO))
                            .get()
                            .addOnSuccessListener(q -> {
                                if (q.isEmpty()) {
                                    // 1) Transicionamos
                                    transitionState(EST_ACEPTADO);
                                    // Aquí escribimos en Firestore el perfil del taxista:
                                    FirebaseFirestore.getInstance()
                                            .collection("servicios_taxi")
                                            .document(serviceId)
                                            .update(Map.of(
                                                    "taxistaNombre",   taxistaNombre,
                                                    "taxistaTelefono", taxistaTelefono,
                                                    "taxistaPlaca",    taxistaPlaca
                                            ));

                                    // Publicar ubicación inicial en Realtime DB
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
                                    fusedLocationClient.getLastLocation().addOnSuccessListener(loc -> {
                                        if (loc != null) {
                                            rideRef.child("taxiLocation")
                                                    .setValue(Map.of("lat", loc.getLatitude(), "lng", loc.getLongitude()));
                                        }
                                    });
                                } else {
                                    Toast.makeText(this, "Ya tienes un viaje activo", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
                break;
            case EST_ACEPTADO:
                tvHeader.setText("Recoge al Cliente");
                btnAccion.setText("Confirmar Recojo");
                btnSecundario.setText("Cancelar Solicitud");
                btnAccion.setVisibility(View.VISIBLE);
                btnSecundario.setVisibility(View.VISIBLE);
                //Notificacion En curso (Iniciado tipo 08)
                btnAccion.setOnClickListener(v -> transitionState(EST_EN_CURSO));
                //Notificacion Cancelado
                btnSecundario.setOnClickListener(v -> transitionState(EST_CANCELADO));
                break;
            case EST_EN_CURSO:
                tvHeader.setText("En ruta al Destino");
                btnAccion.setText("Finalizar Viaje");
                btnSecundario.setText("Cancelar Viaje");
                btnAccion.setVisibility(View.VISIBLE);
                btnSecundario.setVisibility(View.VISIBLE);
                btnAccion.setOnClickListener(v -> {
                    // en lugar de transitionState:
                    // notificacion FINALIZADOOO tipo 09
                    Intent qr = new Intent(this, QrLecturaActivity.class);
                    qr.putExtra("serviceId", serviceId);
                    startActivity(qr);
                });
                //Notificacion Cancelado
                btnSecundario.setOnClickListener(v -> transitionState(EST_CANCELADO));
                break;
            case EST_CANCELADO:
                tvHeader.setText("Viaje Cancelado");
                break;

            case EST_FINALIZADO:
                tvHeader.setText("Viaje Finalizado");
                break;
        }
    }


    private void transitionState(String nuevoEstado) {
        // 1) Actualiza Firestore
        FirebaseFirestore.getInstance()
                .collection("servicios_taxi")
                .document(serviceId)
                .update("estado", nuevoEstado)
                .addOnSuccessListener(u -> {
                    estadoActual = nuevoEstado;
                    setupButtonsForState(nuevoEstado);
                    Toast.makeText(this, "Estado: " + nuevoEstado, Toast.LENGTH_SHORT).show();
                    // 2) Redibuja ruta con el nuevo target
                    if (mMap != null && !EST_SOLICITADO.equals(nuevoEstado)) {
                        drawRouteToCurrentTarget();
                    }
                });
    }

    private void drawRouteToCurrentTarget() {
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
                    LatLng target;
                    if (EST_EN_CURSO.equals(estadoActual)) {
                        // ruta cliente → destino
                        target = new LatLng(destinoLat, destinoLng);
                    } else {
                        // ruta taxi → cliente
                        target = new LatLng(clienteLat, clienteLng);
                    }
                    drawRoute(taxiPos, target);
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
            if (loc == null) return;
            LatLng taxiPos = new LatLng(loc.getLatitude(), loc.getLongitude());
            if (EST_SOLICITADO.equals(estadoActual)) {
                // solo marcadores
                placeMarkers(taxiPos, new LatLng(clienteLat, clienteLng));
            } else {
                LatLng target = EST_EN_CURSO.equals(estadoActual)
                        ? new LatLng(destinoLat, destinoLng)
                        : new LatLng(clienteLat, clienteLng);
                drawRoute(taxiPos, target);
            }
        });

        // Publica tu posición cada 2s
        LocationRequest req = LocationRequest.create()
                .setInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(req, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult lr) {
                double lat = lr.getLastLocation().getLatitude();
                double lng = lr.getLastLocation().getLongitude();
                rideRef.child("taxiLocation").setValue(Map.of("lat", lat, "lng", lng));
            }
        }, Looper.getMainLooper());

        rideRef.child("taxiLocation")
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot ds) {
                        LatLng taxiPos = new LatLng(ds.child("lat").getValue(Double.class),
                                ds.child("lng").getValue(Double.class));
                        if (EST_SOLICITADO.equals(estadoActual)) {
                            placeMarkers(taxiPos, new LatLng(clienteLat, clienteLng));
                        } else {
                            LatLng target = EST_EN_CURSO.equals(estadoActual)
                                    ? new LatLng(destinoLat, destinoLng)
                                    : new LatLng(clienteLat, clienteLng);
                            drawRoute(taxiPos, target);
                        }
                    }
                    @Override public void onCancelled(DatabaseError e) { }
                });


    }

    private void placeMarkers(LatLng taxiPos, LatLng clientePos) {
        if (markerTaxi    != null) markerTaxi.remove();
        if (markerCliente != null) markerCliente.remove();

        Bitmap bmpTaxi = BitmapFactory.decodeResource(getResources(), R.drawable.icono_auto);
        Bitmap iconTaxi = Bitmap.createScaledBitmap(bmpTaxi, 80, 80, false);
        Bitmap bmpCli  = BitmapFactory.decodeResource(getResources(), R.drawable.icono_cliente);
        Bitmap iconCli = Bitmap.createScaledBitmap(bmpCli, 80, 80, false);

        markerTaxi = mMap.addMarker(new MarkerOptions()
                .position(taxiPos)
                .title("Taxi")
                .icon(BitmapDescriptorFactory.fromBitmap(iconTaxi)));

        markerCliente = mMap.addMarker(new MarkerOptions()
                .position(clientePos)
                .title("Cliente")
                .icon(BitmapDescriptorFactory.fromBitmap(iconCli)));
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

        String title = EST_EN_CURSO.equals(estadoActual) ? "Destino" : "Cliente";
        Bitmap bmpOther = BitmapFactory.decodeResource(
                getResources(),
                EST_EN_CURSO.equals(estadoActual)
                        ? R.drawable.icono_bandera_llegada
                        : R.drawable.icono_cliente
        );
        Bitmap iconOther = Bitmap.createScaledBitmap(bmpOther,80,80,false);

        markerCliente = mMap.addMarker(new MarkerOptions()
                .position(destino)
                .title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(iconOther)));

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
