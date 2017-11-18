package com.example.tarikh.myapplication;

import android.app.DownloadManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    Button serviceButton;
    TextView textViewService;
    SensorResponse responseJson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        textViewService = findViewById(R.id.textViewServiceOutput);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void callTheService(View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.0.11:8080/alljsonresult",
                (String response) -> {
                    try{
                        ObjectMapper mapper = new ObjectMapper();
                        responseJson = mapper.readValue(response,SensorResponse.class);
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                        CharSequence text = "Error parsing JSON";
                        printToast(getApplicationContext(),text,Toast.LENGTH_SHORT);
                    } catch (JsonMappingException e) {
                        CharSequence text = "Error mapping to JSON";
                        printToast(getApplicationContext(),text,Toast.LENGTH_SHORT);
                        e.printStackTrace();
                    } catch (IOException e) {
                        CharSequence text = "Error.";
                        printToast(getApplicationContext(),text,Toast.LENGTH_SHORT);
                        e.printStackTrace();
                    }
                    textViewService.setText(responseJson.toString());
                },
                (VolleyError error) -> {
                    CharSequence text = "Failed to connect to the service!";
                    printToast(getApplicationContext(),text,Toast.LENGTH_SHORT);

                }
        );
        requestQueue.add(stringRequest);
    }

    public void printToast(Context context, CharSequence text, int duration){
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
