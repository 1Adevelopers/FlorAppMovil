package com.ispc.florappmovil.api;

import com.ispc.florappmovil.models.LoginRequest;
import com.ispc.florappmovil.models.TokenResponse;
import com.google.gson.JsonObject;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FlorAppApi {
    @POST("api/token/")
    Call<TokenResponse> loginUsuario(@Body LoginRequest loginRequest);

    @POST("api/usuarios/")
    Call<ResponseBody> registrarUsuario(@Body JsonObject jsonBody);



    // Aquí abajo tus compañeros agregarán sus métodos (ej. @GET("api/flora/especies/"))
}
