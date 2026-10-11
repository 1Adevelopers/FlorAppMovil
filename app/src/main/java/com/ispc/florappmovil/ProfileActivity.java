package com.ispc.florappmovil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import com.ispc.florappmovil.api.FlorAppApi;
import com.ispc.florappmovil.api.RetrofitClient;
import com.ispc.florappmovil.models.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private EditText etNombre;
    private EditText etApellido;
    private EditText etEmail;
    private Button btnModificar;

    private SessionManager sesion;
    private FlorAppApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sesion = new SessionManager(this);

        // Un invitado no tiene token: no puede ver el perfil
        if (!sesion.haySesion()) {
            Toast.makeText(this, "Iniciá sesión para ver tu perfil", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        api = RetrofitClient.getClient().create(FlorAppApi.class);

        Button btnCerrar = findViewById(R.id.btnCerrar);
        btnModificar = findViewById(R.id.btnModificar);
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etEmail = findViewById(R.id.etEmail);

        btnModificar.setOnClickListener(v -> actualizarPerfil());
        btnCerrar.setOnClickListener(v -> cerrarSesion());

        cargarPerfil();

        NavbarManager.setupNavbar(this, "perfil");
    }

    // GET /api/usuarios/{id}/ : trae los datos del usuario logueado
    private void cargarPerfil() {
        api.obtenerUsuario(sesion.obtenerHeaderAutorizacion(), sesion.obtenerUsuarioId())
                .enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Usuario usuario = response.body();
                            etNombre.setText(usuario.getNombre());
                            etApellido.setText(usuario.getApellido());
                            etEmail.setText(usuario.getEmail());
                        } else if (response.code() == 401) {
                            sesionVencida();
                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    "No se pudieron cargar los datos (" + response.code() + ")",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        Toast.makeText(ProfileActivity.this,
                                "Error de red: comprobá tu conexión", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // PUT /api/usuarios/{id}/ : guarda los cambios del perfil
    private void actualizarPerfil() {
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Ingrese un correo electrónico válido");
            etEmail.requestFocus();
            return;
        }

        btnModificar.setEnabled(false); // evita envíos dobles
        Usuario datos = new Usuario(nombre, apellido, email);

        api.actualizarUsuario(sesion.obtenerHeaderAutorizacion(), sesion.obtenerUsuarioId(), datos)
                .enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        btnModificar.setEnabled(true);
                        if (response.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this,
                                    "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 401) {
                            sesionVencida();
                        } else if (response.code() == 400) {
                            Toast.makeText(ProfileActivity.this,
                                    "Datos inválidos o el email ya está en uso", Toast.LENGTH_LONG).show();
                        } else if (response.code() == 403) {
                            Toast.makeText(ProfileActivity.this,
                                    "No tenés permiso para modificar este perfil", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    "Error del servidor (" + response.code() + ")", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        btnModificar.setEnabled(true);
                        Toast.makeText(ProfileActivity.this,
                                "Error de red: comprobá tu conexión", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // El token venció (dura 30 minutos): se borra la sesión y se pide login otra vez
    private void sesionVencida() {
        sesion.cerrarSesion();
        Toast.makeText(this, "Tu sesión venció. Volvé a iniciar sesión.", Toast.LENGTH_LONG).show();
        irA(LoginActivity.class);
    }

    // Cerrar sesión: borra el token y vuelve a la pantalla de ingreso
    private void cerrarSesion() {
        sesion.cerrarSesion();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        irA(IngresoActivity.class);
    }

    // Abre una pantalla y borra el historial, así con "Atrás" no se vuelve al perfil
    private void irA(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}