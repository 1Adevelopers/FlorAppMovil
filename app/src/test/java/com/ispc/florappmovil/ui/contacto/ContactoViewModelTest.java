package com.ispc.florappmovil.ui.contacto;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.ispc.florappmovil.data.ContactoRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContactoViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private ContactoRepository repositorioMock;

    private ContactoViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        viewModel = new ContactoViewModel(repositorioMock);
    }

    @Test
    public void enviarConsultaExito_actualizaEstadoExitoso() throws Exception {
        // Arrange
        String nombre = "Pedro Perez";
        String email = "pedro_perez91@gmail.com";
        String mensaje = "Hola, quisiera realizar una consulta me pueden contactar?";

        when(repositorioMock.enviarMensajeContacto(nombre, email, mensaje)).thenReturn(true);

        // Act
        viewModel.enviarConsulta(nombre, email, mensaje);

        // Assert
        verify(repositorioMock).enviarMensajeContacto(nombre, email, mensaje);
        assertEquals("¡Consulta enviada con éxito!", viewModel.getMensajeExito().getValue());
    }

    @Test
    public void enviarConsultaError_actualizaEstadoError() throws Exception {
        // Arrange
        String nombre = "Pedro Perez";
        String email = "pedro_perez91@gmail.com";
        String mensaje = "Hola, quisiera realizar una consulta me pueden contactar?";

        when(repositorioMock.enviarMensajeContacto(nombre, email, mensaje))
                .thenThrow(new RuntimeException("Error de conexion"));

        // Act
        viewModel.enviarConsulta(nombre, email, mensaje);

        // Assert
        verify(repositorioMock).enviarMensajeContacto(nombre, email, mensaje);
        assertEquals("Error al conectar con el servidor", viewModel.getMensajeError().getValue());
    }
}
