package com.ispc.florappmovil;

import com.ispc.florappmovil.models.Usuario;
import com.ispc.florappmovil.api.FlorAppApi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ispc.florappmovil.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String URL_REGISTRO = RetrofitClient.getBaseUrl() + "api/usuarios/";

    private EditText etNombre, etApellido, etEmail, etPassword, etConfirmarPassword;
    private Button btnRegistrar, btnArrepentimiento;
    private CheckBox cbTerminos;
    private TextView tvVolverALogin, tvInvitado, tvTerminos;

    private TextView tvLargo, tvMayuscula, tvMinuscula, tvNumero, tvSimbolo;

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

        tvLargo = findViewById(R.id.tvLargo);
        tvMayuscula = findViewById(R.id.tvMayuscula);
        tvMinuscula = findViewById(R.id.tvMinuscula);
        tvNumero = findViewById(R.id.tvNumero);
        tvSimbolo = findViewById(R.id.tvSimbolo);

        actualizarBotonRegistrar(false);
        cbTerminos.setOnCheckedChangeListener((buttonView, isChecked) ->
                actualizarBotonRegistrar(isChecked));

        tvTerminos.setOnClickListener(v -> mostrarModalTerminos());
        btnArrepentimiento.setOnClickListener(v -> confirmarArrepentimiento());
        btnRegistrar.setOnClickListener(v -> intentarRegistro());

        tvVolverALogin.setOnClickListener(v -> finish());
        tvInvitado.setOnClickListener(v -> {
            new SessionManager(RegisterActivity.this).cerrarSesion();
            startActivity(new Intent(RegisterActivity.this, GaleriaActivity.class));
        });

        // Validación en tiempo real
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String pass = s.toString();

                boolean largoOk = pass.length() >= 8;
                boolean mayusculaOk = !pass.equals(pass.toLowerCase());
                boolean minusculaOk = !pass.equals(pass.toUpperCase());
                boolean numeroOk = pass.matches(".*\\d.*");
                boolean simboloOk = pass.matches(".*[!@#$%^&*()].*");

                // Cambiamos el color: verde cuando cumple, gris si no
                tvLargo.setTextColor(largoOk ? Color.parseColor("#2E7D32") : Color.parseColor("#757575"));
                tvMayuscula.setTextColor(mayusculaOk ? Color.parseColor("#2E7D32") : Color.parseColor("#757575"));
                tvMinuscula.setTextColor(minusculaOk ? Color.parseColor("#2E7D32") : Color.parseColor("#757575"));
                tvNumero.setTextColor(numeroOk ? Color.parseColor("#2E7D32") : Color.parseColor("#757575"));
                tvSimbolo.setTextColor(simboloOk ? Color.parseColor("#2E7D32") : Color.parseColor("#757575"));
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // Ícono mostrar/ocultar contraseña
        configurarOjo(etPassword);
        configurarOjo(etConfirmarPassword);
    }

    private void configurarOjo(EditText campo) {
        final boolean[] visible = {false};

        campo.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (campo.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                    int iconWidth = campo.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    int padding = 100; // zona segura para tocar

                    if (event.getRawX() >= (campo.getRight() - iconWidth - padding)) {
                        visible[0] = !visible[0];

                        if (visible[0]) {
                            // Mostrar contraseña
                            campo.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            campo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ojo_abierto, 0);
                        } else {
                            // Ocultar contraseña
                            campo.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            campo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ojo_cerrado, 0);
                        }

                        // Mantener el cursor al final
                        campo.setSelection(campo.getText().length());
                        return true;
                    }
                }
            }
            return false;
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
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
            return;
        }

        registrarEnBackend(nombre, apellido, email, password);
    }

    private void registrarEnBackend(String nombre, String apellido, String email, String password) {
        actualizarBotonRegistrar(false);

        Usuario usuario = new Usuario(nombre, apellido, email, password);
        FlorAppApi api = RetrofitClient.getClient().create(FlorAppApi.class);
        Call<Usuario> call = api.registrarUsuario(usuario);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                actualizarBotonRegistrar(cbTerminos.isChecked());

                if (response.code() == 201) {
                    Toast.makeText(RegisterActivity.this, "Cuenta creada correctamente", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else if (response.code() == 400) {
                    Toast.makeText(RegisterActivity.this, "El correo ya está registrado o los datos no son válidos", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error del servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                actualizarBotonRegistrar(cbTerminos.isChecked());
                Toast.makeText(RegisterActivity.this, "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarModalTerminos() {
        Dialog dialog = new Dialog(RegisterActivity.this);
        dialog.setContentView(R.layout.modal_terminos);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        Button btnModalAceptar = dialog.findViewById(R.id.btnModalAceptar);
        Button btnModalCancelar = dialog.findViewById(R.id.btnModalCancelar);
        btnModalAceptar.setOnClickListener(v -> { cbTerminos.setChecked(true); dialog.dismiss(); });
        btnModalCancelar.setOnClickListener(v -> { cbTerminos.setChecked(false); dialog.dismiss(); });
        dialog.show();
    }

    private void confirmarArrepentimiento() {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar registro")
                .setMessage("Se borrarán todos los datos ingresados. ¿Desea continuar?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    etNombre.setText("");
                    etApellido.setText("");
                    etEmail.setText("");
                    etPassword.setText("");
                    etConfirmarPassword.setText("");
                    cbTerminos.setChecked(false);
                    actualizarBotonRegistrar(false);
                    Toast.makeText(this, "Registro cancelado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}