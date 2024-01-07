package com.hoanglan.sqrcauthenticationapp.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ApiUser {
    @GET("/user/{id}")
    Call<UserRes> getUser(@Path("id") String id, @Header("Authorization") String token);
}
