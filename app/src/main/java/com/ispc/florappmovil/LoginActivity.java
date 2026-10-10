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

import com.ispc.florappmovil.api.FlorAppApi;
import com.ispc.florappmovil.api.RetrofitClient;
import com.ispc.florappmovil.models.LoginRequest;
import com.ispc.florappmovil.models.TokenResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    // 1. Declaración de variables para los componentes visuales
    private EditText etUsuario;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvIrARegistro;
    private TextView tvInvitado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvIrARegistro = findViewById(R.id.tvIrARegistro);
        tvInvitado = findViewById(R.id.tvInvitado);

        tvIrARegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        tvInvitado.setOnClickListener(v -> {
            new SessionManager(LoginActivity.this).cerrarSesion(); // invitado: sin sesión
            Intent intent = new Intent(LoginActivity.this, GaleriaActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 4. Extracción y limpieza de datos
                String email = etUsuario.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // 5. Validación de campos
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etUsuario.setError("Ingrese un correo electrónico válido");
                    etUsuario.requestFocus();
                    return;
                }
                realizarLogin(email, password);
            }
        });
    }

    // Método para enviar los datos a Django usando Retrofit
    private void realizarLogin(String email, String pass) {
        // Preparamos la conexión apuntando a la interfaz
        FlorAppApi api = RetrofitClient.getClient().create(FlorAppApi.class);
        LoginRequest request = new LoginRequest(email, pass);

        // enqueue() ejecuta la petición de fondo para que la app no se congele
        api.loginUsuario(request).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                // Django responde OK (HTTP 200) y las credenciales son correctas
                if (response.isSuccessful() && response.body() != null) {

                    TokenResponse datos = response.body();
                    if (datos.getUser() == null || datos.getUser().getId() == null) {
                        Toast.makeText(LoginActivity.this, "Respuesta inesperada del servidor", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Guardamos token, refresh e id del usuario de forma cifrada (Requerimiento Ciberseguridad)
                    new SessionManager(LoginActivity.this).guardarSesion(
                            datos.getAccess(), datos.getRefresh(), datos.getUser().getId());

                    Toast.makeText(LoginActivity.this, "¡Bienvenido a FlorApp!", Toast.LENGTH_SHORT).show();

                    // 3. Intent a GaleriaActivity
                    Intent intent = new Intent(LoginActivity.this, GaleriaActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    // Django responde un HTTP 401 Unauthorized
                    Toast.makeText(LoginActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                // Si el servidor de Django está apagado o no hay internet
                Toast.makeText(LoginActivity.this, "Error de red: Comprueba tu conexión", Toast.LENGTH_LONG).show();
            }
        });
    }
}