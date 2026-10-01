package com.ispc.florappmovil;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import android.content.SharedPreferences;


public class LoginActivity extends AppCompatActivity {
    // 1. Declaración de variables para los componentes visuales
    private EditText etUsuario;
    private EditText etPassword;
    private Button btnLogin;
    private android.widget.TextView tvIrARegistro;
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
            Intent intent = new Intent(LoginActivity.this, GaleriaActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 4. Extracción y limpieza de datos
                String usuario = etUsuario.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // 5. Validación de campos
                if (!usuario.isEmpty() && !password.isEmpty()) {
                    // llamamos al backend enviando los datos.
                    realizarLogin(usuario, password);
                } else {
                    // Si faltan datos, mostramos un mensaje temporal (Toast)
                    Toast.makeText(LoginActivity.this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
                }
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

                    // 1. Extraemos el token generado por el backend
                    String token = response.body().getAccess();

                    // 2. Lo guardamos de forma segura en el celular (Requerimiento Ciberseguridad)
                    guardarTokenLocal(token);

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

    // Método para almacenar el Token JWT en las preferencias del celular
    private void guardarTokenLocal(String token) {
        try {
            // 1. Crear la llave maestra para cifrar
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            // Creamos un archivo local privado llamado FlorAppPrefs
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(this, "FlorAppPrefsSeguro",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("JWT_TOKEN", token);
            editor.apply();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}