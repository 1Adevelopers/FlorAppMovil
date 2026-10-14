package com.ispc.florappmovil.screens;

import com.ispc.florappmovil.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/** Screen Object de la pantalla de Ingreso (IngresoActivity). */
public class IngresoScreen extends BaseScreen {

    // Verifica que se vean los tres botones de la pantalla
    public IngresoScreen verificarVisible() {
        esperarVisible(withId(R.id.btnRegistrarse));
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.btnInvitado)).check(matches(isDisplayed()));
        return this;
    }

    // Toca "Iniciar sesión" y pasa a la pantalla de Login
    public LoginScreen tocarIniciarSesion() {
        onView(withId(R.id.btnLogin)).perform(click());
        return new LoginScreen();
    }
}