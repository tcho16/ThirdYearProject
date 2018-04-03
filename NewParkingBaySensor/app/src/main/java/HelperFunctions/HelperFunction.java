package HelperFunctions;

import android.content.Context;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Interfaces.TimeHelper;
import Model.Bays;
import Model.SensorBay;

public class HelperFunction{

    private static TimeHelper timeHelper = new TimeHelperImpl();

    public static void loadTime(TimeHelper impl){
        timeHelper = impl;
    }

    public static Calendar getInstance(){
        return timeHelper.getTiming();
    }

    public static float getCurrentTime() {
        Calendar calandar = HelperFunction.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        String[] time = simpleDateFormat.format(calandar.getTime()).split(":");
        int hour = Integer.parseInt(time[0]) * 60;
        float currentTime = hour + Integer.parseInt(time[1]);

        return currentTime;
    }

    public static void printToast(Context context, CharSequence text, int duration) {
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //This method populates the arrays in the SensorBay object.
    //One array refers to time and the other array refers to status at that particular time
    //Logic:
    //Get list of all timings that has been used
    //parse the timings to get time in minutes and get the status
    //populate relevant parts of the arrays
    //convert every element in odd position to mintues
    //get the corresponding next element and place both of them in the map
    public static void populateLists(List<SensorBay> listOfResponses) {
        for (SensorBay parkingBay : listOfResponses) {
            for (int j = 0; j < parkingBay.getTimeDateOfUsage().size(); j = j + 2) {
                Date date = null;
                try {
                    if(parkingBay.getTimeDateOfUsage().get(j).length() <=6){
                        date = new SimpleDateFormat("HH:mm").parse(parkingBay.getTimeDateOfUsage().get(j));
                    }else{
                        date = new SimpleDateFormat("dd/MM/yy HH:mm:ss").parse(parkingBay.getTimeDateOfUsage().get(j));
                    }
                    String newString = new SimpleDateFormat("HH:mm").format(date);
                    String[] splitTime = newString.split(":");
                    int convertedToMinutes = (Integer.parseInt(splitTime[0]) * 60) + (Integer.parseInt(splitTime[1]));
                    int currentIteration = j + 1;
                    int status = Integer.parseInt(parkingBay.getTimeDateOfUsage().get(currentIteration));
                    parkingBay.timeUsage.add(new Bays(convertedToMinutes,status));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
