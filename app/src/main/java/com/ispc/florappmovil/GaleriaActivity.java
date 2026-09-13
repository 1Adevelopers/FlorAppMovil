package com.ispc.florappmovil;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GaleriaActivity extends AppCompatActivity {

    private Button btnFiltroTodas;
    private Button btnFiltroArboles;
    private Button btnFiltroArbustos;
    private Button btnFiltroHierbas;

    private LinearLayout ficha1;
    private LinearLayout ficha2;
    private LinearLayout ficha3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        btnFiltroTodas = findViewById(R.id.btnFiltroTodas);
        btnFiltroArboles = findViewById(R.id.btnFiltroArboles);
        btnFiltroArbustos = findViewById(R.id.btnFiltroArbustos);
        btnFiltroHierbas = findViewById(R.id.btnFiltroHierbas);

        ficha1 = findViewById(R.id.ficha1);
        ficha2 = findViewById(R.id.ficha2);
        ficha3 = findViewById(R.id.ficha3);

        btnFiltroTodas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GaleriaActivity.this, "Filtro: Todas", Toast.LENGTH_SHORT).show();
            }
        });

        btnFiltroArboles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GaleriaActivity.this, "Filtro: Árboles", Toast.LENGTH_SHORT).show();
            }
        });

        btnFiltroArbustos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GaleriaActivity.this, "Filtro: Arbustos", Toast.LENGTH_SHORT).show();
            }
        });

        btnFiltroHierbas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GaleriaActivity.this, "Filtro: Hierbas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}