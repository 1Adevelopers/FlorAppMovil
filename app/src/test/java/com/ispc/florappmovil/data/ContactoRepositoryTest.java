package com.ispc.florappmovil.data;

import com.ispc.florappmovil.api.FlorAppApi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * AUT-UNIT-04: respuesta 200 -> el envío se considera exitoso.
 * AUT-UNIT-05: respuesta 500 -> el envío se considera fallido.
 */
public class ContactoRepositoryTest {

    private MockWebServer servidor;
    private ContactoRepository repositorio;

    @Before
    public void setUp() throws Exception {
        // 1. Encender el servidor falso
        servidor = new MockWebServer();
        servidor.start();

        // 2. Crear Retrofit apuntando al servidor falso (en vez de Django)
        FlorAppApi api = new Retrofit.Builder()
                .baseUrl(servidor.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FlorAppApi.class);

        // 3. Crear el repositorio con esa API
        repositorio = new ContactoRepository(api);
    }

    @After
    public void tearDown() throws Exception {
        servidor.shutdown();
    }

    @Test
    public void enviarMensajeContacto_respuesta200_devuelveTrue() throws Exception {
        // Arrange: el servidor va a responder 200 OK
        servidor.enqueue(new MockResponse().setResponseCode(200));

        // Act
        boolean enviado = repositorio.enviarMensajeContacto(
                "Pedro Perez", "pedro@florapp.com", "Hola, quisiera hacer una consulta");

        // Assert: el repositorio informa éxito
        assertTrue("Con respuesta 200 el envío debería ser exitoso", enviado);

        // Assert: la app envió la petición correcta al servidor
        RecordedRequest peticion = servidor.takeRequest();
        assertEquals("POST", peticion.getMethod());
        assertEquals("/api/interacciones/", peticion.getPath());
        String cuerpo = peticion.getBody().readUtf8();
        assertTrue("El cuerpo debería incluir el email", cuerpo.contains("pedro@florapp.com"));
    }

    @Test
    public void enviarMensajeContacto_respuesta500_devuelveFalse() throws Exception {
        // Arrange: el servidor va a responder 500 (error interno)
        servidor.enqueue(new MockResponse().setResponseCode(500));

        // Act
        boolean enviado = repositorio.enviarMensajeContacto(
                "Pedro Perez", "pedro@florapp.com", "Hola, quisiera hacer una consulta");

        // Assert: el repositorio informa que falló
        assertFalse("Con respuesta 500 el envío debería fallar", enviado);
        assertEquals("Debería haberse hecho una sola petición", 1, servidor.getRequestCount());
    }
}