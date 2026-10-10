package com.ispc.florappmovil.api;

import com.ispc.florappmovil.models.LoginRequest;
import com.ispc.florappmovil.models.TokenResponse;
import com.ispc.florappmovil.models.Usuario;

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
}
