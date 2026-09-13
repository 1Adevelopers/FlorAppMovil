package com.ispc.florappmovil;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Pantalla de carga inicial de FlorApp.
 *
 * Esta versión es 100% independiente: no depende de nada que hagan los
 * demás. Muestra la animación de bienvenida y, cuando termina, manda
 * siempre a LoginActivity.
 *
 * Más adelante, si el equipo decide guardar el login en SharedPreferences
 * para "recordar sesión", esa lógica se agrega adentro de goToNextScreen()
 * sin tocar el resto de la pantalla. Por ahora no hace falta.
 */
public class SplashActivity extends AppCompatActivity {

    // Duración total que se muestra el splash (5 segundos)
    private static final long MIN_SPLASH_TIME_MS = 5000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        animateWelcome();

        // Espera exactamente los 5 segundos antes de navegar a la siguiente pantalla
        new Handler(Looper.getMainLooper()).postDelayed(this::goToNextScreen, MIN_SPLASH_TIME_MS);
    }


    private void animateWelcome() {
        View halo = findViewById(R.id.viewHalo);
        View logo = findViewById(R.id.florapp_logo);
        View appName = findViewById(R.id.txtAppName);
        View tagline = findViewById(R.id.txtTagline);
        View progress = findViewById(R.id.progressSplash);

        ObjectAnimator haloFade = ObjectAnimator.ofFloat(halo, View.ALPHA, 0f, 1f);
        haloFade.setDuration(1000); // Un poco más suave

        ObjectAnimator logoFade = ObjectAnimator.ofFloat(logo, View.ALPHA, 0f, 1f);
        ObjectAnimator logoScaleX = ObjectAnimator.ofFloat(logo, View.SCALE_X, 0.6f, 1f);
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(logo, View.SCALE_Y, 0.6f, 1f);

        AnimatorSet logoSet = new AnimatorSet();
        logoSet.playTogether(logoFade, logoScaleX, logoScaleY);
        logoSet.setDuration(1200);
        logoSet.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator nameFade = ObjectAnimator.ofFloat(appName, View.ALPHA, 0f, 1f);
        nameFade.setDuration(800);

        ObjectAnimator taglineFade = ObjectAnimator.ofFloat(tagline, View.ALPHA, 0f, 1f);
        taglineFade.setDuration(800);

        ObjectAnimator progressFade = ObjectAnimator.ofFloat(progress, View.ALPHA, 0f, 1f);
        progressFade.setDuration(800);

        AnimatorSet textSet = new AnimatorSet();
        textSet.playTogether(nameFade, taglineFade, progressFade);

        AnimatorSet fullSequence = new AnimatorSet();
        fullSequence.playSequentially(haloFade, logoSet, textSet);
        fullSequence.start();
    }

    /**
     * Navega siempre a LoginActivity. No depende de SharedPreferences, de
     * ningún token ni de que otra pantalla esté terminada: es autosuficiente.
     */
    private void goToNextScreen() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish(); // para que no se pueda volver al splash con el botón "atrás"
    }
}