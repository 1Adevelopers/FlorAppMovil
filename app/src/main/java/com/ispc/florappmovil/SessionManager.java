package com.ispc.florappmovil;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Maneja la sesión del usuario: guarda, lee y borra el token JWT y el id del usuario.
 * Los datos se guardan cifrados en el celular (EncryptedSharedPreferences).
 */
public class SessionManager {

    private static final String ARCHIVO = "FlorAppPrefsSeguro";
    private static final String CLAVE_TOKEN = "JWT_TOKEN";
    private static final String CLAVE_REFRESH = "JWT_REFRESH";
    private static final String CLAVE_USUARIO_ID = "USUARIO_ID";
    private static final String CLAVE_ROL = "ROL_ID";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = crearPreferencias(context.getApplicationContext());
    }

    private static SharedPreferences crearPreferencias(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return EncryptedSharedPreferences.create(
                    context,
                    ARCHIVO,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException("No se pudo abrir el almacenamiento seguro", e);
        }
    }

    /** Guarda los datos de la sesión después de un login exitoso. */
    public void guardarSesion(String token, String refresh, int usuarioId, int rolId) {
        prefs.edit()
                .putString(CLAVE_TOKEN, token)
                .putString(CLAVE_REFRESH, refresh)
                .putInt(CLAVE_USUARIO_ID, usuarioId)
                .putInt(CLAVE_ROL, rolId)
                .apply();
    }

    public String obtenerToken() {
        return prefs.getString(CLAVE_TOKEN, null);
    }

    /** Devuelve el valor listo para el header Authorization: "Bearer <token>". */
    public String obtenerHeaderAutorizacion() {
        return "Bearer " + obtenerToken();
    }

    public int obtenerUsuarioId() {
        return prefs.getInt(CLAVE_USUARIO_ID, -1);
    }

    public int obtenerRolId() { return prefs.getInt(CLAVE_ROL, 0); }

    /** true si hay un usuario logueado (los invitados no tienen token). */
    public boolean haySesion() {
        return obtenerToken() != null && obtenerUsuarioId() != -1;
    }

    /** Borra el token y los datos de la sesión (cerrar sesión). */
    /** Borra el token y los datos de la sesión (cerrar sesión). */
    public void cerrarSesion() {
        // Se borra cada dato por su nombre: clear() no es confiable con EncryptedSharedPreferences.
        // commit() asegura que se borre antes de seguir (apply() lo hace "más tarde").
        prefs.edit()
                .remove(CLAVE_TOKEN)
                .remove(CLAVE_REFRESH)
                .remove(CLAVE_USUARIO_ID)
                .remove(CLAVE_ROL)
                .commit();
    }
}