package com.example.tarikh.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Model.SensorBay;

public class SaveRetrieveData {

    public static void saveData(Context ctx, List<SensorBay> listOfResponses) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = pref.edit();
        ObjectMapper mapper = new ObjectMapper();
        String jsonList = "";
        try {
            jsonList = mapper.writeValueAsString(listOfResponses).toString();
        } catch (JsonProcessingException e) {
            MainActivity.printToast(ctx, "Error saving data", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
        edit.putString("listOfSavedBays", jsonList);
        edit.apply();
    }

    public static void clearSharedPreferences(Context ctx){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    public static ArrayList<SensorBay> loadData(Context ctx) {
        ArrayList<SensorBay> returnSensorBayList = new ArrayList<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String listOfBays = preferences.getString("listOfSavedBays", null);
        ObjectMapper mapper = new ObjectMapper();
        if (//listOfResponses.size() == 0 ||
                !listOfBays.equals(null)) {
            try {
                returnSensorBayList = new ArrayList<>(Arrays.asList(mapper.readValue(listOfBays, SensorBay[].class)));
                MainActivity.printToast(ctx, "Successfully loaded data", Toast.LENGTH_SHORT);
            } catch (IOException e) {
                MainActivity.printToast(ctx,"Not saved data to load.", Toast.LENGTH_SHORT);
                e.printStackTrace();
            }

        }
        return returnSensorBayList;
    }
}
