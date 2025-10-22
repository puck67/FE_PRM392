package com.example.prm.network;

import com.example.prm.models.ApiResponse;
import com.example.prm.models.Center;
import com.example.prm.models.CenterListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CenterApiService {
    
    @GET("center/active")
    Call<CenterListResponse> getCenters(
        @Query("pageNumber") int pageNumber,
        @Query("pageSize") int pageSize,
        @Query("searchTerm") String searchTerm,
        @Query("city") String city
    );

    @GET("center/{id}")
    Call<ApiResponse<Center>> getCenterById(
        @Path("id") int centerId
    );
}
