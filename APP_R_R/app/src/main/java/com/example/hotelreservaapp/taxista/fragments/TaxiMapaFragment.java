
package com.example.hotelreservaapp.taxista.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.hotelreservaapp.R;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TaxiMapaFragment extends Fragment implements OnMapReadyCallback {

    private MaterialButton btnBuscarenMapa;
    private FloatingActionButton btnRuta;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final int REQUEST_CODE_PLACES = 1002;
    private static final int REQUEST_CODE_LOCATION = 1001;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Marker markerActual;

    private Marker markerDestino;

    private LatLng destinoSeleccionado;
    private Polyline rutaPolyline;

    public TaxiMapaFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_key));
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        return inflater.inflate(R.layout.taxista_fragment_mapa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBuscarenMapa = view.findViewById(R.id.btnBuscarenMapa);
        btnRuta = view.findViewById(R.id.btnRuta);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnBuscarenMapa.setOnClickListener(v -> buscarLugar());

        btnRuta.setOnClickListener(v -> {
            if (markerActual != null && destinoSeleccionado != null) {
                trazarRuta(markerActual.getPosition(), destinoSeleccionado);
            } else {
                Toast.makeText(getContext(), "Selecciona un destino primero", Toast.LENGTH_SHORT).show();
            }
        });



        FloatingActionButton btnCentrarUbicacion = view.findViewById(R.id.btnCentrarUbicacion);
        btnCentrarUbicacion.setOnClickListener(v -> {
            if (markerActual != null && mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerActual.getPosition(), 16));
            }
        });


    }

    private void buscarLugar() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null || mMap == null) return;

            double lat = location.getLatitude(), lng = location.getLongitude();
            double radiusKm = 100.0;
            double latDelta = radiusKm / 111.0;
            double lngDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(lat)));
            LatLng southwest = new LatLng(lat - latDelta, lng - lngDelta);
            LatLng northeast = new LatLng(lat + latDelta, lng + lngDelta);

            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setLocationBias(RectangularBounds.newInstance(southwest, northeast))
                    .build(requireContext());
            startActivityForResult(intent, REQUEST_CODE_PLACES);
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        View locationButton = ((View) getChildFragmentManager().findFragmentById(R.id.map).getView()
                .findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.setMargins(0, 0, 30, 180);
        locationButton.setLayoutParams(params);

        mostrarUbicacionActual();
    }

    private void mostrarUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
            return;
        }

        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
                .setMinUpdateIntervalMillis(1000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult res) {
                Location location = res.getLastLocation();
                if (location == null || mMap == null) return;

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                if (markerActual != null) {
                    markerActual.setPosition(latLng);
                } else {
                    Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.icono_auto);
                    Bitmap small = Bitmap.createScaledBitmap(original, 80, 80, false);
                    markerActual = mMap.addMarker(new MarkerOptions().position(latLng).title("Estás aquí").icon(BitmapDescriptorFactory.fromBitmap(small)));
                }

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(location -> {
                        if (location != null && mMap != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                        }
                    });
        }

        Executor executor = ContextCompat.getMainExecutor(requireContext());
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, executor, locationCallback);
    }

    private void trazarRuta(LatLng origen, LatLng destino) {
        String url = "https://maps.googleapis.com/maps/api/directions/json"
                + "?origin=" + origen.latitude + "," + origen.longitude
                + "&destination=" + destino.latitude + "," + destino.longitude
                + "&mode=driving"  // fuerza ruta vehicular
                + "&key=" + getString(R.string.google_maps_key);

        Log.d("RUTA", "Origen: " + origen.latitude + "," + origen.longitude);
        Log.d("RUTA", "Destino: " + destino.latitude + "," + destino.longitude);
        Log.d("RUTA_URL", url);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.e("RUTA_ERROR", "Fallo HTTP: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        Log.e("RUTA_HTTP", "Código HTTP inválido: " + response.code());
                        return;
                    }

                    String json = response.body().string();
                    JSONObject obj = new JSONObject(json);
                    Log.d("RUTA_JSON", obj.toString()); // <- muestra respuesta cruda
                    JSONArray routes = obj.getJSONArray("routes");

                    if (routes.length() == 0) {
                        Log.e("RUTA", "No se encontraron rutas.");
                        return;
                    }

                    String encoded = routes.getJSONObject(0)
                            .getJSONObject("overview_polyline")
                            .getString("points");
                    List<LatLng> points = PolyUtil.decode(encoded);

                    Log.d("RUTA_PUNTOS", "Cantidad de puntos: " + points.size());

                    requireActivity().runOnUiThread(() -> {
                        if (rutaPolyline != null) rutaPolyline.remove();

                        rutaPolyline = mMap.addPolyline(new PolylineOptions()
                                .addAll(points)
                                .width(10)
                                .color(Color.BLACK)); // visible para pruebas

                        // Ajustar cámara para mostrar toda la ruta
                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(origen)
                                .include(destino)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
                    });

                } catch (JSONException e) {
                    Log.e("RUTA_JSON_ERROR", "Error al parsear JSON: " + e.getMessage());
                }
            }
        });
    }



    @Override
    public void onPause() {
        super.onPause();
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PLACES && resultCode == Activity.RESULT_OK && data != null) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            destinoSeleccionado = place.getLatLng();  // Guarda el destino para el botón de ruta

            if (mMap != null && destinoSeleccionado != null) {

                // Eliminar el marcador anterior, si lo hubiera
                if (markerDestino != null) {
                    markerDestino.remove();
                }

                // Agregar nuevo marcador
                markerDestino = mMap.addMarker(new MarkerOptions()
                        .position(destinoSeleccionado)
                        .title(place.getName()));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinoSeleccionado, 15));
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] perms, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, perms, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mostrarUbicacionActual();
        }
    }
}
