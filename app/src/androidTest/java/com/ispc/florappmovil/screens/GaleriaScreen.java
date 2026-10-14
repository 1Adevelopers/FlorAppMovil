package com.ispc.florappmovil.screens;

import com.ispc.florappmovil.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/** Screen Object de la Galería (GaleriaActivity) y su modal de salida. */
public class GaleriaScreen extends BaseScreen {

    // Espera la respuesta del servidor y verifica "Mi Perfil" en la barra
    public GaleriaScreen verificarSesionActiva() {
        esperarVisible(withId(R.id.txtIngreso));
        onView(withId(R.id.txtIngreso)).check(matches(withText("Mi Perfil")));
        return this;
    }

    // Presiona Atrás y verifica que aparezca el modal
    public GaleriaScreen presionarAtras() {
        pressBack();
        onView(withId(R.id.tvModalSalirTitulo))
                .inRoot(isDialog())
                .check(matches(withText("¿Deseás cerrar sesión?")));
        return this;
    }

    // Toca "Aceptar" en el modal y vuelve a la pantalla de Ingreso
    public IngresoScreen aceptarCerrarSesion() {
        onView(withId(R.id.btnModalSalirAceptar)).inRoot(isDialog()).perform(click());
        return new IngresoScreen();
    }
}