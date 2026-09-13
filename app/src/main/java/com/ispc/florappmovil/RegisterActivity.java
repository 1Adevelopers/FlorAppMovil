package com.ispc.florappmovil;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNombre;
    private EditText etApellido;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmarPassword;
    private Button btnRegistrar;
    private TextView tvVolverALogin;

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

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarRegistro();
            }
        });

        tvVolverALogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

        // TODO(Eric): reemplazar esta simulación por la llamada real al backend
        // (POST /api/usuarios/usuarios/) cuando el equipo defina la conexión con JWT.
        Toast.makeText(this, "Registro válido (pendiente de conexión con el backend)", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}