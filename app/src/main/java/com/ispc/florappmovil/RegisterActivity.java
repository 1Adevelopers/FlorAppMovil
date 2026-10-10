package com.ispc.florappmovil;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ispc.florappmovil.api.RetrofitClient;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RegisterActivity extends AppCompatActivity {

    // Emulador + Django en tu compu: 10.0.2.2. Cambiar según el entorno del equipo.
    private static final String URL_REGISTRO = RetrofitClient.getBaseUrl() + "api/usuarios/";

    private EditText etNombre, etApellido, etEmail, etPassword, etConfirmarPassword;
    private Button btnRegistrar, btnArrepentimiento;
    private CheckBox cbTerminos;
    private TextView tvVolverALogin, tvInvitado, tvTerminos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmarPassword = findViewById(R.id.etConfirmarPassword);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        tvVolverALogin = findViewById(R.id.tvVolverALogin);
        tvInvitado = findViewById(R.id.tvInvitado);
        cbTerminos = findViewById(R.id.cbTerminos);
        tvTerminos = findViewById(R.id.tvTerminos);
        btnArrepentimiento = findViewById(R.id.btnArrepentimiento);

        actualizarBotonRegistrar(false);
        cbTerminos.setOnCheckedChangeListener((buttonView, isChecked) ->
                actualizarBotonRegistrar(isChecked));

        tvTerminos.setOnClickListener(v -> mostrarTerminos());
        btnArrepentimiento.setOnClickListener(v -> confirmarArrepentimiento());
        btnRegistrar.setOnClickListener(v -> intentarRegistro());

        tvVolverALogin.setOnClickListener(v -> finish());
        tvInvitado.setOnClickListener(v -> {
            new SessionManager(RegisterActivity.this).cerrarSesion(); // invitado: sin sesión
            startActivity(new Intent(RegisterActivity.this, GaleriaActivity.class));
        });
    }

    private void actualizarBotonRegistrar(boolean habilitado) {
        btnRegistrar.setEnabled(habilitado);
        btnRegistrar.setAlpha(habilitado ? 1f : 0.5f);
    }

    private void intentarRegistro() {
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmarPassword = etConfirmarPassword.getText().toString().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmarPassword.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Ingrese un email válido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmarPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 8) {
            Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!cbTerminos.isChecked()) {
            Toast.makeText(this, "Debe aceptar los Términos y Condiciones", Toast.LENGTH_SHORT).show();
            return;
        }

        registrarEnBackend(nombre, apellido, email, password);
    }

    private void registrarEnBackend(String nombre, String apellido, String email, String password) {
        actualizarBotonRegistrar(false); // evita envíos dobles

        // La red no se puede usar en el hilo principal: se hace en un hilo aparte
        new Thread(() -> {
            int codigo = -1; // -1 = no se pudo conectar
            HttpURLConnection conn = null;
            try {
                JSONObject json = new JSONObject();
                json.put("nombre", nombre);
                json.put("apellido", apellido);
                json.put("email", email);
                json.put("contrasena", password);
                // No se envía "rol": el backend asigna el rol por defecto

                conn = (HttpURLConnection) new URL(URL_REGISTRO).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                }
                codigo = conn.getResponseCode();
            } catch (Exception e) {
                codigo = -1; // sin log de datos: nunca se registra la contraseña
            } finally {
                if (conn != null) conn.disconnect();
            }

            final int resultado = codigo;
            runOnUiThread(() -> manejarRespuesta(resultado));
        }).start();
    }

    private void manejarRespuesta(int codigo) {
        actualizarBotonRegistrar(cbTerminos.isChecked());

        if (codigo == 201) {
            Toast.makeText(this, "Cuenta creada. Ya podés iniciar sesión", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (codigo == 400) {
            Toast.makeText(this, "Datos inválidos o el email ya está registrado", Toast.LENGTH_LONG).show();
        } else if (codigo == -1) {
            Toast.makeText(this, "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error del servidor (" + codigo + ")", Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarTerminos() {
        new AlertDialog.Builder(this)
                .setTitle("Términos y Condiciones")
                .setMessage("Al registrarte aceptás que FlorApp trate tus datos personales "
                        + "(nombre, apellido y email) únicamente para gestionar tu cuenta. "
                        + "Podés solicitar la modificación o eliminación de tus datos en "
                        + "cualquier momento.")
                .setPositiveButton("Entendido", null)
                .show();
    }

    private void confirmarArrepentimiento() {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar registro")
                .setMessage("Se borrarán los datos ingresados y no se creará ninguna cuenta. ¿Querés continuar?")
                .setPositiveButton("Sí, cancelar", (dialog, which) -> {
                    etNombre.setText("");
                    etApellido.setText("");
                    etEmail.setText("");
                    etPassword.setText("");
                    etConfirmarPassword.setText("");
                    cbTerminos.setChecked(false);
                    Toast.makeText(this, "Registro cancelado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Volver", null)
                .show();
    }
}