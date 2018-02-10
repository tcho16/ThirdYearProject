package com.example.tarikh.myapplication;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Model.SensorBay;

import static HelperFunctions.HelperFunction.populateLists;
import static HelperFunctions.HelperFunction.printToast;

//import android.location.LocationListener;


public class ParkingBayMain extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private RequestQueue requestQueue;
    private GoogleMap googleMap;
    private ArrayList<MarkerOptions> markers;
    private List<SensorBay> listOfResponses;
    private EditText location;
    LocationManager lm;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        setUpMap();
        if (listOfResponses.size() != 0) {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                updateMap(listOfResponses);
            } else {
                startMachineLearning();
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(51.514471, -0.110893)).zoom(9).build();
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            }

        }
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
        googleMap.setOnMarkerClickListener(this);
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(this);
        markers = new ArrayList<>();
        listOfResponses = new ArrayList<>();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        location = findViewById(R.id.addressLookUp);


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);

        //Loading data if present
        if (PreferenceManager.getDefaultSharedPreferences(this).contains("listOfSavedBays")) {
            listOfResponses = SaveRetrieveData.loadData(this);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SaveRetrieveData.saveData(this, listOfResponses);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutAppItemID:
                Intent i = new Intent(this, AboutApp.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            printToast(getApplicationContext(), "Turn on the internet. Using machine learning on the bays.", Toast.LENGTH_LONG);
            startMachineLearning();
        }
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
                    CharSequence text = "Connection error to server. Using machine learning.";
                    startMachineLearning();
                    printToast(getApplicationContext(), text, Toast.LENGTH_LONG);
                }
        );
        requestQueue.add(stringRequest);
    }


    private void startMachineLearning() {
        googleMap.clear();
        MachineLearningBay ml = new MachineLearningBay(getApplicationContext(), googleMap, listOfResponses);
        ml.execute();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("Distance", "marker was clicked.");
        double longtitude = marker.getPosition().longitude;
        double latitude = marker.getPosition().latitude;


        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d("Distance", "before checks");
        //Check if connection is active and check is gps is enabled
        if (null != activeNetwork && true == gpsEnabled) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            try {
                Task location = fusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Location location1 = (Location) task.getResult();
                        //FETCH RESULTS FROM GOOGLE DISTANCE API
                        fetchResults(longtitude, latitude, location1.getLatitude(), location1.getLongitude());
                    } else {
                        Log.d("Distance", "in else block");
                    }
                });
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            printToast(getApplicationContext(), "Turn on wifi and gps to use routing feature", Toast.LENGTH_LONG);
        }
        return false;
    }

    private void fetchResults(double longtitude, double latitude, double userLatit, double userLong) {
        Log.d("Distance", "In fetching results");
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + userLatit + "," + userLong + "&destination=" + latitude + "," + longtitude + "&key=AIzaSyANQCpWEeO_jtzc1voVxb-Zh1j3z42vKFQ";
        Log.d("Distance", url);
        StringRequest requestGoogle = new StringRequest(Request.Method.GET, url,
                (String response) -> {
                    Log.d("Distance", response);
                    ParsingDistanceResponse distance = new ParsingDistanceResponse(response, getApplicationContext(), googleMap);
                    distance.execute();
                }, (VolleyError) -> {
            printToast(getApplicationContext(), "Error occurred.", Toast.LENGTH_LONG);
        }
        );
        requestQueue.add(requestGoogle);
    }
}
