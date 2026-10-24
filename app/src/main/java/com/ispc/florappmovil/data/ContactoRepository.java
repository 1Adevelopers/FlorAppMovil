package com.ispc.florappmovil.data;

import com.ispc.florappmovil.api.FlorAppApi;
import com.ispc.florappmovil.api.RetrofitClient;
import com.ispc.florappmovil.models.ContactoRequest;

import retrofit2.Response;

public class ContactoRepository {

    private final FlorAppApi api;

    // Uso normal en la app: se conecta al backend Django configurado en RetrofitClient
    public ContactoRepository() {
        this(RetrofitClient.getClient().create(FlorAppApi.class));
    }

    // Uso en tests: se le pasa una API que apunta al servidor falso (MockWebServer)
    public ContactoRepository(FlorAppApi api) {
        this.api = api;
    }

    /**
     * Envía el formulario de contacto al backend (POST api/interacciones/).
     * Devuelve true si el servidor responde 2xx (200/201) y false si responde con error (400, 500...).
     * Si no se puede conectar, lanza la excepción.
     * Es una llamada sincrónica: no llamarla desde el hilo principal de la app.
     */
    public boolean enviarMensajeContacto(String nombre, String email, String mensaje) throws Exception {
        ContactoRequest contacto = new ContactoRequest(nombre, email, mensaje);
        Response<Void> respuesta = api.enviarContacto(contacto).execute();
        return respuesta.isSuccessful();
    }
}