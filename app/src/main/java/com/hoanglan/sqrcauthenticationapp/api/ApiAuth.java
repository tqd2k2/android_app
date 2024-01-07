package com.hoanglan.sqrcauthenticationapp.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiAuth {
    @Multipart
    @POST("/auth")
    Call<AuthRes> sendAuth(@Part("qr_code") RequestBody qr_data, @Part MultipartBody.Part file);
}
