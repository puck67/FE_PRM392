package com.example.prm.network;

import com.example.prm.models.ApiResponse;
import com.example.prm.models.LoginRequest;
import com.example.prm.models.LoginResponse;
import com.example.prm.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest loginRequest);
    
    @POST("auth/register")
    Call<ApiResponse<Object>> register(@Body RegisterRequest registerRequest);
}
