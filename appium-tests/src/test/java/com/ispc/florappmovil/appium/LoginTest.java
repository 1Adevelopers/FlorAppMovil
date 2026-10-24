package com.ispc.florappmovil.appium;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.appmanagement.ApplicationState;

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
import static org.junit.Assert.assertTrue;

/**
 * AUT-LOG-01: automatiza el caso TC-LOG-01 (Autenticación exitosa con credenciales válidas)
 *
 * Antes de ejecutar:
 *  - Servidor Appium encendido (comando: appium)
 *  - Celular conectado por USB con la app instalada
 *  - Backend Django corriendo (runserver 0.0.0.0:8000)
 *  - Usuario de prueba registrado: docente@florapp.com / UserPass123
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
                .setNewCommandTimeout(Duration.ofSeconds(120))
                .setUiautomator2ServerInstallTimeout(Duration.ofSeconds(120))
                .setAdbExecTimeout(Duration.ofSeconds(120));

        driver = new AndroidDriver(new URL(APPIUM_URL), opciones);
        espera = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test
    public void testLoginYCierreDeSesion() {
        // 1. Abrir la app (lo hace @Before) y tocar "Iniciar sesión" en Ingreso
        espera.until(ExpectedConditions.elementToBeClickable(id("btnLogin"))).click();

        // 2. Completar los campos
        espera.until(ExpectedConditions.visibilityOfElementLocated(id("etUsuario"))).sendKeys(EMAIL);
        driver.findElement(id("etPassword")).sendKeys(PASSWORD);
        ocultarTeclado();

        // 3. Ingresar
        driver.findElement(id("btnLogin")).click();

        // 4. Corroborar que se abrió la Galería con la sesión activa ("Mi Perfil" en la barra)
        WebElement txtIngreso = espera.until(
                ExpectedConditions.visibilityOfElementLocated(id("txtIngreso")));
        assertEquals("La barra debería mostrar 'Mi Perfil' (sesión activa)",
                "Mi Perfil", txtIngreso.getText());
        assertTrue("Debería estar en la Galería",
                driver.currentActivity().endsWith("GaleriaActivity"));

        // 5. Apretar Atrás: aparece el modal de cierre de sesión
        driver.navigate().back();
        WebElement tituloModal = espera.until(
                ExpectedConditions.visibilityOfElementLocated(id("tvModalSalirTitulo")));
        assertEquals("El modal debería preguntar por el cierre de sesión",
                "¿Deseás cerrar sesión?", tituloModal.getText());

        // 6. Presionar Aceptar
        driver.findElement(id("btnModalSalirAceptar")).click();

        // 7. Vuelve a la pantalla de Ingreso (inicio)
        espera.until(ExpectedConditions.visibilityOfElementLocated(id("btnRegistrarse")));
        assertTrue("Debería volver a la pantalla de Ingreso",
                driver.currentActivity().endsWith("IngresoActivity"));
        assertTrue("Debería verse el botón 'Ingresar como invitado'",
                driver.findElement(id("btnInvitado")).isDisplayed());

        // 8. Cerrar la app
        driver.terminateApp(PAQUETE);
        assertEquals("La app debería quedar cerrada",
                ApplicationState.NOT_RUNNING, driver.queryAppState(PAQUETE));
    }

    @After
    public void cerrarAppium() {
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