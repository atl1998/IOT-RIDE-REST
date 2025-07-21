package com.example.hotelreservaapp.taxista.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class MiniMapaFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LatLng origen, destino;
    private String estado;
    private FusedLocationProviderClient fusedLocationClient;
    private Polyline rutaPolyline;

    public static MiniMapaFragment newInstance(LatLng origen, LatLng destino, String estado) {
        MiniMapaFragment fragment = new MiniMapaFragment();
        Bundle args = new Bundle();
        args.putParcelable("origen", origen);
        args.putParcelable("destino", destino);
        args.putString("estado", estado);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.taxista_fragment_mini_mapa, container, false);

        if (getArguments() != null) {
            origen  = getArguments().getParcelable("origen");
            destino = getArguments().getParcelable("destino");
            estado  = getArguments().getString("estado");
        }

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireContext());

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.miniMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Hacer el mini-mapa interactivo
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.clear();

        // Íconos
        Bitmap origenIcon  = BitmapFactory.decodeResource(
                getResources(), R.drawable.icono_ubicacion_cliente);
        Bitmap destinoIcon = BitmapFactory.decodeResource(
                getResources(), R.drawable.icono_bandera_llegada);

        if ("Solicitado".equalsIgnoreCase(estado) ||
                "Cancelado".equalsIgnoreCase(estado)&& origen != null) {
            // Solo origen
            mMap.addMarker(new MarkerOptions()
                    .position(origen)
                    .title("Origen")
                    .icon(BitmapDescriptorFactory.fromBitmap(
                            Bitmap.createScaledBitmap(origenIcon, 80, 80, false))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origen, 15));
        }
        else if (("En progreso".equalsIgnoreCase(estado) ||
                "Finalizado".equalsIgnoreCase(estado))
                && origen != null && destino != null) {
            // Ambos marcadores
            mMap.addMarker(new MarkerOptions()
                    .position(origen)
                    .title("Origen")
                    .icon(BitmapDescriptorFactory.fromBitmap(
                            Bitmap.createScaledBitmap(origenIcon, 80, 80, false))));
            mMap.addMarker(new MarkerOptions()
                    .position(destino)
                    .title("Destino")
                    .icon(BitmapDescriptorFactory.fromBitmap(
                            Bitmap.createScaledBitmap(destinoIcon, 80, 80, false))));
            // Dibujar ruta
            drawRoute(origen, destino);
        }
    }

    private void drawRoute(LatLng start, LatLng end) {
        String url =
                "https://maps.googleapis.com/maps/api/directions/json"
                        + "?origin="      + start.latitude  + "," + start.longitude
                        + "&destination=" + end.latitude    + "," + end.longitude
                        + "&mode=driving"
                        + "&key="         + getString(R.string.google_maps_key);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // manejar error
            }
            @Override
            public void onResponse(@NonNull Call call,
                                   @NonNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    JSONObject root   = new JSONObject(json);
                    JSONArray routes  = root.getJSONArray("routes");
                    if (routes.length() > 0) {
                        String encoded = routes
                                .getJSONObject(0)
                                .getJSONObject("overview_polyline")
                                .getString("points");
                        List<LatLng> points = PolyUtil.decode(encoded);
                        requireActivity().runOnUiThread(() -> {
                            if (rutaPolyline != null) {
                                rutaPolyline.remove();
                            }
                            rutaPolyline = mMap.addPolyline(new PolylineOptions()
                                    .addAll(points)
                                    .width(8)
                                    .color(ContextCompat.getColor(
                                            requireContext(), R.color.dorado)));
                            // Ajustar cámara
                            LatLngBounds bounds = new LatLngBounds.Builder()
                                    .include(start)
                                    .include(end)
                                    .build();
                            mMap.animateCamera(
                                    CameraUpdateFactory.newLatLngBounds(bounds, 60));
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}