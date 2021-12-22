package com.example.smartgarden.client.response;

import com.example.smartgarden.client.dto.GardenInfo;

public class GardenControlResponse {
    private GardenInfo gardenInfo;

    public GardenInfo getGardenInfo() {
        return gardenInfo;
    }

    public void setGardenInfo(GardenInfo gardenInfo) {
        this.gardenInfo = gardenInfo;
    }
}
