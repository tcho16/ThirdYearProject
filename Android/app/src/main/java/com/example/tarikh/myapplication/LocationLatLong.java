package com.example.tarikh.myapplication;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationLatLong {

    String location;
    Context ctx;
    List<Address> list ;
    public void setLocation(String loc){
        this.location = loc;
    }

    public LocationLatLong(String loc, Context ctx){
        this.location = loc;
        this.ctx = ctx;
       this.list = new ArrayList<>();
    }

    public Address getAddress(){
        doInBackground();
        return list.get(0);
    }


    private void doInBackground() {
        list.clear();
        Geocoder gc = new Geocoder(ctx);
        try {
            list = gc.getFromLocationName(location,1);

        } catch (IOException e) {
            Log.d("location","Failed");
            e.printStackTrace();
        }
        if(!list.isEmpty()){
            Address address = list.get(0);
            double lat = address.getLatitude();
            double lon = address.getLongitude();
        }else{
            MainActivity.printToast(ctx,"Error parsing address. Make sure internet is turned on.", Toast.LENGTH_SHORT);
        }

    }
}
