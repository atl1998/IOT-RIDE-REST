package com.example.hotelreservaapp.cliente;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.content.Intent;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelreservaapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

public class BuscadorDestino extends AppCompatActivity {

    private final Handler debounceHandler = new Handler();
    private Runnable debounceRunnable;

    private SearchView buscador;
    private ListView listaCiudades;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> ciudades = new ArrayList<>();
    private ArrayList<JSONObject> datosCiudades = new ArrayList<>();

    private final OkHttpClient cliente = new OkHttpClient();

    private final String API_HOST = "wft-geo-db.p.rapidapi.com";
    private final String API_KEY = "fe54667e64mshb5f255c609acd65p19db61jsnf9e653170159";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_activity_buscador_destino);

        // Configurar Toolbar con flecha de retroceso
        Toolbar toolbar = findViewById(R.id.toolbarDestino);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Buscar destino");
        }

        buscador = findViewById(R.id.buscadorCiudad);
        listaCiudades = findViewById(R.id.listaCiudades);

        // Mostrar hint en el SearchView
        buscador.setQueryHint("Introduce tu destino");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ciudades);
        listaCiudades.setAdapter(adapter);

        buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // No hace nada al enviar
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                debounceHandler.removeCallbacks(debounceRunnable);

                if (newText.length() >= 2) {
                    debounceRunnable = () -> buscarCiudades(newText);
                    debounceHandler.postDelayed(debounceRunnable, 400);
                } else {
                    ciudades.clear();
                    datosCiudades.clear();
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        listaCiudades.setOnItemClickListener((parent, view, position, id) -> {
            try {
                JSONObject ciudad = datosCiudades.get(position);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("name", ciudad.optString("name", ""));
                resultIntent.putExtra("city", ciudad.optString("city", ""));
                resultIntent.putExtra("country", ciudad.optString("country", ""));
                resultIntent.putExtra("region", ciudad.optString("region", ""));
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Error al enviar ciudad seleccionada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarCiudades(String query) {
        String url = "https://wft-geo-db.p.rapidapi.com/v1/geo/cities?limit=5&namePrefix=" + query + "&countryIds=PE";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", API_HOST)
                .build();

        cliente.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(BuscadorDestino.this, "Error de conexión", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ciudades.clear();
                datosCiudades.clear();

                if (response.isSuccessful()) {
                    try {
                        String json = response.body().string();
                        JSONObject obj = new JSONObject(json);
                        JSONArray data = obj.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject ciudad = data.getJSONObject(i);
                            String nombre = ciudad.getString("name");
                            String pais = ciudad.optString("country", "");
                            String region = ciudad.optString("region", "");

                            ciudades.add(nombre + ", " + region + ", " + pais);
                            datosCiudades.add(ciudad);
                        }

                        runOnUiThread(() -> adapter.notifyDataSetChanged());

                    } catch (Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(BuscadorDestino.this, "Error al procesar resultados", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            }
        });
    }

    // Volver atrás al presionar la flecha de la Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

