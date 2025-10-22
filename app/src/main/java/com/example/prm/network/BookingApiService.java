package com.example.prm.network;

import com.example.prm.models.AvailabilityResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface BookingApiService {
    
    @GET("booking/availability")
    Call<AvailabilityResponse> getAvailability(
        @Header("Authorization") String authorization,
        @Query("centerId") int centerId,
        @Query("date") String date,
        @Query("serviceIds") String serviceIds
    );
}
