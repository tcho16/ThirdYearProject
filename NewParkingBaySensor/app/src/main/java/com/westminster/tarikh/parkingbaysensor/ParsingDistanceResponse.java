package com.westminster.tarikh.parkingbaysensor;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParsingDistanceResponse extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    String response;
    GoogleMap gmap;
    JSONObject jObject;
    List<List<HashMap<String, String>>> routes = null;

    public ParsingDistanceResponse(String response, GoogleMap googleMap) {
        this.response = response;
        gmap = googleMap;

    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
        try {
            jObject = new JSONObject(response);
            DataParser dataParser = new DataParser();

            routes = dataParser.parse(jObject);
            Log.d("Distance",routes.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;

        for (int i = 0; i < lists.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = lists.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.RED);

            Log.d("onPostExecute","onPostExecute lineoptions decoded");

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                GMap.lineUpdate(lineOptions);

                //gmap.addPolyline(lineOptions);

            }
            else {
                Log.d("Distance","without Polylines drawn");
            }

        }

    }
}
