package com.ispc.florappmovil.util;

public class ContactoValidador {

    public static boolean esNombreValido(String nombre) {
        if (nombre == null) {
            return false;
        }
        String nombreLimpio = nombre.trim();

        // Reglas: No vacío, longitud entre 3 y 30 caracteres, solo letras y espacios
        if (nombreLimpio.isEmpty() || nombreLimpio.length() < 3 || nombreLimpio.length() > 30) {
            return false;
        }

        return nombreLimpio.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+");
    }
}