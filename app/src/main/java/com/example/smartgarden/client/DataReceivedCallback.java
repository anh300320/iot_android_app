package com.example.smartgarden.client;

public class DataReceivedCallback implements Callback{

    private String value;

    public DataReceivedCallback(String value) {
        this.value = value;
    }

    @Override
    public void doCallback(String value) {
        this.value = value;
    }
}
