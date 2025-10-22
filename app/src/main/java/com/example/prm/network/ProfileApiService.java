package com.example.prm.network;

import com.example.prm.models.ApiResponse;
import com.example.prm.models.User;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface ProfileApiService {
    
    @GET("auth/profile")
    Call<ApiResponse<User>> getProfile(
        @Header("Authorization") String authorization
    );

    @PUT("auth/profile") 
    Call<ApiResponse<User>> updateProfile(
        @Header("Authorization") String authorization,
        @Body User userUpdate
    );

    @POST("auth/change-password")
    Call<ApiResponse<String>> changePassword(
        @Header("Authorization") String authorization,
        @Body ChangePasswordRequest request
    );

    @Multipart
    @POST("auth/upload-avatar")
    Call<ApiResponse<String>> uploadAvatar(
        @Header("Authorization") String authorization,
        @Part MultipartBody.Part file
    );

    class ChangePasswordRequest {
        public String currentPassword;
        public String newPassword;
        public String confirmNewPassword;

        public ChangePasswordRequest(String currentPassword, String newPassword, String confirmNewPassword) {
            this.currentPassword = currentPassword;
            this.newPassword = newPassword;
            this.confirmNewPassword = confirmNewPassword;
        }
    }
}
