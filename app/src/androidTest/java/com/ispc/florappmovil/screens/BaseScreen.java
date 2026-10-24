package com.ispc.florappmovil.screens;

import android.view.View;

import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

/**
 * Base de todas las Screens: métodos comunes.
 * Espresso espera solo a que la pantalla esté lista, NO a la respuesta del servidor (Django).
 * Por eso, después de una llamada al backend, usamos esperarVisible().
 */
public abstract class BaseScreen {

    private static final long TIEMPO_MAXIMO_MS = 15000; // 15 segundos
    private static final long INTERVALO_MS = 250;       // reintenta cada 0,25 s

    // Reintenta hasta que la vista se vea o se acabe el tiempo
    protected void esperarVisible(Matcher<View> vista) {
        long limite = System.currentTimeMillis() + TIEMPO_MAXIMO_MS;
        while (true) {
            try {
                onView(vista).check(matches(isDisplayed()));
                return; // apareció
            } catch (RuntimeException | AssertionError noApareceTodavia) {
                if (System.currentTimeMillis() > limite) {
                    throw noApareceTodavia; // se acabó el tiempo: el test falla
                }
                try {
                    Thread.sleep(INTERVALO_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(e);
                }
            }
        }
    }
}