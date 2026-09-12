package com.ispc.florappmovil;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {
    // 1. Declaración de variables para los componentes visuales
    private EditText etUsuario;
    private EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Carga el diseño XML en la pantalla
        setContentView(R.layout.activity_login);

        // 2. Vinculación: Buscamos los componentes XML por su ID
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // 3. Evento: Detectamos el clic en el botón
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 4. Extracción y limpieza de datos
                String usuario = etUsuario.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // 5. Validación de campos
                if (!usuario.isEmpty() && !password.isEmpty()) {
                    // Si hay datos, preparamos el viaje a la siguiente pantalla (Intent explícito)
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                    // Empaquetamos el nombre de usuario para enviarlo a la MainActivity
                    intent.putExtra("EXTRA_USUARIO", usuario);

                    // Iniciamos la nueva Activity
                    startActivity(intent);

                    // Cerramos el Login para que el usuario no pueda volver usando el botón Atrás
                    finish();
                } else {
                    // Si faltan datos, mostramos un mensaje temporal (Toast)
                    Toast.makeText(LoginActivity.this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}