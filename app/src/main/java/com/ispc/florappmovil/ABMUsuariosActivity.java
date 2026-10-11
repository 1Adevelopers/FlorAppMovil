package com.ispc.florappmovil;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class ABMUsuariosActivity extends AppCompatActivity {

    private TextView tvGestionUsuarios;
    private EditText etNombre, etApellido, etEmail, etContrasena;
    private Spinner spinnerRoles;
    private Button btnGuardar;
    private String modo = "CREAR";
    private int usuarioId = -1;

    private final List<String> listaRolesNombre = new ArrayList<>();
    private final List<Integer> listaRolesId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abm_usuarios);

        validarRolUsuario();
        vincularVistas();
        configurarModoPantalla();
        obtenerRolesBackend();
        configurarListeners();
    }

    private void validarRolUsuario() {
        int rolId = new SessionManager(this).obtenerRolId();
        if (rolId != 1) { // Solo administradores pueden gestionar usuarios
            Toast.makeText(this, "Acceso exclusivo para administradores", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void vincularVistas() {
        tvGestionUsuarios = findViewById(R.id.tvGestionUsuarios);
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etEmail = findViewById(R.id.etEmail);
        etContrasena = findViewById(R.id.etContrasena);
        spinnerRoles = findViewById(R.id.spinnerRoles);
        btnGuardar = findViewById(R.id.btnGuardar);
    }

    private void configurarModoPantalla() {
        if (getIntent() != null && getIntent().hasExtra("MODO")) {
            modo = getIntent().getStringExtra("MODO");
        }
        if ("ACTUALIZAR".equals(modo)) {
            tvGestionUsuarios.setText("Actualizar usuario");
            btnGuardar.setText("Actualizar");
            usuarioId = getIntent().getIntExtra("USUARIO_ID", -1);
            etNombre.setText(getIntent().getStringExtra("NOMBRE"));
            etApellido.setText(getIntent().getStringExtra("APELLIDO"));
            etEmail.setText(getIntent().getStringExtra("EMAIL"));
            // Opcional: si actualiza, la contraseña puede dejarse vacía o modificarse opcionalmente
            etContrasena.setHint("Dejar en blanco para mantener contraseña");
        } else {
            tvGestionUsuarios.setText("Agregar usuario");
            btnGuardar.setText("Guardar");
        }
    }

    private void configurarListeners() {
        findViewById(R.id.btnCancelar).setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> validarYGuardar());
    }

    private void validarYGuardar() {
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Por favor complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("CREAR".equals(modo) && contrasena.isEmpty()) {
            Toast.makeText(this, "La contraseña es obligatoria para un nuevo usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listaRolesId.isEmpty() || spinnerRoles.getSelectedItemPosition() < 0) {
            Toast.makeText(this, "Selecciona un tipo de perfil válido", Toast.LENGTH_SHORT).show();
            return;
        }

        int rolIdSeleccionado = listaRolesId.get(spinnerRoles.getSelectedItemPosition());
        SessionManager session = new SessionManager(this);

        enviarUsuarioBackend(nombre, apellido, email, contrasena, rolIdSeleccionado, session.obtenerToken());
    }

    private void enviarUsuarioBackend(String nombre, String apellido, String email, String contrasena, int rolId, String token) {
        new Thread(() -> {
            try {
                String urlStr = RetrofitClient.getBaseUrl() + "api/usuarios/";
                String metodo = "POST";
                if ("ACTUALIZAR".equals(modo)) {
                    urlStr += usuarioId + "/";
                    metodo = "PUT"; // O PATCH según prefiera tu backend
                }

                HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setRequestMethod(metodo);
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("nombre", nombre);
                jsonBody.put("apellido", apellido);
                jsonBody.put("email", email);
                jsonBody.put("rol", rolId);

                // Solo enviamos contraseña si el usuario escribió una nueva
                if (!contrasena.isEmpty()) {
                    jsonBody.put("contrasena", contrasena);
                }

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = conn.getResponseCode();
                runOnUiThread(() -> {
                    if (code == 200 || code == 201) {
                        Toast.makeText(this, "¡Usuario guardado correctamente!", Toast.LENGTH_SHORT).show();
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

    private void obtenerRolesBackend() {
        SessionManager session = new SessionManager(this);
        String token = session.obtenerToken();

        new Thread(() -> {
            try {
                // Endpoint de roles que definiste en tus rutas de Django
                HttpURLConnection conn = (HttpURLConnection) new URL(RetrofitClient.getBaseUrl() + "api/roles/").openConnection();
                conn.setRequestMethod("GET");
                if (token != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                }

                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String linea;
                    while ((linea = reader.readLine()) != null) sb.append(linea);
                    reader.close();

                    JSONArray roles = new JSONArray(sb.toString());
                    listaRolesNombre.clear();
                    listaRolesId.clear();

                    for (int i = 0; i < roles.length(); i++) {
                        JSONObject rol = roles.getJSONObject(i);
                        listaRolesNombre.add(rol.getString("nombre_rol"));
                        listaRolesId.add(rol.getInt("id"));
                    }

                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaRolesNombre);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerRoles.setAdapter(adapter);
                    });
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}