package com.ispc.florappmovil.models;

public class Usuario {
    private int id;
    private String nombre;
    private String apellido;
    private String email;

    // Solo se envía en los POST (Registro) o PUT (Actualización).
    // Retrofit omitirá campos null, por lo que no viajará si solo consultas el perfil.
    private String contrasena;

    public Usuario() {}

    // Constructor para registro desde la app móvil
    public Usuario(String nombre, String apellido, String email, String contrasena) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasena = contrasena;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
