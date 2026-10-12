package com.ispc.florappmovil.util;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ContactoValidadorTest {

    @Test
    public void valorValido_retornaTrue() {
        // Arrange
        String nombre = "Pedro Perez";

        // Act
        boolean resultado = ContactoValidador.esNombreValido(nombre);

        // Assert
        assertTrue(resultado);
    }

    @Test
    public void valorVacio_retornaFalse() {
        // Arrange
        String nombre = "   ";

        // Act
        boolean resultado = ContactoValidador.esNombreValido(nombre);

        // Assert
        assertFalse(resultado);
    }

    @Test
    public void valorEnLimiteInferior_retornaTrue() {
        // Arrange
        String nombre = "Ana"; // 3 caracteres (cumple límite >= 3)

        // Act
        boolean resultado = ContactoValidador.esNombreValido(nombre);

        // Assert
        assertTrue(resultado);
    }

    @Test
    public void valorFueraDeFormato_retornaFalse() {
        // Arrange
        String nombre = "Pedro_#123"; // Contiene números y símbolos inválidos

        // Act
        boolean resultado = ContactoValidador.esNombreValido(nombre);

        // Assert
        assertFalse(resultado);
    }
}