package com.example.prm.network;

import com.example.prm.models.ApiResponse;
import com.example.prm.models.CreateVehicleRequest;
import com.example.prm.models.Vehicle;
import com.example.prm.models.VehicleListResponse;
import com.example.prm.models.VehicleRequestWrapper;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface VehicleApiService {
    
    @GET("vehicle")
    Call<VehicleListResponse> getCustomerVehicles(
        @Header("Authorization") String authorization,
        @Query("customerId") int customerId,
        @Query("pageNumber") int pageNumber,
        @Query("pageSize") int pageSize
    );

    @POST("vehicle")
    Call<ApiResponse<Vehicle>> createVehicle(
        @Header("Authorization") String authorization,
        @Body CreateVehicleRequest request
    );
}
