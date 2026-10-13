package com.ispc.florappmovil.flujos;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ispc.florappmovil.IngresoActivity;
import com.ispc.florappmovil.SessionManager;
import com.ispc.florappmovil.screens.IngresoScreen;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * AUT-ESP-01: flujo crítico de login con Espresso + Screen Object.
 * Ingreso → Login → credenciales → Galería ("Mi Perfil") → Atrás → modal → Aceptar → Ingreso.
 * Además revisa la accesibilidad en cada acción (contraste, tamaño de botones, etiquetas).
 *
 * Antes de ejecutar:
 *  - Celular conectado por USB
 *  - Backend Django corriendo (runserver 0.0.0.0:8000)
 *  - Usuario de prueba registrado: docente@florapp.com / UserPass123
 */
@RunWith(AndroidJUnit4.class)
public class LoginFlujoCriticoTest {

    private static final String EMAIL = "docente@florapp.com";
    private static final String PASSWORD = "UserPass123";

    private ActivityScenario<IngresoActivity> escenario;

    @BeforeClass
    public static void activarChequeosDeAccesibilidad() {
        // ⬇ LA LÍNEA DE ACCESIBILIDAD: revisa contraste, tamaño de botones y etiquetas en toda la pantalla
        AccessibilityChecks.enable().setRunChecksFromRootView(true);
    }

    @Before
    public void abrirApp() {
        // Arranca siempre sin sesión guardada y abre la pantalla de Ingreso
        new SessionManager(ApplicationProvider.getApplicationContext()).cerrarSesion();
        escenario = ActivityScenario.launch(IngresoActivity.class);
    }

    @After
    public void cerrarApp() {
        if (escenario != null) {
            escenario.close();
        }
    }

    @Test
    public void loginExitoso_yCierreDeSesionDesdeGaleria() {
        new IngresoScreen()
                .verificarVisible()
                .tocarIniciarSesion()
                .iniciarSesion(EMAIL, PASSWORD)
                .verificarSesionActiva()
                .presionarAtras()
                .aceptarCerrarSesion()
                .verificarVisible();
    }
}











