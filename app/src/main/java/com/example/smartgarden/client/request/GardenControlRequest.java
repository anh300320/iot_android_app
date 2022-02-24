package com.example.smartgarden.client.request;

public class GardenControlRequest {
    private int gardenId;
    private boolean auto;
    private boolean watering;
    private int humidityThreshold;
    private int humidityTopThreshold;

    public GardenControlRequest(){}

    public GardenControlRequest(int gardenId, boolean auto, boolean watering, int humidityThreshold, int humidityTopThreshold) {
        this.gardenId = gardenId;
        this.auto = auto;
        this.watering = watering;
        this.humidityThreshold = humidityThreshold;
        this.humidityTopThreshold = humidityTopThreshold;
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

    public int getHumidityTopThreshold() {
        return humidityTopThreshold;
    }

    public void setHumidityTopThreshold(int humidityTopThreshold) {
        this.humidityTopThreshold = humidityTopThreshold;
    }
}
