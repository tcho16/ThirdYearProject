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

import HelperFunctions.HelperFunction;
import Model.SensorBay;

import static HelperFunctions.HelperFunction.getCurrentTime;
import static HelperFunctions.HelperFunction.printToast;

//This class is responsible for the Machine learning. It it using
//the logistic regression algorithm in combination with the
//stochastic gradient descent algorithm for training.
public class MachineLearningBay extends AsyncTask<Void, Void, Void> {
    private List<SensorBay> listOfSensor;
    GoogleMap googleMap;
    GMap gmap;
    Context ctx;

    public MachineLearningBay(Context ctx, GoogleMap map, List<SensorBay> res, GMap gmap) {
        listOfSensor = res;
        this.ctx = ctx;
        this.googleMap = map;
        this.gmap = gmap;
    }

    public void calculateWeightsAndCoefficient(){
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

    @Override
    protected Void doInBackground(Void... voids) {
        calculateWeightsAndCoefficient();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d("MLL","EXECUTED MACHINE LEARNING");
        gmap.updateMapML(listOfSensor);
        //updateMap();
    }
}
