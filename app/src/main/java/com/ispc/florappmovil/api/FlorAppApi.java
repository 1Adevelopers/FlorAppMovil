package com.ispc.florappmovil.api;

import com.ispc.florappmovil.models.Categoria;
import com.ispc.florappmovil.models.ContactoRequest;
import com.ispc.florappmovil.models.Especie;
import com.ispc.florappmovil.models.LoginRequest;
import com.ispc.florappmovil.models.TokenResponse;
import com.ispc.florappmovil.models.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface FlorAppApi {
    @POST("api/token/")
    Call<TokenResponse> loginUsuario(@Body LoginRequest loginRequest);


    // Perfil: leer los datos del usuario logueado
    @GET("api/usuarios/{id}/")
    Call<Usuario> obtenerUsuario(@Header("Authorization") String token, @Path("id") int id);

    // Perfil: actualizar los datos del usuario logueado
    @PUT("api/usuarios/{id}/")
    Call<Usuario> actualizarUsuario(@Header("Authorization") String token,
                                    @Path("id") int id,
                                    @Body Usuario usuario);

    // Crea un nuevo docente (Registro). Retorna HTTP 201.
    @POST("api/usuarios/")
    Call<Usuario> registrarUsuario(@Body Usuario usuario);

    // Trae el listado completo de categorías para el Spinner
    @GET("api/flora/categorias/")
    Call<List<Categoria>> obtenerCategorias();

    // Trae el listado completo de especies para el RecyclerView
    @GET("api/flora/especies/")
    Call<List<Especie>> obtenerEspecies();

    // Envía el formulario de contacto. Retorna HTTP 201.
    // Usamos Call<Void> porque solo nos interesa saber si fue exitoso, no necesitamos leer el JSON de respuesta.
    @POST("api/interacciones/")
    Call<Void> enviarContacto(@Body ContactoRequest contacto);


}
