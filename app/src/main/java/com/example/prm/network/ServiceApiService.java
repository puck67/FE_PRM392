package com.example.prm.network;

import com.example.prm.models.ApiResponse;
import com.example.prm.models.Service;
import com.example.prm.models.ServiceListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServiceApiService {
    
    @GET("service/active")
    Call<ServiceListResponse> getActiveServices(
        @Query("pageNumber") int pageNumber,
        @Query("pageSize") int pageSize,
        @Query("searchTerm") String searchTerm,
        @Query("categoryId") Integer categoryId
    );

    @GET("service/active")
    Call<ServiceListResponse> getActiveServicesWithAuth(
        @Header("Authorization") String authorization,
        @Query("pageNumber") int pageNumber,
        @Query("pageSize") int pageSize,
        @Query("searchTerm") String searchTerm,
        @Query("categoryId") Integer categoryId
    );

    @GET("service/{id}")
    Call<ApiResponse<Service>> getServiceById(
        @Header("Authorization") String authorization,
        @Path("id") int serviceId
    );
}
