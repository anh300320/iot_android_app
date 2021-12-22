package com.example.smartgarden.client.response;

import com.example.smartgarden.client.dto.GardenInfo;

import java.util.List;

public class GardenListResponse extends BaseResponse {

    private List<GardenInfo> gardens;

    public List<GardenInfo> getGardens() {
        return gardens;
    }

    public void setGardens(List<GardenInfo> gardens) {
        this.gardens = gardens;
    }
}
