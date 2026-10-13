package com.ispc.florappmovil.screens;

import com.ispc.florappmovil.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/** Screen Object de la pantalla de Login (LoginActivity). */
public class LoginScreen extends BaseScreen {

    // Escribe el email y cierra el teclado
    public LoginScreen escribirEmail(String email) {
        esperarVisible(withId(R.id.etUsuario));
        onView(withId(R.id.etUsuario)).perform(typeText(email), closeSoftKeyboard());
        return this;
    }

    // Escribe la contraseña y cierra el teclado
    public LoginScreen escribirPassword(String password) {
        onView(withId(R.id.etPassword)).perform(typeText(password), closeSoftKeyboard());
        return this;
    }

    // Toca "Iniciar sesión" y pasa a la Galería
    public GaleriaScreen tocarIngresar() {
        onView(withId(R.id.btnLogin)).perform(click());
        return new GaleriaScreen();
    }

    // Atajo: hace los tres pasos de arriba seguidos
    public GaleriaScreen iniciarSesion(String email, String password) {
        return escribirEmail(email)
                .escribirPassword(password)
                .tocarIngresar();
    }
}