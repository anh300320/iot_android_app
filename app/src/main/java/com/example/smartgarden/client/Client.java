package com.example.smartgarden.client;

import com.example.smartgarden.client.dto.GardenInfo;
import com.example.smartgarden.client.request.GardenControlRequest;
import com.example.smartgarden.client.request.LoginRequest;
import com.example.smartgarden.client.response.BaseResponse;
import com.example.smartgarden.client.response.GardenControlResponse;
import com.example.smartgarden.client.response.GardenListResponse;
import com.example.smartgarden.client.response.LoginResponse;
import com.example.smartgarden.object.Garden;
import com.example.smartgarden.retrofit.SmartGardenServer;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    private static final String HTTP_SERVER_URL = "http://10.0.2.2:8081";
    private SmartGardenServer server;
    private Gson gson;

    public Client() {
        this.gson = new Gson();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HTTP_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.server = retrofit.create(SmartGardenServer.class);
    }

    // Login
    public String login(String email, String password) throws Exception {

        email = "ngohoanganh300320@gmail.com";
        password = "123456";

        final LoginRequest loginRequest = new LoginRequest(email, password);

        Call<LoginResponse> loginCall = this.server.login(loginRequest);
        Response<LoginResponse> loginResponse = loginCall.execute();
        if(loginResponse.isSuccessful()) {
            return loginResponse.body().getToken();
        } else throw new Exception("Call to login API failed");
    }

    public String register(String email, String password) throws IOException {
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<BaseResponse> registerCall = this.server.register(loginRequest);
        Response<BaseResponse> response = registerCall.execute();
        return null;
    }

    public List<GardenInfo> getMyGardenList(String token) throws IOException {
        Call<GardenListResponse> call = this.server.getGardenList( "Bearer " + token);
        Response<GardenListResponse> response = call.execute();
        if(response.body() == null) return null;
        return response.body().getGardens();
    }

    public GardenInfo sendControlCommand(String token, int gardenId, boolean isAuto, boolean isWatering, int humidityThreshold, int humidityTopThreshold) throws IOException {
        GardenControlRequest request = new GardenControlRequest(gardenId, isAuto, isWatering, humidityThreshold, humidityTopThreshold);
        Call<GardenControlResponse> controlCall = server.gardenControl("Bearer " + token, request);
        Response<GardenControlResponse> response = controlCall.execute();
        if(response.body() == null) return null;
        return response.body().getGardenInfo();
    }

    public GardenControlResponse getGardenInfo(String token, int gardenId) throws IOException {
        Call<GardenControlResponse> controlCall = server.getGardenInfo(token, gardenId);
        Response<GardenControlResponse> response = controlCall.execute();
        return response.body();
    }
}
