package com.example.tarikh.myapplication;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RequestQueue requestQueue;
    private TextView textViewService;
    private GoogleMap googleMap;
    private ArrayList<MarkerOptions> markers;
    private List<SensorResponse> listOfResponses;

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

    public void setUpMap() {

        //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void updateMap(List<SensorResponse> res) {
        markers.clear();

        for (int i = 0; i < res.size(); i++) {
            markers.add(new MarkerOptions()
                    .position(new LatLng(
                            Double.parseDouble(res.get(i).getLatitude()), Double.parseDouble(res.get(i).getLongitude())
                    ))
                    .title("Parking Bay for ID: " + res.get(i).get_id())
            );

            int status = res.get(i).getTimeDateOfUsage().size() - 1;
            if (Integer.parseInt(res.get(i).getTimeDateOfUsage().get(status)) == 0) {
                markers.get(i).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            } else {
                markers.get(i).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            }
        }

        for (MarkerOptions m : markers) {
            googleMap.addMarker(m);
        }

        //TODO: GET CURRENT LOCATION OF USER AND PAN THE CAMERA ON THEM. IF NOT AVAIL THEN PAN
        //OVER LONDON ON THE WHOLE
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(51.514471, -0.110893)).zoom(9).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

    }

    private void updateMapOne(double x, double y, int status, String id) {

        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(x, y)).title("Parking Bay for ID: " + id);

        // Changing marker icon
        if (status == 0) {
            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        } else {
            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        }

        googleMap.addMarker(marker);

        //TODO: GET CURRENT LOCATION OF USER AND PAN THE CAMERA ON THEM. IF NOT AVAIL THEN PAN
        //OVER LONDON ON THE WHOLE
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(x, y)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

    }

    private void sendRequestToServer(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.0.11:8080/alljsonresult",
                (String response) -> {
                    //TODO: IF INTERNET NOT AVAIL, KEEP A SET OF BAY OBJECTS AND KEEP THE NEWER ONE IN TERMS OF LONGER TIMEDAY USAGE FOR MACHINE LEARNING
                    //TODO: FROM THE SET, ITERATE AND DISPLAY EACH STATUS ON THE MAP TOO
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        if (listOfResponses != null) {
                            listOfResponses.clear();
                        }
                        listOfResponses = new ArrayList<>(Arrays.asList(mapper.readValue(response, SensorResponse[].class)));

                        googleMap.clear();
                        updateMap(listOfResponses);

                    } catch (JsonParseException e) {
                        e.printStackTrace();
                        CharSequence text = "Error parsing JSON";
                        printToast(getApplicationContext(), text, Toast.LENGTH_SHORT);
                    } catch (JsonMappingException e) {
                        CharSequence text = "Error mapping to JSON";
                        printToast(getApplicationContext(), text, Toast.LENGTH_SHORT);
                        e.printStackTrace();
                    } catch (IOException e) {
                        CharSequence text = "Error.";
                        printToast(getApplicationContext(), text, Toast.LENGTH_SHORT);
                        e.printStackTrace();
                    }

                },
                (VolleyError error) -> {
                    CharSequence text = "Failed to connect to the service!";
                    printToast(getApplicationContext(), text, Toast.LENGTH_SHORT);

                }
        );
        requestQueue.add(stringRequest);
    }

    //Method gets called when button is pushed.
    public void callTheService(View view) {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {
            sendRequestToServer();
        }
        else {
            googleMap.clear();
           printToast(getApplicationContext(), "Turn on the internet. Using machine learning on the bays.", Toast.LENGTH_SHORT);
           predictBaysUsingML();
        }


    }

    private void predictBaysUsingML() {
        double betaZero = 0.7287119626;
        double betaOne = -0.000380228529892768;
        float currentTime = getCurrentTime();
        Log.d("time", currentTime + " CURRENTT");

        double prediction = (Math.exp(betaZero + betaOne * currentTime)) / ( 1 + Math.exp(betaZero + betaOne * currentTime));
        Log.d("time", prediction + "This is the predrection");
        DecimalFormat df = new DecimalFormat("#.###");
        Log.d("time",df.format(prediction) + " decimal Format");


        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(
                        51.514471, -0.110893
                ))
                .title("Parking Bay for ID: 4 ML" + df.format(prediction) + " chance of being occupied" );
        if(prediction > 0.5){
            //occupied
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }else{
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        }

        googleMap.addMarker(marker);
    }

    public void printToast(Context context, CharSequence text, int duration) {
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public float getCurrentTime() {
        Calendar calandar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Log.d("time", simpleDateFormat.format(calandar.getTime()) + "Time is <-");

        String[] time = simpleDateFormat.format(calandar.getTime()).split(":");
        int hour = Integer.parseInt(time[0]) * 60;
        float currentTime = hour + Integer.parseInt(time[1]);

        return currentTime;
    }
}
