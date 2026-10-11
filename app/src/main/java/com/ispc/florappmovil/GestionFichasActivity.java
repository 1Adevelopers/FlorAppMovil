package com.ispc.florappmovil;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.ispc.florappmovil.api.RetrofitClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GestionFichasActivity extends AppCompatActivity {

    private LinearLayout contenedorEspecies;
    private Button btnAnadir;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_fichas);

        sessionManager = new SessionManager(this);

        int rolId = sessionManager.obtenerRolId();
        if (rolId != 2) {
            Toast.makeText(this, "Acceso exclusivo para docentes", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        contenedorEspecies = findViewById(R.id.contenedorEspecies);
        btnAnadir = findViewById(R.id.btnAñadir);
        btnAnadir.setOnClickListener(v -> {
            Intent intent = new Intent(GestionFichasActivity.this, ABMFichasActivity.class);
            intent.putExtra("MODO", "CREAR");
            startActivity(intent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        obtenerMisEspeciesBackend();
    }
    private void obtenerMisEspeciesBackend() {
        String token = sessionManager.obtenerToken();
        if (token == null) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL(RetrofitClient.getBaseUrl() + "api/flora/especies/mis-especies/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String linea;
                    while ((linea = reader.readLine()) != null) {
                        sb.append(linea);
                    }
                    reader.close();

                    JSONArray especies = new JSONArray(sb.toString());
                    runOnUiThread(() -> mostrarEspecies(especies));
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error al cargar tus fichas", Toast.LENGTH_SHORT).show());
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void mostrarEspecies(JSONArray especies) {
        contenedorEspecies.removeAllViews();
        try {
            for (int i = 0; i < especies.length(); i++) {
                JSONObject especie = especies.getJSONObject(i);
                int especieId = especie.getInt("id");
                String nombreComun = especie.optString("nombre_comun", "Sin nombre");
                String nombreCientifico = especie.optString("nombre_cientifico", "");
                String descripcion = especie.optString("descripcion", "");

                JSONObject categoriaDetalle = especie.optJSONObject("categoria_detalle");
                String nombreCategoria = categoriaDetalle != null ? categoriaDetalle.optString("categoria", "") : "";

                String urlImagen = "";
                JSONArray imagenes = especie.optJSONArray("imagenes");
                if (imagenes != null && imagenes.length() > 0) {
                    JSONObject primeraImg = imagenes.optJSONObject(0);
                    if (primeraImg != null) {
                        urlImagen = primeraImg.optString("url", "");
                    }
                }

                View vistaItem = getLayoutInflater().inflate(R.layout.item_ficha_gestion, contenedorEspecies, false);

                ImageView imgPlanta = vistaItem.findViewById(R.id.imgPlanta);
                TextView tvNombreComun = vistaItem.findViewById(R.id.tvNombreComun);
                TextView tvCategoria = vistaItem.findViewById(R.id.tvCategoria);
                Button btnActualizar = vistaItem.findViewById(R.id.btnActualizar);
                Button btnBorrar = vistaItem.findViewById(R.id.btnBorrar);
                tvNombreComun.setText(nombreComun);
                tvCategoria.setText(nombreCategoria);

                if (!urlImagen.isEmpty()) {
                    Glide.with(this).load(urlImagen).placeholder(R.drawable.ic_launcher_foreground).into(imgPlanta);
                }

                btnActualizar.setOnClickListener(v -> {
                    Intent intent = new Intent(GestionFichasActivity.this, ABMFichasActivity.class);
                    intent.putExtra("MODO", "ACTUALIZAR");
                    intent.putExtra("ESPECIE_ID", especieId);
                    intent.putExtra("NOMBRE_COMUN", nombreComun);
                    intent.putExtra("NOMBRE_CIENTIFICO", nombreCientifico);
                    intent.putExtra("DESCRIPCION", descripcion);
                    startActivity(intent);
                });
                btnBorrar.setOnClickListener(v -> eliminarEspecieBackend(especieId));
                contenedorEspecies.addView(vistaItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void eliminarEspecieBackend(int idEspecie) {
        String token = sessionManager.obtenerToken();

        new Thread(() -> {
            try {
                URL url = new URL(RetrofitClient.getBaseUrl() + "api/flora/especies/" + idEspecie + "/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = conn.getResponseCode();
                runOnUiThread(() -> {
                    if (responseCode == 200 || responseCode == 204) {
                        Toast.makeText(this, "Ficha eliminada con éxito", Toast.LENGTH_SHORT).show();
                        obtenerMisEspeciesBackend();
                    } else {
                        Toast.makeText(this, "Error al eliminar la ficha", Toast.LENGTH_SHORT).show();
                    }
                });
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}