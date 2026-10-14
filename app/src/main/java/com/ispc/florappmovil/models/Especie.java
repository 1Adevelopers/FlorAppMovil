package com.ispc.florappmovil.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Especie {
    private int id;

    @SerializedName("nombre_comun")
    private String nombreComun;

    @SerializedName("nombre_cientifico")
    private String nombreCientifico;

    private String descripcion;

    private int categoria; // Se recibe el ID de la categoría asociada

    // Django devuelve las imágenes anidadas en el serializer de Especie
    private List<ImagenEspecie> imagenes;

    public Especie() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombreComun() { return nombreComun; }
    public void setNombreComun(String nombreComun) { this.nombreComun = nombreComun; }

    public String getNombreCientifico() { return nombreCientifico; }
    public void setNombreCientifico(String nombreCientifico) { this.nombreCientifico = nombreCientifico; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getCategoria() { return categoria; }
    public void setCategoria(int categoria) { this.categoria = categoria; }

    public List<ImagenEspecie> getImagenes() { return imagenes; }
    public void setImagenes(List<ImagenEspecie> imagenes) { this.imagenes = imagenes; }
}