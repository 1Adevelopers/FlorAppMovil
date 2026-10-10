package com.ispc.florappmovil.appium;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * AUT-LOG-01: automatiza el caso TC-LOG-01 (Autenticación exitosa con credenciales válidas).
 *
 * Antes de ejecutar:
 *  - Servidor Appium encendido (comando: appium)
 *  - Celular conectado por USB con la app instalada
 *  - Backend Django corriendo (runserver 0.0.0.0:8000)
 *  - Usuario de prueba registrado: docente@florapp.com / UserPass123!
 */
public class LoginTest {

    private static final String APPIUM_URL = "http://127.0.0.1:4723";
    private static final String PAQUETE = "com.ispc.florappmovil";
    private static final String EMAIL = "docente@florapp.com";
    private static final String PASSWORD = "UserPass123";

    private AndroidDriver driver;
    private WebDriverWait espera;

    @Before
    public void abrirApp() throws MalformedURLException {
        UiAutomator2Options opciones = new UiAutomator2Options()
                .setAppPackage(PAQUETE)
                .setAppActivity(".SplashActivity")
                .setNoReset(false) // borra los datos de la app: arranca sin sesión guardada
                .setNewCommandTimeout(Duration.ofSeconds(120));

        driver = new AndroidDriver(new URL(APPIUM_URL), opciones);
        espera = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test
    public void testLoginExitoso() {
        // Pasos 1-2: esperar que termine la bienvenida y tocar "Iniciar sesión" en Ingreso
        espera.until(ExpectedConditions.elementToBeClickable(id("btnLogin"))).click();

        // Pasos 3-4: completar el formulario de login
        espera.until(ExpectedConditions.visibilityOfElementLocated(id("etUsuario"))).sendKeys(EMAIL);
        driver.findElement(id("etPassword")).sendKeys(PASSWORD);
        ocultarTeclado();

        // Paso 5: presionar "Iniciar sesión"
        driver.findElement(id("btnLogin")).click();

        // Pasos 6-7: se abre la Galería con la sesión activa (botón "Perfil")
        WebElement btnPerfil = espera.until(
                ExpectedConditions.visibilityOfElementLocated(id("btnPerfil")));
        assertEquals("Perfil", btnPerfil.getText());
        assertTrue(driver.currentActivity().endsWith("GaleriaActivity"));

        // Paso 8: LoginActivity fue cerrada (con "Atrás" no se vuelve a ella)
        driver.navigate().back();
        espera.until(d -> !driver.currentActivity().endsWith("GaleriaActivity"));
        assertFalse(driver.currentActivity().endsWith("LoginActivity"));
    }

    @After
    public void cerrarApp() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Busca un elemento por su id de Android (el mismo android:id de los XML)
    private By id(String nombre) {
        return AppiumBy.id(PAQUETE + ":id/" + nombre);
    }

    // Oculta el teclado para que no tape el botón (si no está abierto, no hace nada)
    private void ocultarTeclado() {
        try {
            driver.hideKeyboard();
        } catch (Exception ignorado) {
            // el teclado no estaba visible
        }
    }
}