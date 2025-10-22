package com.example.prm.network;

import com.example.prm.models.ApiResponse;
import com.example.prm.models.LoginResponse;
import com.example.prm.models.RegisterRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body Map<String, String> loginPayload);
    
    @POST("auth/register")
    Call<ApiResponse<Object>> register(@Body RegisterRequest registerRequest);
}
