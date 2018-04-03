package com.westminster.tarikh.parkingbaysensor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Model.SensorBay;

import static HelperFunctions.HelperFunction.populateLists;
import static HelperFunctions.HelperFunction.printToast;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{


    static final double LondonLat = 51.514471;
    static final double LondonLon = -0.110893;

    private RequestQueue requestQueue;
    private boolean connectionEstablished;
    public GoogleMap googleMap;
    private ArrayList<MarkerOptions> markers;
    private List<SensorBay> listOfResponses;
    private EditText location;
    LocationManager lm;
    private FusedLocationProviderClient fusedLocationProviderClient;
    GMap gMap;
    TextView statusOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);
        markers = new ArrayList<>();
        location = findViewById(R.id.addressLookUp);
        statusOutput = findViewById(R.id.textViewServiceOutput);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        checkConnectionToServer();


        //Loading data if present
        if (PreferenceManager.getDefaultSharedPreferences(this).contains("listOfSavedBays")) {
            listOfResponses = SaveRetrieveData.loadData(this);

        }
        gMap = new GMap(googleMap, getMarkers(), getApplicationContext());
    }


    public ArrayList<MarkerOptions> getMarkers() {
        return markers;
    }

    @Override
    public void onPause() {
        super.onPause();
        SaveRetrieveData.saveData(this, listOfResponses);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setUpMap();
        gMap = new GMap(googleMap, getMarkers(), getApplicationContext());
        if (listOfResponses.size() != 0) {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            Log.d("state", String.valueOf(connectionEstablished));
            Log.d("state", "v:" + String.valueOf(null != activeNetwork));
            if (null != activeNetwork && connectionEstablished) {
                statusOutput.setText("Status: Live");
                gMap.updateMap(googleMap, listOfResponses);
            } else {
                startMachineLearning();
                gMap.updateCameraPos(LondonLat, LondonLon, 9);
            }
        }

    }

    private void checkConnectionToServer() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.43.49:8080/index.html",
                (String response) -> {
                    Log.d("state", "ttt");
                    this.connectionEstablished = true;
                },
                (VolleyError error) -> {
                    Log.d("state", "fff");
                    this.connectionEstablished = false;
                }
        );
        requestQueue.add(stringRequest);
    }



    public void setUpMap() {
        googleMap.setTrafficEnabled(false);
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

        googleMap.setOnMarkerClickListener(this);
        googleMap.setMyLocationEnabled(true);
    }






    //Method is called when search location button is clicked
    public void searchLocationButtonOnClick(View view) {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {
            LocationLatLong loc = new LocationLatLong(String.valueOf(location.getText()), getApplicationContext());
            try {
                Address address = loc.getAddress();
                updateCameraPosition(address.getLatitude(), address.getLongitude());
            } catch (IndexOutOfBoundsException e) {
                e.getMessage();
                printToast(getApplicationContext(), "Incorrect address supplied", Toast.LENGTH_SHORT);
            }
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

    //Method gets called when user wants to search for a bay
    public void callTheService(View view) {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {
            sendRequestToServer();
        } else {
            printToast(getApplicationContext(), "Turn on the internet. Using machine learning on the bays.", Toast.LENGTH_LONG);
            startMachineLearning();
        }
    }

    private void startMachineLearning() {
        statusOutput.setText("Status: Offline");
        KNNML ml = new KNNML(getApplicationContext(), googleMap, listOfResponses, gMap);
        ml.execute();
    }

    private void sendRequestToServer() {
        // http://10.100.150.208:8080/alljsonresult <-- uni IPv4
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.43.49:8080/alljsonresult",
                (String response) -> {

                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        if (listOfResponses != null) {
                            listOfResponses.clear();
                        }
                        listOfResponses = new ArrayList<>(Arrays.asList(mapper.readValue(response, SensorBay[].class)));
                        populateLists(listOfResponses);

                        statusOutput.setText("Status: Live");
                        googleMap.clear();
                        GMap gMap = new GMap(googleMap, markers, getApplicationContext());
                        gMap.updateMap(googleMap, listOfResponses);
                        //updateMap(listOfResponses);

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
                    CharSequence text = "Connection error to server. Using machine learning.";
                    startMachineLearning();
                    printToast(getApplicationContext(), text, Toast.LENGTH_LONG);
                }
        );
        requestQueue.add(stringRequest);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        double longtitude = marker.getPosition().longitude;
        double latitude = marker.getPosition().latitude;
        Log.d("Distance", "clicked on marker");


        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //Check if connection is active and check is gps is enabled
        if (null != activeNetwork && true == gpsEnabled) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            Log.d("Distance", "after location");
            try {
                Task location = fusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Distance", "successful location");
                        Location location1 = (Location) task.getResult();
                        gMap.drawRoute(longtitude, latitude, location1.getLatitude(), location1.getLongitude());
                    } else {
                        Log.d("Distance", "in else block");
                    }
                });
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else if (!gpsEnabled) {
            printToast(getApplicationContext(), "Turn on GPS to use this feature", Toast.LENGTH_SHORT);
        } else {
            printToast(getApplicationContext(), "Turn on internet and gps to use routing feature", Toast.LENGTH_LONG);
        }
        return false;
    }
}
