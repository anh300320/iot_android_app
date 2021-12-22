package com.example.smartgarden.retrofit;

import com.example.smartgarden.client.response.BaseResponse;
import com.example.smartgarden.client.request.GardenControlRequest;
import com.example.smartgarden.client.response.GardenControlResponse;
import com.example.smartgarden.client.response.GardenListResponse;
import com.example.smartgarden.client.request.LoginRequest;
import com.example.smartgarden.client.response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SmartGardenServer {

    @POST("/api/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("/api/register")
    Call<BaseResponse> register(@Body LoginRequest request);

    @GET("/api/garden/my_gardens")
    Call<GardenListResponse> getGardenList(@Header("Authorization") String token);

    @GET("/api/garden/{id}")
    Call<GardenControlResponse> getGardenInfo(@Header("Authorization") String token, @Path("id") int gardenId);

    @POST("/api/garden/control")
    Call<GardenControlResponse> gardenControl(@Header("Authorization") String token, @Body GardenControlRequest request);
}
