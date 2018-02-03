package com.example.tarikh.myapplication;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import HelperFunctions.HelperFunction;
import HelperFunctions.TimeHelperImpl;
import Interfaces.TimeHelper;
import Model.SensorBay;

import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
    public void shouldReturn180FromThreeAM(){
        Calendar testCal = Calendar.getInstance();
        testCal.set(Calendar.HOUR, 03);
        testCal.set(Calendar.MINUTE, 00);
        testCal.set(Calendar.SECOND,00);


        TimeHelper mockObject = mock(TimeHelperImpl.class);
        HelperFunction.loadTime(mockObject);
        when(mockObject.getTiming()).thenReturn(testCal);

        float actualResult = HelperFunction.getCurrentTime();
        float expectedResult = 180f;

        assertTrue(actualResult == expectedResult);
        }

    @Test
    public void shouldReturn930FOR330PM(){
        Calendar testCal = Calendar.getInstance();
        testCal.set(Calendar.HOUR, 15);
        testCal.set(Calendar.MINUTE, 30);
        testCal.set(Calendar.SECOND,00);


        TimeHelper mockObject = mock(TimeHelperImpl.class);
        HelperFunction.loadTime(mockObject);
        when(mockObject.getTiming()).thenReturn(testCal);

        float actualResult = HelperFunction.getCurrentTime();
        float expectedResult = 930f;

        assertTrue(actualResult == expectedResult);
    }



}
