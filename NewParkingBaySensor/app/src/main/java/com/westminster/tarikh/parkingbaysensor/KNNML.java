package com.westminster.tarikh.parkingbaysensor;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Model.SensorBay;

import static HelperFunctions.HelperFunction.getCurrentTime;

public class KNNML extends AsyncTask<Void, Void, Void> {
    List<SensorBay> listOfSensor;
    GoogleMap googleMap;
    GMap gmap;
    Context ctx;
    ArrayList<MarkerOptions> markers;


    public KNNML(Context ctx, GoogleMap map, List<SensorBay> res, GMap gmap) {
        listOfSensor = res;
        this.ctx = ctx;
        this.googleMap = map;
        this.gmap = gmap;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        markers = new ArrayList<>();
        int currentTime = (int) getCurrentTime();
        Log.d("MLL", "CURRENT KNN TIME: " + currentTime);

        for (SensorBay bay : listOfSensor) {
            for (int i = 0; i < bay.timeUsage.size(); i++) {
                int distance = (int) Math.sqrt((currentTime - bay.timeUsage.get(i).getHour()) * ((currentTime - bay.timeUsage.get(i).getHour())));
                bay.timeUsage.get(i).setDistant(distance);
            }
            Collections.sort(bay.timeUsage);

            if (!bay.timeUsage.isEmpty()) {
                int statusOccupied = 0;
                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(
                                Double.parseDouble(bay.getLatitude()), Double.parseDouble(bay.getLongitude())
                        ));

                //Get the average if size is greater than 10
                if (bay.timeUsage.size() > 10) {
                    for (int i = 0; i < 10; i++) {
                        if (bay.timeUsage.get(i).getStatus() == 1) {
                            statusOccupied++;
                        }
                    }
                    if (statusOccupied > 5) {
                        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        marker.title("Occupied");
                    } else {
                        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        marker.title("Vacantt");
                    }
                } else {

                    if (bay.timeUsage.get(0).getStatus() == 1) {
                        //occupied
                        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        marker.title("Occupied");

                    } else {
                        //vacant
                        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        marker.title("Vacantt");
                    }
                }
                markers.add(marker);
                GMap.markers = markers;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d("MLL", "EXECUTED MACHINE LEARNING");
        gmap.updateMapKNN(markers);
    }
}
