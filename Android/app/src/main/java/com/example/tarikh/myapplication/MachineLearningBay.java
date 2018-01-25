package com.example.tarikh.myapplication;


import java.util.List;

//This class is responsible for the Machine learning. It it using
//the logistic regression algorithm in combination with the
//stochastic gradient descent algorithm for training.
public class MachineLearningBay {
    private List<SensorResponse> listOfSensor;



    public MachineLearningBay(List<SensorResponse> res) {
        listOfSensor = res;
    }

    public void calculateMachineLearning(){
        for (SensorResponse parkingBay:listOfSensor) {
            parkingBay.betaOne = 0.0;
            parkingBay.betaZero = 0.0;

        double output = 0.0;
        double prediction = 0.0;

        int epoch = 0;
        double alpha = 0.3;

        //Calculating prediction
            while(epoch < 10){
                int i = 1;
                while(i < parkingBay.timeXAxis.length){

                    output = parkingBay.betaZero + (parkingBay.betaOne * parkingBay.timeXAxis[i]);
                    prediction = (1/(1+ Math.exp(-output)));

                    //Refining Coefficients
                    parkingBay.betaZero = parkingBay.betaZero + alpha  * (parkingBay.statusYAxis[i] - prediction) * prediction * (1-prediction) * 1.00;
                    parkingBay.betaOne = (parkingBay.betaOne + alpha * (parkingBay.statusYAxis[i] - prediction) * prediction * (1 - prediction) * parkingBay.timeXAxis[i]);
                    i++;
                }
                epoch++;
            }







        }
    }


}
