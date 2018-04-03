package com.westminster.tarikh.parkingbaysensor;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationLatLong {

    String location;
    Context ctx;
    List<Address> list ;

    public LocationLatLong(String loc, Context ctx){
        this.location = loc;
        this.ctx = ctx;
       this.list = new ArrayList<>();
    }

    public Address getAddress(){
            doInBackground();
        if(list.size() != 0) {
            return list.get(0);
        }else{
            throw new IndexOutOfBoundsException("Incorrect address supplied.");
        }
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
    }
}
