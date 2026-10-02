package com.ispc.florappmovil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
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

public class GaleriaActivity extends AppCompatActivity {

    private static final String URL_ESPECIES = "http://192.168.0.111:8000/api/flora/especies/";
    private Button btnFiltroTodas;
    private Button btnFiltroArboles;
    private Button btnFiltroArbustos;
    private Button btnFiltroHierbas;

    private Button btnContacto;

    private Button btnPerfil;

    private LinearLayout contenedorEspecies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        btnFiltroTodas = findViewById(R.id.btnFiltroTodas);
        btnFiltroArboles = findViewById(R.id.btnFiltroArboles);
        btnFiltroArbustos = findViewById(R.id.btnFiltroArbustos);
        btnFiltroHierbas = findViewById(R.id.btnFiltroHierbas);
        btnContacto = findViewById(R.id.btnContacto);
        btnPerfil = findViewById(R.id.btnPerfil);

        contenedorEspecies = findViewById(R.id.contenedorEspecies);


        btnContacto.setOnClickListener(v -> {
            Intent intent = new Intent(GaleriaActivity.this, ContactoActivity.class);
            startActivity(intent);
        });

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(GaleriaActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnFiltroTodas.setOnClickListener(v ->
                Toast.makeText(GaleriaActivity.this, "Filtro: Todas", Toast.LENGTH_SHORT).show()
        );

        btnFiltroArboles.setOnClickListener(v ->
                Toast.makeText(GaleriaActivity.this, "Filtro: Árboles", Toast.LENGTH_SHORT).show()
        );

        btnFiltroArbustos.setOnClickListener(v ->
                Toast.makeText(GaleriaActivity.this, "Filtro: Arbustos", Toast.LENGTH_SHORT).show()

        );

        btnFiltroHierbas.setOnClickListener(v ->
                Toast.makeText(GaleriaActivity.this, "Filtro: Hierbas", Toast.LENGTH_SHORT).show()
        );

        obtenerEspeciesBackend();
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
                JSONArray listaEspecies = new JSONArray(json);
                contenedorEspecies.removeAllViews();

                for (int i = 0; i < listaEspecies.length(); i++) {
                    JSONObject especie = listaEspecies.getJSONObject(i);
                    String nombreComun = especie.optString("nombre_comun", "Sin nombre");
                    String nombreCientifico = especie.optString("nombre_cientifico", "Sin nombre científico");

                    // Crear el estilo de la Ficha
                    LinearLayout tarjeta = new LinearLayout(this);
                    tarjeta.setOrientation(LinearLayout.VERTICAL);
                    tarjeta.setBackgroundResource(R.drawable.input_fondo);
                    tarjeta.setPadding(0, 0, 0, dpToPx(8));

                    LinearLayout.LayoutParams paramsTarjeta = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    paramsTarjeta.setMargins(0, 0, 0, dpToPx(12));
                    tarjeta.setLayoutParams(paramsTarjeta);

                    // Imagen
                    String urlImagen = "";
                    JSONArray imagenes = especie.optJSONArray("imagenes");

                    if (imagenes != null && imagenes.length() > 0) {
                        JSONObject primeraImagen = imagenes.optJSONObject(0);
                        if (primeraImagen != null) {
                            urlImagen = primeraImagen.optString("url", "");
                        }
                    }
                    
                    if (urlImagen.startsWith("/")) {
                        urlImagen = "http://192.168.0.111:8000" + urlImagen;
                    }

                    ImageView img = new ImageView(this);
                    LinearLayout.LayoutParams paramsImg = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dpToPx(140)
                    );
                    paramsImg.setMargins(0, 0, 0, dpToPx(16));
                    img.setLayoutParams(paramsImg);
                    img.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    if (!urlImagen.isEmpty()) {
                        Glide.with(this)
                                .load(urlImagen)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .error(R.drawable.ic_launcher_foreground)
                                .into(img);
                    } else {
                        img.setImageResource(R.drawable.ic_launcher_foreground);
                    }

                    // Nombre Común
                    TextView tvNombreComun = new TextView(this);
                    tvNombreComun.setText(nombreComun);
                    tvNombreComun.setTextColor(getResources().getColor(R.color.verde1));
                    tvNombreComun.setTypeface(null, Typeface.BOLD);
                    LinearLayout.LayoutParams paramsText1 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    paramsText1.setMargins(dpToPx(5), 0, 0, 0);
                    tvNombreComun.setLayoutParams(paramsText1);

                    // Nombre Científico
                    TextView tvNombreCientifico = new TextView(this);
                    tvNombreCientifico.setText(nombreCientifico);
                    tvNombreCientifico.setTypeface(null, Typeface.ITALIC);
                    LinearLayout.LayoutParams paramsText2 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    paramsText2.setMargins(dpToPx(5), 0, 0, dpToPx(10));
                    tvNombreCientifico.setLayoutParams(paramsText2);

                    tarjeta.addView(img);
                    tarjeta.addView(tvNombreComun);
                    tarjeta.addView(tvNombreCientifico);

                    contenedorEspecies.addView(tarjeta);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error al procesar los datos de las plantas", Toast.LENGTH_SHORT).show();
            }
        } else if (codigo == -1) {
            Toast.makeText(this, "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error del servidor (" + codigo + ")", Toast.LENGTH_LONG).show();
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}