package com.example.tarikh.myapplication;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Model.SensorBay;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RequestQueue requestQueue;
    private GoogleMap googleMap;
    private ArrayList<MarkerOptions> markers;
    private List<SensorBay> listOfResponses;
    private EditText location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(this);
        markers = new ArrayList<>();
        listOfResponses = new ArrayList<>();

        setContentView(R.layout.activity_main);
        location = findViewById(R.id.addressLookUp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);

        //Loading data if present
        if(PreferenceManager.getDefaultSharedPreferences(this).contains("listOfSavedBays")){
            listOfResponses = SaveRetrieveData.loadData(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //SaveRetrieveData.clearSharedPreferences(this);
        SaveRetrieveData.saveData(this,listOfResponses);
    }

    @Override
    public void onStop() {
        super.onStop();
        //SaveRetrieveData.clearSharedPreferences(this);
        //SaveRetrieveData.saveData(this,listOfResponses);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        //SaveRetrieveData.clearSharedPreferences(this);
        //SaveRetrieveData.saveData(this,listOfResponses);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        setUpMap();
    }

    public void setUpMap() {
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                }, 5);
            }
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }

    //Method is called when search location button is clicked
    public void searchLocationButtonOnClick(View view) {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {
            LocationLatLong loc = new LocationLatLong(String.valueOf(location.getText()), getApplicationContext());
            Address address = loc.getAddress();
            updateCameraPosition(address.getLatitude(), address.getLongitude());
        } else {
            printToast(getApplicationContext(), "Turn on internet to use this feature", Toast.LENGTH_SHORT);
        }
    }

    private void updateCameraPosition(double latitude, double longitude) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    private void updateMap(List<SensorBay> res) {
        markers.clear();

        for (int i = 0; i < res.size(); i++) {
            markers.add(new MarkerOptions()
                    .position(new LatLng(
                            Double.parseDouble(res.get(i).getLatitude()), Double.parseDouble(res.get(i).getLongitude())
                    ))
                    .title("Parking Bay for ID: " + res.get(i).get_id())
            );

            int status = res.get(i).getTimeDateOfUsage().size() - 1;
            if (Integer.parseInt(res.get(i).getTimeDateOfUsage().get(status)) == 1) {
                markers.get(i).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                markers.get(i).title("Occupied");
            } else {
                markers.get(i).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                markers.get(i).title("Vacant");
            }
        }

        for (MarkerOptions m : markers) {
            googleMap.addMarker(m);
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(51.514471, -0.110893)).zoom(9).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    //Method gets called when user wants to search for a bay
    public void callTheService(View view) {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {
            sendRequestToServer();
        } else {
            printToast(getApplicationContext(), "Turn on the internet. Using machine learning on the bays.", Toast.LENGTH_SHORT);
            startMachineLearning();
        }
    }

    private void sendRequestToServer() {
        // http://10.100.150.208:8080/alljsonresult <-- uni IPv4
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.0.11:8080/alljsonresult",
                (String response) -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        if (listOfResponses != null) {
                            listOfResponses.clear();
                        }
                        listOfResponses = new ArrayList<>(Arrays.asList(mapper.readValue(response, SensorBay[].class)));
                        //Log.d("ML",listOfResponses.get(0).getMap().get(44).toString());
                        populateLists(listOfResponses);

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
                    CharSequence text = "Failed to connect to the server! Using machine learning.";
                    startMachineLearning();
                    printToast(getApplicationContext(), text, Toast.LENGTH_LONG);
                }
        );
        requestQueue.add(stringRequest);
    }

    //This method populates the arrays in the SensorBay object.
    //One array refers to time and the other array refers to status at that particular time
    //Logic:
    //Get list of all timings that has been used
    //parse the timings to get time in minutes and get the status
    //populate relevant parts of the arrays
    //convert every element in odd position to mintues
    //get the corresponding next element and place both of them in the map
    private void populateLists(List<SensorBay> listOfResponses) {
        for (SensorBay parkingBay : listOfResponses) {
            for (int j = 0; j < parkingBay.getTimeDateOfUsage().size(); j = j + 2) {
                Date date = null;
                try {
                    if(parkingBay.getTimeDateOfUsage().get(j).length() <=6){
                        date = new SimpleDateFormat("HH:mm").parse(parkingBay.getTimeDateOfUsage().get(j));
                    }else{
                        date = new SimpleDateFormat("dd/MM/yy HH:mm:ss").parse(parkingBay.getTimeDateOfUsage().get(j));
                    }
                } catch (ParseException e) {
                    printToast(getApplicationContext(), "error parsing date", Toast.LENGTH_SHORT);
                }
                String newString = new SimpleDateFormat("HH:mm").format(date);
                String[] splitTime = newString.split(":");
                int convertedToMinutes = (Integer.parseInt(splitTime[0]) * 60) + (Integer.parseInt(splitTime[1]));
                int currentIteration = j + 1;
                int status = Integer.parseInt(parkingBay.getTimeDateOfUsage().get(currentIteration));
                if (status == 1) {
                    parkingBay.statusYAxis[convertedToMinutes] = 1;
                }
            }
        }
    }

    private void startMachineLearning() {
        MachineLearningBay ml = new MachineLearningBay(getApplicationContext(),googleMap ,listOfResponses);
        ml.execute();
    }


    public static void printToast(Context context, CharSequence text, int duration) {
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public  static float getCurrentTime() {
        Calendar calandar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        //Log.d("time", simpleDateFormat.format(calandar.getTime()) + "Time is <-");

        String[] time = simpleDateFormat.format(calandar.getTime()).split(":");
        int hour = Integer.parseInt(time[0]) * 60;
        float currentTime = hour + Integer.parseInt(time[1]);

        return currentTime;
    }

}
