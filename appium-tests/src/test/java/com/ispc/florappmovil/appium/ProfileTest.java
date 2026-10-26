package com.ispc.florappmovil.appium;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ProfileTest {

    private static final String APPIUM_URL = "http://127.0.0.1:4723/wd/hub";
    private static final String PAQUETE = "com.ispc.florappmovil";

    private AndroidDriver driver;
    private WebDriverWait espera;

    @Before
    public void abrirApp() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setAutomationName("UiAutomator2");
        options.setAppPackage(PAQUETE);
        options.setAppActivity(PAQUETE + ".SplashActivity");
        // Importante: noReset = false (por defecto) para iniciar el ciclo limpio
        options.setNoReset(true);
        driver = new AndroidDriver(new URL(APPIUM_URL), options);
        espera = new WebDriverWait(driver, Duration.ofSeconds(30)); // Subirlo a 30 segundos
    }

    @Test
    public void testTC_GP_01_VistaPerfilUsuario() {
        // 1. Realizar el login previo para llegar a la sesión activa (según flujo AUT-LOG-01)
        WebElement btnLoginInicio = espera.until(
                ExpectedConditions.presenceOfElementLocated(AppiumBy.id(PAQUETE + ":id/btnLogin"))
        );
        btnLoginInicio.click();

        WebElement inputUsuario = espera.until(
                ExpectedConditions.presenceOfElementLocated(AppiumBy.id(PAQUETE + ":id/etUsuario"))
        );
        WebElement inputPassword = driver.findElement(AppiumBy.id(PAQUETE + ":id/etPassword"));
        WebElement btnConfirmarLogin = driver.findElement(AppiumBy.id(PAQUETE + ":id/btnLogin"));

        inputUsuario.sendKeys("docente@florapp.com");
        inputPassword.sendKeys("UserPass123");
        btnConfirmarLogin.click();

        // 2. Esperar que aparezca la barra de navegación o botón "Mi Perfil" en la Galería
        WebElement btnMiPerfilNav = espera.until(
                ExpectedConditions.presenceOfElementLocated(AppiumBy.xpath("//*[contains(@text, 'Mi Perfil')]"))
        );
        btnMiPerfilNav.click();

        // 3. Verificar que carguen los elementos principales de la vista de Perfil
        WebElement tvNombrePerfil = espera.until(
                ExpectedConditions.presenceOfElementLocated(AppiumBy.id(PAQUETE + ":id/tvNombrePerfil"))
        );

        // 4. Aserciones del caso TC-GP-01
        assertNotNull("El contenedor del perfil del usuario debe estar presente", tvNombrePerfil);
        assertTrue("El nombre de usuario en el perfil debe ser visible", tvNombrePerfil.isDisplayed());
    }

    @After
    public void cerrarApp() {
        if (driver != null) {
            driver.quit();
        }
    }
}
