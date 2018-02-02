package com.example.tarikh.myapplication;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import Model.SensorBay;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MachineLearningTest {

    List<SensorBay> dummySensorBay;
    private final int SIX_FORTY_AM = 400;
    private final int NINE_PM = 1260;

    @Before
    public void constructListOfSensorBays(){
        dummySensorBay = new ArrayList<>();
        SensorBay sBay = new SensorBay();

        for (int h = 1; h < 1440; h++) {
            sBay.timeXAxis[h-1] = h;
        }

        for (int i = 0; i < sBay.timeXAxis.length; i++) {
            if(i<237){
                sBay.statusYAxis[i] = 0;

            }else if(i >=237 && i < 267){
                sBay.statusYAxis[i] = 1;
            }else if(i >= 267 && i <532){
                sBay.statusYAxis[i] = 0;
            }else if(i>=532 && i< 591){
                sBay.statusYAxis[i] = 1;
            }else if(i >= 591 && i <768){
                sBay.statusYAxis[i] = 0;
            }else if(i >= 768 && i<1007){
                sBay.statusYAxis[i] = 1;
            }else if(i >= 1007 && i < 1034){
                sBay.statusYAxis[i] = 0;
            }else if(i>=1034 && i < 1211){
                sBay.statusYAxis[i] = 1;
            }else if(i>=1211 && i < 1240){
                sBay.statusYAxis[i] = 0;
            }else if(i >= 1240 && i < sBay.timeXAxis.length){
                sBay.statusYAxis[i] = 1;
            }
        }

        dummySensorBay.add(sBay);


    }

    @Test
    public void shouldProvideAccurateBetaOneWeight(){

        MachineLearningBay ml = new MachineLearningBay(null,null, dummySensorBay);
        ml.calculateWeightsAndCoefficient();

        float expectedBetaOne = 0.001904f;
        float actualBetaOne = Float.parseFloat(String.format("%.7f", dummySensorBay.get(0).betaOne));

        assertTrue(expectedBetaOne == actualBetaOne);
        //assertSame(expectedBetaOne,actualBetaOne);
    }

    @Test
    public void shouldProvideAccurateBetaZeroWeight(){
        MachineLearningBay ml = new MachineLearningBay(null,null, dummySensorBay);
        ml.calculateWeightsAndCoefficient();

        float expectedZeroOne = -3.03E-5f;
        float actualZeroOne = Float.parseFloat(String.format("%.7f", dummySensorBay.get(0).betaZero));

        assertTrue(expectedZeroOne == actualZeroOne);
    }

    @Test
    public void shouldProvideAccuratePredictionBasedOnMorningTime(){
        MachineLearningBay ml = new MachineLearningBay(null,null, dummySensorBay);
        ml.calculateWeightsAndCoefficient();
        double prediction = 1 / (1 + Math.exp(-(dummySensorBay.get(0).betaZero + dummySensorBay.get(0).betaOne * SIX_FORTY_AM)));
        assertTrue(prediction < 0.8);
    }

    @Test
    public void shouldProvideAccuratePredictionBasedOnEveningTime(){
        MachineLearningBay ml = new MachineLearningBay(null,null, dummySensorBay);
        ml.calculateWeightsAndCoefficient();
        double prediction = 1 / (1 + Math.exp(-(dummySensorBay.get(0).betaZero + dummySensorBay.get(0).betaOne * NINE_PM)));
        assertTrue(prediction > 0.8);
    }

}