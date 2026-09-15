package com.ispc.florappmovil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class IngresoActivity extends AppCompatActivity {

    private TextView tvInvitado;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegistrarse = findViewById(R.id.btnRegistrarse);
        Button btnInvitado = findViewById(R.id.btnInvitado);

        // lleva hacia la pantalla de Iniciar Sesión
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(IngresoActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // lleva hacia la pantalla de Registro
        btnRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(IngresoActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // ingreso invitado (va a Galeria)
        btnInvitado.setOnClickListener(v -> {
            Intent intent = new Intent(IngresoActivity.this, GaleriaActivity.class);
            startActivity(intent);
        });
    }
}