package com.ispc.florappmovil.ui.contacto;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.ispc.florappmovil.data.ContactoRepository;

public class ContactoViewModel extends ViewModel {

    private final ContactoRepository repositorio;
    private final MutableLiveData<String> mensajeExito = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public ContactoViewModel(ContactoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public LiveData<String> getMensajeExito() {
        return mensajeExito;
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public void enviarConsulta(String nombre, String email, String mensaje) {
        try {
            boolean enviado = repositorio.enviarMensajeContacto(nombre, email, mensaje);
            if (enviado) {
                mensajeExito.setValue("¡Consulta enviada con éxito!");
            } else {
                mensajeError.setValue("Error al procesar el envío");
            }
        } catch (Exception e) {
            mensajeError.setValue("Error al conectar con el servidor");
        }
    }
}
