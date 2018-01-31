package com.example.tarikh.myapplication;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import Model.SensorBay;

import static com.example.tarikh.myapplication.ParkingBayMain.getCurrentTime;

//This class is responsible for the Machine learning. It it using
//the logistic regression algorithm in combination with the
//stochastic gradient descent algorithm for training.
public class MachineLearningBay extends AsyncTask<Void, Void, Void> {
    private List<SensorBay> listOfSensor;
    GoogleMap googleMap;
    Context ctx;

    public MachineLearningBay(Context ctx, GoogleMap map, List<SensorBay> res) {
        listOfSensor = res;
        this.ctx = ctx;
        this.googleMap = map;
    }

    private void calculateWeightsAndCoefficient(){
        for (SensorBay parkingBay:listOfSensor) {
            parkingBay.betaOne = 0.0f;
            parkingBay.betaZero = 0.0f;

            float output = 0.0f;
            float prediction = 0.0f;

            int epoch = 0;
            float alpha = 0.0000003f;

            //Calculating prediction
            while(epoch < 3){
                int i = 1;
                while(i < parkingBay.timeXAxis.length){

                    output = (float) parkingBay.betaZero + (parkingBay.betaOne * parkingBay.timeXAxis[i]);
                    prediction = (float) (1/(1+ Math.exp(-output)));

                    //Refining Coefficients
                    parkingBay.betaZero = parkingBay.betaZero + alpha  * (parkingBay.statusYAxis[i] - prediction) * prediction * (1-prediction) * 1.00f;
                    parkingBay.betaOne =(float) (parkingBay.betaOne + alpha * (parkingBay.statusYAxis[i] - prediction) * prediction * (1 - prediction) * parkingBay.timeXAxis[i]);
                    i++;
                }
                epoch++;
            }

        }
    }

    public void updateMap(){
        float currentTime = getCurrentTime();

        if (0 != listOfSensor.size()) {
            googleMap.clear();
            for (SensorBay parkingBay : listOfSensor) {
                double prediction = 1 / (1 + Math.exp(-(parkingBay.betaZero + parkingBay.betaOne * currentTime)));

                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(
                                Double.parseDouble(parkingBay.getLatitude()), Double.parseDouble(parkingBay.getLongitude())
                        ));
                Log.d("MLL",prediction + "<-- Prediction for: "+ parkingBay.get_id());
                if (prediction >= 0.8) {
                    //occupied
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    marker.title("Occupied");
                } else {
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    marker.title("Vacant");
                }

                googleMap.addMarker(marker);
            }
        } else {
            ParkingBayMain.printToast(ctx, "No saved data.", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {

        calculateWeightsAndCoefficient();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d("ML","EXECUTED MACHINE LEARNING");
        updateMap();
    }
}
