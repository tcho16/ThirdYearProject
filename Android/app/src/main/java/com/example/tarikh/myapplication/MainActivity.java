package com.example.tarikh.myapplication;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private RequestQueue requestQueue;
    private TextView textViewService;
    private SensorResponse parkingBaySensor;
    private GoogleMap googleMap;
    private ArrayList<MarkerOptions> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        markers = new ArrayList<>();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.hdcircle);
        textViewService = findViewById(R.id.textViewServiceOutput);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;

        setUpMap();

    }
    public void setUpMap(){

        //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void updateMap(double x, double y, int status, String id){

        googleMap.clear();

        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(x, y)).title("Parking Bay for ID: "+ id);

        // Changing marker icon
        if(status == 0){
            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        }else{
            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        }

        // adding marker
        googleMap.addMarker(marker);

        //TODO: GET CURRENT LOCATION OF USER AND PAN THE CAMERA ON THEM. IF NOT AVAIL THEN PAN
        //OVER LONDON ON THE WHOLE
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(x, y)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

    };

    //Method gets called when button is pushed.
    public void callTheService(View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.0.11:8080/jsonresult?id=45",
                (String response) -> {
            //TODO: KEEP A SET OF BAY OBJECTS AND KEEP THE NEWER ONE IN TERMS OF LONGER TIMEDAY USAGE FOR MACHINE LEARNING
            //TODO: FROM THE SET, ITERATE AND DISPLAY EACH STATUS ON THE MAP TOO
                    try{
                        ObjectMapper mapper = new ObjectMapper();
                        parkingBaySensor = mapper.readValue(response,SensorResponse.class);

                        double lat = Double.parseDouble(parkingBaySensor.getLatitude());
                        double lon = Double.parseDouble(parkingBaySensor.getLongitude());
                        ArrayList<String> timeAndDate = parkingBaySensor.getTimeDateOfUsage();
                        String id = parkingBaySensor.get_id();
                        int status = Integer.parseInt( timeAndDate.get(timeAndDate.size()-1));

                        updateMap(lat,lon, status , id);

                    } catch (JsonParseException e) {
                        e.printStackTrace();
                        CharSequence text = "Error parsing JSON";
                        printToast(getApplicationContext(),text,Toast.LENGTH_SHORT);
                    } catch (JsonMappingException e) {
                        CharSequence text = "Error mapping to JSON";
                        printToast(getApplicationContext(),text,Toast.LENGTH_SHORT);
                        e.printStackTrace();
                    } catch (IOException e) {
                        CharSequence text = "Error.";
                        printToast(getApplicationContext(),text,Toast.LENGTH_SHORT);
                        e.printStackTrace();
                    }

                },
                (VolleyError error) -> {
                    CharSequence text = "Failed to connect to the service!";
                    printToast(getApplicationContext(),text,Toast.LENGTH_SHORT);

                }
        );
        requestQueue.add(stringRequest);
    }

    public void printToast(Context context, CharSequence text, int duration){
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
