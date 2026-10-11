package com.ispc.florappmovil;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.ispc.florappmovil.api.RetrofitClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GestionFichasActivity extends AppCompatActivity {

    private TextView tvGestionFichas;
    private EditText etNombreComun, etNombreCientifico, etDescripcion, etImagenes;
    private Spinner spinnerCategorias;
    private Button btnGuardar;
    private LinearLayout contenedorImagenesDinamicas;
    private String modo = "CREAR";
    private int especieId = -1;
    private final List<String> listaCategorias = new ArrayList<>();
    private final List<Integer> listaCategoriasId = new ArrayList<>();
    private final List<String> listaUrlsImagenes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_fichas);
        validarRolUsuario();
        vincularVistas();
        configurarModoPantalla();
        obtenerCategoriasBackend();
        configurarListeners();
    }

    private void validarRolUsuario() {
        int rolId = new SessionManager(this).obtenerRolId();
        if (rolId != 1 && rolId != 2) {
            Toast.makeText(this, "No tenés permisos para acceder a esta sección", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void vincularVistas() {
        tvGestionFichas = findViewById(R.id.tvGestionFichas);
        etNombreComun = findViewById(R.id.etNombreComun);
        etNombreCientifico = findViewById(R.id.etNombreCientifico);
        etDescripcion = findViewById(R.id.etDescripcion);
        etImagenes = findViewById(R.id.etImagenes);
        spinnerCategorias = findViewById(R.id.spinnerCategorias);
        btnGuardar = findViewById(R.id.btnGuardar);
        contenedorImagenesDinamicas = findViewById(R.id.contenedorImagenesDinamicas);
    }

    private void configurarModoPantalla() {
        if (getIntent() != null && getIntent().hasExtra("MODO")) {
            modo = getIntent().getStringExtra("MODO");
        }
        if ("ACTUALIZAR".equals(modo)) {
            tvGestionFichas.setText("Actualizar ficha");
            btnGuardar.setText("Actualizar");
            especieId = getIntent().getIntExtra("ESPECIE_ID", -1);
            etNombreComun.setText(getIntent().getStringExtra("NOMBRE_COMUN"));
            etNombreCientifico.setText(getIntent().getStringExtra("NOMBRE_CIENTIFICO"));
            etDescripcion.setText(getIntent().getStringExtra("DESCRIPCION"));
        } else {
            tvGestionFichas.setText("Agregar ficha");
            btnGuardar.setText("Guardar");
        }
    }
    private void configurarListeners() {
        findViewById(R.id.btnConfirmacion).setOnClickListener(v -> {
            String url = etImagenes.getText().toString().trim();
            if (!url.isEmpty()) {
                listaUrlsImagenes.add(url);
                agregarVistaImagenDinamica(url);
                etImagenes.setText("");
            } else {
                Toast.makeText(this, "Ingresá un link de imagen válido", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btnCancelar).setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> validarYGuardar());
    }
    private void validarYGuardar() {
        String nombreComun = etNombreComun.getText().toString().trim();
        String nombreCientifico = etNombreCientifico.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (nombreComun.isEmpty() || nombreCientifico.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }
        if (listaCategoriasId.isEmpty() || spinnerCategorias.getSelectedItemPosition() < 0) {
            Toast.makeText(this, "Selecciona una categoría válida", Toast.LENGTH_SHORT).show();
            return;
        }
        int categoriaId = listaCategoriasId.get(spinnerCategorias.getSelectedItemPosition());
        SessionManager session = new SessionManager(this);

        enviarFichaBackend(nombreComun, nombreCientifico, descripcion, categoriaId, session.obtenerUsuarioId(), session.obtenerToken());
    }
    private void enviarFichaBackend(String comun, String cientifico, String desc, int catId, int userId, String token) {
        new Thread(() -> {
            try {
                String urlStr = RetrofitClient.getBaseUrl() + "api/flora/especies/";
                String metodo = "POST";
                if ("ACTUALIZAR".equals(modo)) {
                    urlStr += especieId + "/";
                    metodo = "PUT";
                }
                HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setRequestMethod(metodo);
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("nombre_comun", comun);
                jsonBody.put("nombre_cientifico", cientifico);
                jsonBody.put("descripcion", desc);
                jsonBody.put("categoria", catId);
                jsonBody.put("usuario", userId);

                JSONArray jsonImagenes = new JSONArray();
                for (String imgUrl : listaUrlsImagenes) {
                    JSONObject imgObj = new JSONObject();
                    imgObj.put("url", imgUrl);
                    jsonImagenes.put(imgObj);
                }
                jsonBody.put("imagenes", jsonImagenes);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = conn.getResponseCode();
                runOnUiThread(() -> {
                    if (code == 200 || code == 201) {
                        Toast.makeText(this, "¡Ficha guardada!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error al guardar (Código: " + code + ")", Toast.LENGTH_LONG).show();
                    }
                });
                conn.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error de red: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
    private void obtenerCategoriasBackend() {
        new Thread(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(RetrofitClient.getBaseUrl() + "api/flora/categorias/").openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String linea;
                    while ((linea = reader.readLine()) != null) sb.append(linea);
                    reader.close();

                    JSONArray categorias = new JSONArray(sb.toString());
                    listaCategorias.clear();
                    listaCategoriasId.clear();

                    for (int i = 0; i < categorias.length(); i++) {
                        JSONObject cat = categorias.getJSONObject(i);
                        listaCategorias.add(cat.getString("categoria"));
                        listaCategoriasId.add(cat.getInt("id"));
                    }
                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaCategorias);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategorias.setAdapter(adapter);
                    });
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void agregarVistaImagenDinamica(String urlImagen) {
        View vistaItem = getLayoutInflater().inflate(R.layout.item_imagen_gestion, contenedorImagenesDinamicas, false);
        ImageView imgMiniatura = vistaItem.findViewById(R.id.imgMiniatura);
        Glide.with(this).load(urlImagen).placeholder(R.drawable.ic_launcher_foreground).into(imgMiniatura);
        vistaItem.findViewById(R.id.btnEliminarImagen).setOnClickListener(v -> {
            contenedorImagenesDinamicas.removeView(vistaItem);
            listaUrlsImagenes.remove(urlImagen);
        });
        contenedorImagenesDinamicas.addView(vistaItem);
    }
}