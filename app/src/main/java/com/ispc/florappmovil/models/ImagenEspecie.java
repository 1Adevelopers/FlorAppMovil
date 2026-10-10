package com.ispc.florappmovil.models;

public class ImagenEspecie {
    private int id;
    private String url;

    // El backend devuelve fecha_subida, pero si la app no la muestra, no es estricto incluirla.
    // La agregamos para tener el modelo completo.
    private String fecha_subida;

    public ImagenEspecie() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getFecha_subida() { return fecha_subida; }
    public void setFecha_subida(String fecha_subida) { this.fecha_subida = fecha_subida; }
}
