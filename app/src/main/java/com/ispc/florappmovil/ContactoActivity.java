package com.ispc.florappmovil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ispc.florappmovil.api.RetrofitClient;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ContactoActivity extends AppCompatActivity {

    private static final String URL_INTERACCION = RetrofitClient.getBaseUrl() + "api/interacciones/";
    private EditText etNombre;
    private EditText etEmail;
    private EditText etMensaje;
    private Button btnEnviarContacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviarContacto = findViewById(R.id.btnEnviarContacto);

        btnEnviarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String mensaje = etMensaje.getText().toString().trim();

                if (nombre.isEmpty() || email.isEmpty() || mensaje.isEmpty()) {
                    Toast.makeText(ContactoActivity.this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                enviarContactoBackend(nombre, email, mensaje);
            }
        });

        NavbarManager.setupNavbar(this, "contacto");
    }
    private void enviarContactoBackend(String nombre, String email, String mensaje) {
        btnEnviarContacto.setEnabled(false);

        new Thread(() -> {
            int codigoRespuesta = -1;
            HttpURLConnection conn = null;
            try {
                JSONObject json = new JSONObject();
                json.put("nombre", nombre);
                json.put("email", email);
                json.put("mensaje", mensaje);

                URL url = new URL(URL_INTERACCION);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                }

                codigoRespuesta = conn.getResponseCode();
            } catch (Exception e) {
                codigoRespuesta = -1;
            } finally {
                if (conn != null) conn.disconnect();
            }

            final int resultado = codigoRespuesta;

            runOnUiThread(() -> manejarRespuesta(resultado));
        }).start();
    }

    private void manejarRespuesta(int codigo) {
        btnEnviarContacto.setEnabled(true);

        if (codigo == 201) {
            Toast.makeText(ContactoActivity.this, "¡Consulta enviada con éxito!", Toast.LENGTH_LONG).show();
            etNombre.setText("");
            etEmail.setText("");
            etMensaje.setText("");
        } else if (codigo == 400) {
            Toast.makeText(ContactoActivity.this, "Datos inválidos o incompletos en el servidor", Toast.LENGTH_LONG).show();
        } else if (codigo == -1) {
            Toast.makeText(ContactoActivity.this, "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ContactoActivity.this, "Error del servidor (" + codigo + ")", Toast.LENGTH_LONG).show();
        }
    }
}