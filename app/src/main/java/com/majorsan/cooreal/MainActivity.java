package com.majorsan.cooreal;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.AppCompatTextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationCallback;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private AppCompatTextView latitudeOutputLabel, longitudeOutputLabel,
    altitudeOutputLabel, deviceMovementStatusOutputLabel, deviceVelocityOutputLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        latitudeOutputLabel = findViewById(R.id.latitudeOutputLabel);
        latitudeOutputLabel.setText("Unknown");

        longitudeOutputLabel = findViewById(R.id.longitudeOutputLabel);
        longitudeOutputLabel.setText("Unknown");

        altitudeOutputLabel = findViewById(R.id.altitudeOutputLabel);
        altitudeOutputLabel.setText("Unknown");

        deviceMovementStatusOutputLabel = findViewById(R.id.deviceMovementStatusOutputLabel);
        deviceMovementStatusOutputLabel.setText("Unknown");

        deviceVelocityOutputLabel = findViewById(R.id.deviceVelocityOutputLabel);
        deviceVelocityOutputLabel.setText("Unknown");
        String latitude = " °N";
        String longitude = " °E";
        String altitude = " km";
        String velocity = " m/s";
    }

}
