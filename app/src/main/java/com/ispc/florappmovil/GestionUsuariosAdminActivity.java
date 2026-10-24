package com.ispc.florappmovil;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ispc.florappmovil.api.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GestionUsuariosAdminActivity extends AppCompatActivity {

    private LinearLayout contenedorUsuarios;
    private Button btnAnadirUsuario, btnUsuarios, btnFichas;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_usuarios_admin);

        sessionManager = new SessionManager(this);

        int rolId = sessionManager.obtenerRolId();
        if (rolId != 1) {
            Toast.makeText(this, "Acceso exclusivo para administradores", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        contenedorUsuarios = findViewById(R.id.contenedorUsuarios);
        btnAnadirUsuario = findViewById(R.id.btnAnadirUsuario);
        btnUsuarios = findViewById(R.id.btnUsuarios);
        btnFichas = findViewById(R.id.btnFichas);

        // Al presionar el botón Fichas, volvemos al panel de gestión de plantas
        btnFichas.setOnClickListener(v -> {
            Intent intent = new Intent(GestionUsuariosAdminActivity.this, GestionFichasAdminActivity.class);
            startActivity(intent);
            finish();
        });

        btnAnadirUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(GestionUsuariosAdminActivity.this, ABMUsuariosActivity.class);
            intent.putExtra("MODO", "CREAR");
            startActivity(intent);
        });

        NavbarManager.setupNavbar(this, "gestion");
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerTodosLosUsuariosBackend();
    }

    private void obtenerTodosLosUsuariosBackend() {
        String token = sessionManager.obtenerToken();

        new Thread(() -> {
            try {
                // Ajusta la ruta del endpoint de usuarios según tu API de Django
                URL url = new URL(RetrofitClient.getBaseUrl() + "api/usuarios/");
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

                    JSONArray usuarios = new JSONArray(sb.toString());
                    runOnUiThread(() -> mostrarUsuariosEnPantalla(usuarios));
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error al cargar los usuarios", Toast.LENGTH_SHORT).show());
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void mostrarUsuariosEnPantalla(JSONArray usuarios) {
        contenedorUsuarios.removeAllViews();

        try {
            for (int i = 0; i < usuarios.length(); i++) {
                JSONObject usuario = usuarios.getJSONObject(i);
                int usuarioId = usuario.optInt("id", -1);
                String nombre = usuario.optString("nombre", "");
                String apellido = usuario.optString("apellido", "");
                String email = usuario.optString("email", "");

                // Si el backend te devuelve el rol como objeto o texto:
                String rolNombre = usuario.optString("rol_nombre", "Usuario");

                View vistaItem = getLayoutInflater().inflate(R.layout.item_usuario_gestion, contenedorUsuarios, false);

                TextView tvRolUsuario = vistaItem.findViewById(R.id.tvRolUsuario);
                TextView tvNombreUsuario = vistaItem.findViewById(R.id.tvNombreUsuario);
                TextView tvEmailUsuario = vistaItem.findViewById(R.id.tvEmailUsuario);
                Button btnActualizar = vistaItem.findViewById(R.id.btnActualizarUsuario);
                Button btnBorrar = vistaItem.findViewById(R.id.btnBorrarUsuario);

                tvRolUsuario.setText(rolNombre);
                tvNombreUsuario.setText(nombre + " " + apellido);
                tvEmailUsuario.setText(email);

                btnActualizar.setOnClickListener(v -> {
                    Intent intent = new Intent(GestionUsuariosAdminActivity.this, ABMUsuariosActivity.class);
                    intent.putExtra("MODO", "ACTUALIZAR");
                    intent.putExtra("USUARIO_ID", usuarioId);
                    intent.putExtra("NOMBRE", nombre);
                    intent.putExtra("APELLIDO", apellido);
                    intent.putExtra("EMAIL", email);
                    startActivity(intent);
                });

                btnBorrar.setOnClickListener(v -> eliminarUsuarioBackend(usuarioId));

                contenedorUsuarios.addView(vistaItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminarUsuarioBackend(int idUsuario) {
        String token = sessionManager.obtenerToken();

        new Thread(() -> {
            try {
                URL url = new URL(RetrofitClient.getBaseUrl() + "api/usuarios/" + idUsuario + "/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = conn.getResponseCode();
                runOnUiThread(() -> {
                    if (responseCode == 200 || responseCode == 204) {
                        Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                        obtenerTodosLosUsuariosBackend();
                    } else {
                        Toast.makeText(this, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}