package com.ispc.florappmovil.models;

public class TokenResponse {
    private String access;  // Token JWT principal
    private String refresh; // Token para renovar el access
    private Usuario user;   // Datos del usuario que inició sesión

    public String getAccess() {
        return access;
    }

    public String getRefresh() {
        return refresh;
    }

    public Usuario getUser() {
        return user;
    }
}