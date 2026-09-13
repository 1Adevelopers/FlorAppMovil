package com.ispc.florappmovil;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactoActivity extends AppCompatActivity {

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

                if (!nombre.isEmpty() && !email.isEmpty() && !mensaje.isEmpty()) {
                    Toast.makeText(ContactoActivity.this, "¡Consulta enviada con éxito!", Toast.LENGTH_LONG).show();

                    etNombre.setText("");
                    etEmail.setText("");
                    etMensaje.setText("");
                } else {
                    Toast.makeText(ContactoActivity.this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}