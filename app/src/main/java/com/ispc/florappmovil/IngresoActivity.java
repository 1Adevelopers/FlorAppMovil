package com.ispc.florappmovil;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

        // ingreso invitado (modal)
        btnInvitado.setOnClickListener(v -> {
            Dialog dialog = new Dialog(IngresoActivity.this);
            dialog.setContentView(R.layout.modal_invitado); // Apunta a tu nuevo layout del modal

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            Button btnModalIngresar = dialog.findViewById(R.id.btnModalIngresar);
            Button btnModalVolver = dialog.findViewById(R.id.btnModalVolver);

            btnModalIngresar.setOnClickListener(view -> {
                dialog.dismiss();
                new SessionManager(IngresoActivity.this).cerrarSesion(); // invitado: sin sesión
                Intent intent = new Intent(IngresoActivity.this, GaleriaActivity.class);
                startActivity(intent);
            });

            btnModalVolver.setOnClickListener(view -> {
                dialog.dismiss();
            });

            dialog.show();
        });
    }
}