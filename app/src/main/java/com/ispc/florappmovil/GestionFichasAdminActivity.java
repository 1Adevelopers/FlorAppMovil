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

public class GestionFichasAdminActivity extends AppCompatActivity {

    private LinearLayout contenedorEspecies;
    private Button btnAnadir, btnUsuarios, btnFichas;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_fichas_admin);

        sessionManager = new SessionManager(this);

        int rolId = sessionManager.obtenerRolId();
        if (rolId != 1) {
            Toast.makeText(this, "Acceso exclusivo para administradores", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        contenedorEspecies = findViewById(R.id.contenedorEspecies);
        btnAnadir = findViewById(R.id.btnAñadir);
        btnUsuarios = findViewById(R.id.btnUsuarios);
        btnFichas = findViewById(R.id.btnFichas);
        btnUsuarios.setOnClickListener(v -> {
            // TODO: Redirigir a GestionUsuariosActivity cuando esté implementada
            Toast.makeText(this, "Sección de Gestión de Usuarios", Toast.LENGTH_SHORT).show();
        });
        btnAnadir.setOnClickListener(v -> {
            Intent intent = new Intent(GestionFichasAdminActivity.this, ABMFichasActivity.class);
            intent.putExtra("MODO", "CREAR");
            startActivity(intent);
        });
        NavbarManager.setupNavbar(this, "gestion");
    }
    @Override
    protected void onResume() {
        super.onResume();
        obtenerTodasLasEspeciesBackend();
    }
    private void obtenerTodasLasEspeciesBackend() {
        String token = sessionManager.obtenerToken();

        new Thread(() -> {
            try {
                URL url = new URL(RetrofitClient.getBaseUrl() + "api/flora/especies/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                if (token != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                }
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
                    runOnUiThread(() -> mostrarEspeciesEnPantalla(especies));
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error al cargar las fichas", Toast.LENGTH_SHORT).show());
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void mostrarEspeciesEnPantalla(JSONArray especies) {
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
                    Intent intent = new Intent(GestionFichasAdminActivity.this, ABMFichasActivity.class);
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
                        Toast.makeText(this, "Ficha eliminada por el administrador", Toast.LENGTH_SHORT).show();
                        obtenerTodasLasEspeciesBackend(); // Recargar lista completa
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