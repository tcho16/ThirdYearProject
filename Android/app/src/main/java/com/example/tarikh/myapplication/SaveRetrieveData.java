package com.example.tarikh.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Model.SensorBay;

import static HelperFunctions.HelperFunction.printToast;

public class SaveRetrieveData {

    public static void saveData(Context ctx, List<SensorBay> listOfResponses) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = pref.edit();
        ObjectMapper mapper = new ObjectMapper();
        String jsonList = "";
        try {
            jsonList = mapper.writeValueAsString(listOfResponses).toString();
            Log.d("SAVE", "SAVING: " + jsonList);
            edit.putString("listOfSavedBays", jsonList);
            edit.commit();
        } catch (JsonProcessingException e) {
            printToast(ctx, "Error saving data", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
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
        Log.d("SAVE","LOADING: " + listOfBays);
        ObjectMapper mapper = new ObjectMapper();

            try {
                returnSensorBayList = new ArrayList<>(Arrays.asList(mapper.readValue(listOfBays, SensorBay[].class)));
                printToast(ctx, "Successfully loaded data", Toast.LENGTH_SHORT);
            } catch (IOException e) {
                printToast(ctx,"No saved data to load.", Toast.LENGTH_SHORT);
                e.printStackTrace();
            }

        return returnSensorBayList;
    }
}
