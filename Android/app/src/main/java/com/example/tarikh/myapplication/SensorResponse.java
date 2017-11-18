package com.example.tarikh.myapplication;

import java.util.List;

public class SensorResponse {

    private final String id;
    private final String longitude;
    private final String latitude;
    private final List<String> timeDateOfUsage;

    public SensorResponse(String id, String longitude, String latitude, List<String> timeDateOfUsage){

        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.timeDateOfUsage = timeDateOfUsage;
    }

    public String getId() {
        return id;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public List<String> getTimeDateOfUsage() {
        return timeDateOfUsage;
    }

}
