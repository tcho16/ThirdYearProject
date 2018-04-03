package com.westminster.tarikh.parkingbaysensor;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

public class AboutApp extends AppCompatActivity {
    ImageView imageView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_toolbarSecond);
        setSupportActionBar(myChildToolbar);
        imageView = (ImageView) findViewById(R.id.imageView);

        imageView.setImageResource(R.drawable.screenshot);
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
