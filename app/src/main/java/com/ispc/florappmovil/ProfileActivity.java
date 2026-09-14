package com.ispc.florappmovil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView tvVolver = findViewById(R.id.tvVolverGaleria);

        tvVolver.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, GaleriaActivity.class);
            startActivity(intent);
            finish();
        });
    }
}