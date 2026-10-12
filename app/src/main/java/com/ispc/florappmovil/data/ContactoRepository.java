package com.ispc.florappmovil.data;

public class ContactoRepository {

    public boolean enviarMensajeContacto(String nombre, String email, String mensaje) throws Exception {
        // En la app real, aquí se consume la API REST de Django o FormSubmit
        // Para el ViewModel / Tests locales, se simula con Mockito
        return true;
    }
}
