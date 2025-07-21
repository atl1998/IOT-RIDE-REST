package com.example.hotelreservaapp.cliente.TaxistaCliente;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.hotelreservaapp.R;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ClienteServicioTaxi extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_CODE_LOCATION = 1001;
    private static final int REQUEST_CODE_PLACES   = 1002;

    // Map & Places
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private PlacesClient placesClient;
    private LatLng originLatLng;
    private Marker markerActual, markerDestino;
    private Polyline rutaPolyline;

    // UI
    private TextView tvOrigin, tvTime, tvDistance;
    private EditText editTextDestination;

    // Firebase
    private FirebaseAuth auth;
    private FirebaseUser usuarioActual;
    private FirebaseFirestore db;

    // Perfil cliente
    private String nombreCliente;
    private String apellidoCliente;
    private String correoCliente;
    private String telefonoCliente;
    private String direccionCliente;
    private String dniCliente;
    private String fotoUrlCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_servicio_taxi);

        // Firebase
        auth = FirebaseAuth.getInstance();
        usuarioActual = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // cargar perfil
        cargarDatosPerfilCliente();

        // Places & Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(this);

        // Bind vistas
        tvOrigin            = findViewById(R.id.textViewOrigin);
        tvTime              = findViewById(R.id.textViewTime);
        tvDistance          = findViewById(R.id.textViewDistance);
        editTextDestination = findViewById(R.id.editTextDestination);

        // Botón volver
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        // Destino Autocomplete
        editTextDestination.setFocusable(false);
        editTextDestination.setCursorVisible(false);
        editTextDestination.setOnClickListener(v -> launchPlaceAutocomplete());

        // Inicializar mapa
        SupportMapFragment mapFrag = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFrag != null) mapFrag.getMapAsync(this);

        // Solicitar ahora
        findViewById(R.id.btnRequestNow).setOnClickListener(v -> {
            if (usuarioActual == null) {
                Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
                return;
            }
            if (originLatLng == null || markerDestino == null) {
                Toast.makeText(this, "Falta origen o destino", Toast.LENGTH_SHORT).show();
                return;
            }
            if (nombreCliente == null) {
                Toast.makeText(this, "Cargando perfil, espera...", Toast.LENGTH_SHORT).show();
                return;
            }

            TarjetaModel solicitud = new TarjetaModel();
            solicitud.setIdCliente(usuarioActual.getUid());
            solicitud.setNombreCliente(nombreCliente);
            solicitud.setCorreoCliente(correoCliente);
            solicitud.setTelefonoCliente(telefonoCliente);
            solicitud.setFotoCliente(fotoUrlCliente != null ? fotoUrlCliente : "");

            solicitud.setUbicacionOrigen(tvOrigin.getText().toString());
            solicitud.setDestino(editTextDestination.getText().toString());

            String fechaStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date());
            String horaStr  = new SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(new Date());
            solicitud.setFecha(fechaStr);
            solicitud.setHora(horaStr);

            solicitud.setEstado("Solicitado");
            solicitud.setTimestamp(System.currentTimeMillis());

            solicitud.setLatOrigen(originLatLng.latitude);
            solicitud.setLngOrigen(originLatLng.longitude);

            LatLng dest = markerDestino.getPosition();
            solicitud.setLatDestino(dest.latitude);
            solicitud.setLngDestino(dest.longitude);

            db.collection("servicios_taxi")
                    .add(solicitud)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(this, "Solicitud enviada", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error enviando solicitud", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    /** Carga campos de perfil desde Firestore */
    private void cargarDatosPerfilCliente() {
        if (usuarioActual == null) return;
        db.collection("usuarios")
                .document(usuarioActual.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;
                    nombreCliente   = doc.getString("nombre") + " " + doc.getString("apellido");
                    apellidoCliente = doc.getString("apellido");
                    correoCliente   = usuarioActual.getEmail();
                    telefonoCliente = doc.getString("telefono");
                    direccionCliente= doc.getString("direccion");
                    dniCliente      = doc.getString("numeroDocumento");
                    fotoUrlCliente  = doc.getString("urlFotoPerfil");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error cargando perfil", Toast.LENGTH_SHORT).show()
                );
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(loc -> {
                        if (loc == null) return;
                        originLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());

                        Bitmap orig = BitmapFactory.decodeResource(getResources(),
                                R.drawable.icono_ubicacion_cliente);
                        Bitmap smallOrig = Bitmap.createScaledBitmap(orig, 80, 80, false);

                        if (markerActual == null) {
                            markerActual = mMap.addMarker(new MarkerOptions()
                                    .position(originLatLng)
                                    .title("Tu ubicación")
                                    .icon(BitmapDescriptorFactory.fromBitmap(smallOrig)));
                        } else {
                            markerActual.setPosition(originLatLng);
                            markerActual.setIcon(BitmapDescriptorFactory.fromBitmap(smallOrig));
                        }

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(originLatLng, 15));
                        fetchCurrentPlaceName();
                    });

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION);
        }
    }

    private void launchPlaceAutocomplete() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            RectangularBounds bounds = originLatLng != null
                    ? RectangularBounds.newInstance(
                    new LatLng(originLatLng.latitude - .1, originLatLng.longitude - .1),
                    new LatLng(originLatLng.latitude + .1, originLatLng.longitude + .1))
                    : null;

            List<Place.Field> fields = Arrays.asList(
                    Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG
            );
            Autocomplete.IntentBuilder builder = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields);
            if (bounds != null) builder.setLocationBias(bounds);
            startActivityForResult(builder.build(this), REQUEST_CODE_PLACES);

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    private void fetchCurrentPlaceName() {
        FindCurrentPlaceRequest req = FindCurrentPlaceRequest.newInstance(
                Arrays.asList(Place.Field.NAME)
        );
        placesClient.findCurrentPlace(req)
                .addOnSuccessListener((FindCurrentPlaceResponse resp) -> {
                    if (!resp.getPlaceLikelihoods().isEmpty()) {
                        PlaceLikelihood pl = resp.getPlaceLikelihoods().get(0);
                        tvOrigin.setText(pl.getPlace().getName());
                    } else {
                        tvOrigin.setText("Origen desconocido");
                    }
                })
                .addOnFailureListener(e -> tvOrigin.setText("Error Origen"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PLACES
                && resultCode == RESULT_OK
                && data != null) {

            Place place = Autocomplete.getPlaceFromIntent(data);
            editTextDestination.setText(place.getName());
            LatLng dest = place.getLatLng();
            if (dest == null) return;

            Bitmap flag = BitmapFactory.decodeResource(getResources(),
                    R.drawable.icono_bandera_llegada);
            Bitmap smallFlag = Bitmap.createScaledBitmap(flag, 80, 80, false);

            if (markerDestino != null) markerDestino.remove();
            markerDestino = mMap.addMarker(new MarkerOptions()
                    .position(dest)
                    .title(place.getName())
                    .icon(BitmapDescriptorFactory.fromBitmap(smallFlag)));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dest, 15));

            // trazar ruta y actualizar
            if (originLatLng != null) {
                trazarRuta(originLatLng, dest);
            } else {
                Toast.makeText(this,
                        "Esperando ubicación para trazar ruta",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void trazarRuta(LatLng origen, LatLng destino) {
        String url = "https://maps.googleapis.com/maps/api/directions/json"
                + "?origin=" + origen.latitude + "," + origen.longitude
                + "&destination=" + destino.latitude + "," + destino.longitude
                + "&mode=driving"
                + "&key=" + getString(R.string.google_maps_key);

        new OkHttpClient().newCall(new Request.Builder().url(url).build())
                .enqueue(new Callback() {
                    @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(() ->
                                Toast.makeText(ClienteServicioTaxi.this,
                                        "Ruta fallida", Toast.LENGTH_SHORT).show());
                    }
                    @Override public void onResponse(@NonNull Call call, @NonNull Response resp)
                            throws IOException {
                        try {
                            if (!resp.isSuccessful()) return;
                            String body = resp.body().string();
                            JSONArray routes = new JSONObject(body).getJSONArray("routes");
                            if (routes.length() == 0) return;
                            JSONObject leg = routes.getJSONObject(0)
                                    .getJSONArray("legs").getJSONObject(0);
                            final String duration = leg.getJSONObject("duration").getString("text");
                            final String distance = leg.getJSONObject("distance").getString("text");
                            String points = routes.getJSONObject(0)
                                    .getJSONObject("overview_polyline").getString("points");
                            final List<LatLng> decoded =
                                    com.google.maps.android.PolyUtil.decode(points);

                            runOnUiThread(() -> {
                                if (rutaPolyline != null) rutaPolyline.remove();
                                rutaPolyline = mMap.addPolyline(new PolylineOptions()
                                        .addAll(decoded)
                                        .width(8)
                                        .color(0xFF000000));

                                tvTime.setText(duration);
                                tvDistance.setText(distance);

                                LatLngBounds bounds = new LatLngBounds.Builder()
                                        .include(origen)
                                        .include(destino)
                                        .build();
                                mMap.animateCamera(
                                        CameraUpdateFactory.newLatLngBounds(bounds, 100)
                                );
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] perms,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, perms, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && mMap != null) {
            onMapReady(mMap);
        }
    }
}
