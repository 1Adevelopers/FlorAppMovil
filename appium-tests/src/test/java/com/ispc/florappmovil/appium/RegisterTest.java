package com.ispc.florappmovil.appium;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static org.junit.Assert.assertTrue;

public class RegisterTest {

    private static final String APPIUM_URL = "http://127.0.0.1:4723";
    private static final String PAQUETE = "com.ispc.florappmovil";

    private AndroidDriver driver;
    private WebDriverWait espera;

    @Before
    public void abrirApp() throws MalformedURLException {

        UiAutomator2Options opciones = new UiAutomator2Options()
                .setAppPackage(PAQUETE)
                .setAppActivity(".SplashActivity")
                .setNoReset(false)
                .setNewCommandTimeout(Duration.ofSeconds(120))
                .setUiautomator2ServerInstallTimeout(Duration.ofSeconds(120))
                .setAdbExecTimeout(Duration.ofSeconds(120));

        driver = new AndroidDriver(new URL(APPIUM_URL), opciones);
        espera = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test
    public void testRegistroExitoso() {
        // 1. Inicia en Splash y esperamos que aparezca la pantalla de Ingreso.
        // Hacemos clic en el botón de Registrarse.
        espera.until(ExpectedConditions.elementToBeClickable(id("btnRegistrarse"))).click();

        // 2. Esperar a que cargue el formulario de Registro y llenar campos
        espera.until(ExpectedConditions.visibilityOfElementLocated(id("etNombre"))).sendKeys("Carlos");
        driver.findElement(id("etApellido")).sendKeys("Docente");
        driver.findElement(id("etEmail")).sendKeys("carlos.docente@florapp.com");
        ocultarTeclado();

        driver.findElement(id("etPassword")).sendKeys("Password123!");
        driver.findElement(id("etConfirmarPassword")).sendKeys("Password123!");
        ocultarTeclado();

        // 3. Aceptar términos y condiciones
        driver.findElement(id("cbTerminos")).click();

        // 4. Hacer clic en Registrar
        driver.findElement(id("btnRegistrar")).click();

        // 5. Validar que tras el éxito, la app redirige automáticamente a la pantalla de Login
        // (Buscamos que aparezca el input de usuario del Login)
        boolean redirigidoALogin = espera.until(ExpectedConditions.visibilityOfElementLocated(id("etUsuario"))).isDisplayed();
        assertTrue("No se procesó el registro ni se redirigió al Login", redirigidoALogin);
    }

    @Test
    public void testCancelacionRegistro() {
        // 1. Inicia en Splash -> ir a Registrarse
        espera.until(ExpectedConditions.elementToBeClickable(id("btnRegistrarse"))).click();

        // 2. Llenar datos parciales
        espera.until(ExpectedConditions.visibilityOfElementLocated(id("etNombre"))).sendKeys("Prueba");
        driver.findElement(id("etApellido")).sendKeys("Arrepentido");
        ocultarTeclado();

        // 3. Presionar botón de arrepentimiento
        driver.findElement(id("btnArrepentimiento")).click();

        // 4. Presionar "Sí" en el modal nativo de Android (usamos xpath porque el modal no tiene ID propio)
        espera.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@text='Sí']"))).click();

        // 5. Validar que regresa a la pantalla de Ingreso (Buscamos el botón de Registrarse de nuevo)
        boolean regresoAIngreso = espera.until(ExpectedConditions.visibilityOfElementLocated(id("btnRegistrarse"))).isDisplayed();
        assertTrue("No se abortó el registro ni se regresó al inicio", regresoAIngreso);
    }

    @After
    public void cerrarAppium() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Funciones Helper extraídas de tu código funcional
    private By id(String nombre) {
        return AppiumBy.id(PAQUETE + ":id/" + nombre);
    }

    private void ocultarTeclado() {
        try {
            driver.hideKeyboard();
        } catch (Exception ignorado) {}
    }
}