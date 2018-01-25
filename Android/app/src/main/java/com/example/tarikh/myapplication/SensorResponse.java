package com.example.tarikh.myapplication;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SensorResponse {

    private HashMap<Integer, Integer> mapToUseForML = new HashMap<>();

    public SensorResponse(){
        for(int i = 1; i <= 1440; i++){
            //By default it the bay is vacant
            mapToUseForML.put(i,0);
        }
    }

    public HashMap<Integer,Integer> getMap(){
        return mapToUseForML;
    }

    private String id;

    public String get_id() { return this.id; }

    public void set_id(String id) { this.id = id; }

    private String longitude;

    public String getLongitude() { return this.longitude; }

    public void setLongitude(String longitude) { this.longitude = longitude; }

    private String latitude;

    public String getLatitude() { return this.latitude; }

    public void setLatitude(String latitude) { this.latitude = latitude; }

    private ArrayList<String> timeDateOfUsage;

    public ArrayList<String> getTimeDateOfUsage() { return this.timeDateOfUsage; }

    public void setTimeDateOfUsage(ArrayList<String> timeDateOfUsage) { this.timeDateOfUsage = timeDateOfUsage; }

    @Override
    public String toString(){
        return "ID: " + get_id() + ". Longtitude: " + getLongitude() + ". Latitude: " + getLatitude() + ". time: " + getTimeDateOfUsage().toString();
    }

}


