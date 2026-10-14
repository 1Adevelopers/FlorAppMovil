package com.ispc.florappmovil.models;

public class Categoria {
    private int id;
    private String categoria;
    private String descripcion;

    public Categoria() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
