package com.example.smartgarden.client.dto;

import androidx.annotation.NonNull;

public class GardenInfo {

    private int gardenId;
    private boolean auto;
    private boolean watering;
    private int humidityThreshold;

    public GardenInfo(){}

    public GardenInfo(int gardenId, boolean auto, boolean watering, int humidityThreshold) {
        super();
        this.gardenId = gardenId;
        this.auto = auto;
        this.watering = watering;
        this.humidityThreshold = humidityThreshold;
    }

    public int getGardenId() {
        return gardenId;
    }

    public void setGardenId(int gardenId) {
        this.gardenId = gardenId;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public boolean isWatering() {
        return watering;
    }

    public void setWatering(boolean watering) {
        this.watering = watering;
    }

    public int getHumidityThreshold() {
        return humidityThreshold;
    }

    public void setHumidityThreshold(int humidityThreshold) {
        this.humidityThreshold = humidityThreshold;
    }

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(gardenId);
    }
}
