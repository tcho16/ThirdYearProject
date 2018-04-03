package com.westminster.tarikh.parkingbaysensor;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import Model.SensorBay;

public class GMap {

     static ArrayList<MarkerOptions> markers;
     static GoogleMap gmap;
     Context ctx;

    public GMap(GoogleMap gmap, ArrayList<MarkerOptions> markers, Context ctx){
        GMap.markers = markers;
        this.gmap = gmap;
        this.ctx = ctx;
    }


    public void updateMap(GoogleMap googleMap ,List<SensorBay> res) {
        GMap.markers.clear();

        for (int i = 0; i < res.size(); i++) {
            GMap.markers.add(new MarkerOptions()
                    .position(new LatLng(
                            Double.parseDouble(res.get(i).getLatitude()), Double.parseDouble(res.get(i).getLongitude())
                    ))
                    .title("Parking Bay for ID: " + res.get(i).get_id())
            );

            int status = res.get(i).getTimeDateOfUsage().size() - 1;
            if (Integer.parseInt(res.get(i).getTimeDateOfUsage().get(status)) == 1) {
                GMap.markers.get(i).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                GMap.markers.get(i).title("Occupied");
            } else {
                GMap.markers.get(i).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                GMap.markers.get(i).title("Vacant");
            }
        }

        for (MarkerOptions m : GMap.markers) {
            googleMap.addMarker(m);
        }

        updateCameraPos(51.514471,-0.110893,9);
    }

    public void updateCameraPos(double latitude, double longitude, int zoom){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(zoom).build();
        gmap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    public void drawRoute(double longtitude, double latitude, double userLatit, double userLong) {
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        Log.d("Distance", "In fetching results");
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + userLatit + "," + userLong + "&destination=" + latitude + "," + longtitude + "&key=AIzaSyANQCpWEeO_jtzc1voVxb-Zh1j3z42vKFQ";
        Log.d("Distance", url);
        StringRequest requestGoogle = new StringRequest(Request.Method.GET, url,
                (String response) -> {
                    Log.d("Distance", response);
                    ParsingDistanceResponse distance = new ParsingDistanceResponse(response, gmap);
                    distance.execute();
                }, (VolleyError) -> {
        }
        );
        requestQueue.add(requestGoogle);
    }

    public static void lineUpdate(PolylineOptions lineOptions) {
        //Whenever an update method/poly is called, save all markers in gmap to an array
        //Whenever a clear the map
        //draw the line
        //draw the markers from the array
        gmap.clear();


        Log.d("Distance","Size of markers" + markers.size());
        for(MarkerOptions m : GMap.markers){
            gmap.addMarker(m);
        }
        gmap.addPolyline(lineOptions);


    }

    public void updateMapKNN() {
        gmap.clear();
        for(MarkerOptions marker : GMap.markers){
            gmap.addMarker(marker);
        }
    }
}
