package com.ispc.florappmovil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.bumptech.glide.Glide;
import com.ispc.florappmovil.api.RetrofitClient;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;



public class GaleriaActivity extends AppCompatActivity {

    private static final String URL_ESPECIES = RetrofitClient.getBaseUrl() + "api/flora/especies/";
    private static final String URL_CATEGORIAS = RetrofitClient.getBaseUrl() + "api/flora/categorias/";

    private Button btnContacto;
    private Button btnPerfil;
    private LinearLayout contenedorEspecies;
    private Spinner spinnerCategorias;
    private List<String> listaCategorias = new ArrayList<>();
    private JSONArray todasLasEspecies = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);
        btnContacto = findViewById(R.id.btnContacto);
        btnPerfil = findViewById(R.id.btnPerfil);

        spinnerCategorias = findViewById(R.id.spinnerCategorias);
        contenedorEspecies = findViewById(R.id.contenedorEspecies);


        btnContacto.setOnClickListener(v -> {
            Intent intent = new Intent(GaleriaActivity.this, ContactoActivity.class);
            startActivity(intent);
        });

        if (new SessionManager(this).haySesion()) {
            // Usuario logueado: el botón abre su perfil
            btnPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(GaleriaActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        } else {
            // Invitado: el mismo botón invita a iniciar sesión (desde ahí puede registrarse)
            btnPerfil.setText("Iniciar sesión");
            btnPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(GaleriaActivity.this, LoginActivity.class);
                startActivity(intent);
            });
        }

        listaCategorias.add("Todas las categorías");
        listaCategorias.add("Todas las categorías");
        spinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoriaSeleccionada = listaCategorias.get(position);
                filtrarYMostrarEspecies(categoriaSeleccionada);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        obtenerCategoriasBackend();
        obtenerEspeciesBackend();
    }


    private void obtenerCategoriasBackend() {
        new Thread(() -> {
            try {
                URL url = new URL(URL_CATEGORIAS);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(8000);


                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String linea;
                    while ((linea = reader.readLine()) != null) sb.append(linea);
                    reader.close();


                    JSONArray arrayCat = new JSONArray(sb.toString());
                    listaCategorias.clear();
                    listaCategorias.add("Todas las categorías");


                    for (int i = 0; i < arrayCat.length(); i++) {
                        JSONObject cat = arrayCat.getJSONObject(i);
                        String nombreCat = cat.optString("categoria", "");


                        if (!nombreCat.isEmpty()) {
                            listaCategorias.add(nombreCat);
                        }
                    }


                    runOnUiThread(this::actualizarSpinner);
                }
                conn.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(GaleriaActivity.this, "Error al cargar categorías", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void actualizarSpinner() {


        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        listaCategorias
                );


        spinnerCategorias.setAdapter(adapter);
    }

    private void obtenerEspeciesBackend() {
        new Thread(() -> {
            String respuestaJson = "";
            int codigoRespuesta = -1;
            HttpURLConnection conn = null;


            try {
                URL url = new URL(URL_ESPECIES);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);


                codigoRespuesta = conn.getResponseCode();


                if (codigoRespuesta == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String linea;
                    while ((linea = reader.readLine()) != null) {
                        sb.append(linea);
                    }
                    reader.close();
                    respuestaJson = sb.toString();
                }
            } catch (Exception e) {
                codigoRespuesta = -1;
            } finally {
                if (conn != null) conn.disconnect();
            }


            final int estado = codigoRespuesta;
            final String jsonFinal = respuestaJson;


            runOnUiThread(() -> manejarRespuestaEspecies(estado, jsonFinal));
        }).start();
    }
    private void manejarRespuestaEspecies(int codigo, String json) {
        if (codigo == 200 && !json.isEmpty()) {
            try {
                todasLasEspecies = new JSONArray(json);


                String categoriaSeleccionada = spinnerCategorias.getSelectedItem() != null ?
                        spinnerCategorias.getSelectedItem().toString() : "Todas las categorías";


                filtrarYMostrarEspecies(categoriaSeleccionada);


            } catch (Exception e) {
                Toast.makeText(this, "Error al procesar los datos de las plantas", Toast.LENGTH_SHORT).show();
            }
        } else if (codigo == -1) {
            Toast.makeText(this, "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error del servidor (" + codigo + ")", Toast.LENGTH_LONG).show();
        }
    }

    private void filtrarYMostrarEspecies(String categoriaFiltro) {
        contenedorEspecies.removeAllViews();


        try {
            for (int i = 0; i < todasLasEspecies.length(); i++) {
                JSONObject especie = todasLasEspecies.getJSONObject(i);


                JSONObject categoriaDetalle =
                        especie.optJSONObject(
                                "categoria_detalle"
                        );
                String catEspecie = "";


                if (categoriaDetalle != null) {
                    catEspecie =
                            categoriaDetalle.optString(
                                    "categoria",
                                    ""
                            );
                }


                boolean mostrarTodas = categoriaFiltro.equals("Todas las categorías");
                boolean coincideCategoria = catEspecie.equalsIgnoreCase(categoriaFiltro);


                if (mostrarTodas || coincideCategoria) {
                    crearTarjetaEspecie(especie);
                }
            }
        } catch (Exception ignored) {
        }
    }
    private void crearTarjetaEspecie(JSONObject especie) {


        try {


            View ficha = getLayoutInflater().inflate(
                    R.layout.item_ficha,
                    contenedorEspecies,
                    false
            );


            ImageView img =
                    ficha.findViewById(R.id.imgPlanta);


            TextView tvDescripcion =
                    ficha.findViewById(
                            R.id.tvDescripcion
                    );


            TextView tvNombreComun =
                    ficha.findViewById(R.id.tvNombreComun);


            TextView tvNombreCientifico =
                    ficha.findViewById(R.id.tvNombreCientifico);


            TextView tvCategoria =
                    ficha.findViewById(R.id.tvCategoria);


            String descripcion =
                    especie.optString(
                            "descripcion",
                            ""
                    );


            JSONObject categoriaDetalle =
                    especie.optJSONObject(
                            "categoria_detalle"
                    );


            String categoria = "";


            if (categoriaDetalle != null) {


                categoria =
                        categoriaDetalle.optString(
                                "categoria",
                                ""
                        );
            }


            String nombreComun =
                    especie.optString(
                            "nombre_comun",
                            "Sin nombre"
                    );


            String nombreCientifico =
                    especie.optString(
                            "nombre_cientifico",
                            "Sin nombre científico"
                    );


            tvDescripcion.setText(descripcion);
            tvNombreComun.setText(nombreComun);
            tvNombreCientifico.setText(nombreCientifico);
            tvCategoria.setText(categoria);


            String urlImagen = "";


            JSONArray imagenes =
                    especie.optJSONArray("imagenes");


            if (imagenes != null &&
                    imagenes.length() > 0) {


                JSONObject primeraImagen =
                        imagenes.optJSONObject(0);


                if (primeraImagen != null) {
                    urlImagen =
                            primeraImagen.optString("url", ""
                            );
                }
            }


            if (!urlImagen.isEmpty()) {


                Glide.with(this)
                        .load(urlImagen)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground).into(img);


            } else {


                img.setImageResource(R.drawable.ic_launcher_foreground);
            }


            ficha.setOnClickListener(v -> {


                if (tvDescripcion.getVisibility() == View.GONE) {


                    tvDescripcion.setVisibility(View.VISIBLE);


                    img.setVisibility(View.GONE);
                    tvCategoria.setVisibility(View.GONE);


                } else {


                    tvDescripcion.setVisibility(View.GONE);
                    tvCategoria.setVisibility(View.VISIBLE);


                    img.setVisibility(View.VISIBLE);
                }
            });


            contenedorEspecies.addView(ficha);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}