package com.example.tarikh.myapplication;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import HelperFunctions.HelperFunction;
import Model.SensorBay;

import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;


public class HelperFunctionTest {




    @Test
    public void shouldThrowAnExceptionWhenInvalidTimeIsPassed(){

              List<SensorBay> dummyList = new ArrayList<>();
              SensorBay sb = new SensorBay();
              ArrayList<String> usages = new ArrayList<>();
              usages.add("999999:00");
              usages.add("1");
              usages.add("17:00");
              usages.add("0");
              sb.setTimeDateOfUsage(usages);
              dummyList.add(sb);

              HelperFunction.populateLists(dummyList);
    }

    @Test
    public void shouldCorrectlyPopulateTimeArrayCorrectly(){
        List<SensorBay> dummyList = new ArrayList<>();
        SensorBay sb = new SensorBay();
        ArrayList<String> usages = new ArrayList<>();
        usages.add("03:00");
        usages.add("1");
        usages.add("17:00");
        usages.add("0");
        sb.setTimeDateOfUsage(usages);
        dummyList.add(sb);

        HelperFunction.populateLists(dummyList);

        int expectfirstElementShouldContain180 = 180;
        int actualFirstElement = dummyList.get(0).timeXAxis[180];

        assertTrue(expectfirstElementShouldContain180 == actualFirstElement);
    }

    @Test
    public void shouldCorrectlyPopulateStatusArrayCorrectly(){
        List<SensorBay> dummyList = new ArrayList<>();
        SensorBay sb = new SensorBay();
        ArrayList<String> usages = new ArrayList<>();
        usages.add("03:00");
        usages.add("1");
        usages.add("17:00");
        usages.add("0");
        sb.setTimeDateOfUsage(usages);
        dummyList.add(sb);

        HelperFunction.populateLists(dummyList);
        int expect180ElementShouldContainAOne = 1;
        int actualFirstStatusElement = dummyList.get(0).statusYAxis[180];

        assertSame(expect180ElementShouldContainAOne,actualFirstStatusElement);
    }

    @Test
    public void shouldReturnCorrectMinutesFromTime(){
        Calendar calandar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String[] time = simpleDateFormat.format(calandar.getTime()).split(":");
        int hour = Integer.parseInt(time[0]) * 60;
        float expectedTime = hour + Integer.parseInt(time[1]);

        float actualTime = HelperFunction.getCurrentTime();

        assertTrue(expectedTime == actualTime);
    }
}
