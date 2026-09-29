package com.ispc.florappmovil.api;

import com.ispc.florappmovil.models.LoginRequest;
import com.ispc.florappmovil.models.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FlorAppApi {
    @POST("api/usuarios/login/")
    Call<TokenResponse> loginUsuario(@Body LoginRequest loginRequest);

    // Aquí abajo tus compañeros agregarán sus métodos (ej. @GET("api/flora/especies/"))
}
