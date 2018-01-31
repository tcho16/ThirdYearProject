package com.example.tarikh.myapplication;


import java.util.List;

import Model.SensorBay;

//This class is responsible for the Machine learning. It it using
//the logistic regression algorithm in combination with the
//stochastic gradient descent algorithm for training.
public class MachineLearningBay {
    private List<SensorBay> listOfSensor;



    public MachineLearningBay(List<SensorBay> res) {
        listOfSensor = res;
    }

    public void calculateMachineLearning(){
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


}
