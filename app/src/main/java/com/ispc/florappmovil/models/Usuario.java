package com.ispc.florappmovil.models;

import com.google.gson.annotations.SerializedName;

/** Datos de un usuario tal como los envía y recibe la API (/api/usuarios/). */
public class Usuario {

    private Integer id;
    private String nombre;
    private String apellido;
    private String email;
    private Integer rol;

    @SerializedName("rol_nombre")
    private String rolNombre;

    public Usuario() {
    }

    /** Constructor para enviar cambios del perfil (PUT). */
    public Usuario(String nombre, String apellido, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public Integer getRol() {
        return rol;
    }

    public String getRolNombre() {
        return rolNombre;
    }
}