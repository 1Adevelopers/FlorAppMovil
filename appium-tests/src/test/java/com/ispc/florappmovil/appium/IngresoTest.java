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

import static org.junit.Assert.assertTrue;

public class IngresoTest {

    // 1. Agregamos /wd/hub para que Appium Server GUI 1.22.3 no tire error 404
    private static final String APPIUM_URL = "http://127.0.0.1:4723/wd/hub";
    // Corregimos el paquete para que apunte a la app real sin el ".appium"
    private static final String PAQUETE = "com.ispc.florappmovil";

    private AndroidDriver driver;
    private WebDriverWait espera;

    @Before
    public void setUp() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setAutomationName("UiAutomator2");
        options.setAppPackage(PAQUETE);
        options.setAppActivity(PAQUETE + ".SplashActivity");

        // 🔑 Esta línea evita que Appium pida la APK para reinstalar
        options.setNoReset(true);

        driver = new AndroidDriver(new URL(APPIUM_URL), options);
        espera = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    public void testTC_Welcome_ElementosIniciales() {
        // 1. Verificar el botón de Iniciar Sesión (btnLogin)
        WebElement btnIngreso = espera.until(
                ExpectedConditions.presenceOfElementLocated(AppiumBy.id(PAQUETE + ":id/btnLogin"))
        );
        assertTrue("El botón de Iniciar Sesión debe mostrarse en la pantalla inicial", btnIngreso.isDisplayed());

        // 2. Verificar el botón de Registro (btnRegistrarse)
        WebElement btnRegistro = espera.until(
                ExpectedConditions.presenceOfElementLocated(AppiumBy.id(PAQUETE + ":id/btnRegistrarse"))
        );
        assertTrue("El botón de Registro debe mostrarse en la pantalla inicial", btnRegistro.isDisplayed());

        // 3. Verificar el botón de Modo Invitado (btnInvitado)
        WebElement btnInvitado = espera.until(
                ExpectedConditions.presenceOfElementLocated(AppiumBy.id(PAQUETE + ":id/btnInvitado"))
        );
        assertTrue("El botón de Modo Invitado debe mostrarse en la pantalla inicial", btnInvitado.isDisplayed());
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}