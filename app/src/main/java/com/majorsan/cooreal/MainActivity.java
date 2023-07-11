package com.majorsan.cooreal;

import java.lang.Math;

import android.os.Bundle;
import android.os.Looper;
import android.content.IntentSender;
import android.location.Location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity {

    private Location mCurrentLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    protected int REQUEST_CHECK_SETTINGS;

    private String latitude;
    private String longitude;
    private String altitude;
    private String velocity;
    private String accuracy;
    private String bearing;
    private String accuracyMeterPerSec;

    private MaterialTextView latitudeOutputLabel;
    private MaterialTextView longitudeOutputLabel;
    private MaterialTextView altitudeOutputLabel;
    private MaterialTextView deviceMovementStatusOutputLabel;
    private MaterialTextView deviceVelocityOutputLabel;
    private MaterialTextView accuracyOutputLabel;
    private MaterialTextView bearingOutputLabel;
    private MaterialTextView accuracyMeterPerSecOutputLabel;

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    mCurrentLocation = location;
                    boolean deviceMoving = false;
                    latitude = Double.toString(mCurrentLocation.getLatitude()) + " °N";
                    longitude = Double.toString(mCurrentLocation.getLongitude()) + " °E";
                    altitude = Double.toString(Math.round(mCurrentLocation.getAltitude())) + " m";
                    velocity = "0 m/s";
                    accuracy = Double.toString(Math.round(location.getAccuracy()));
                    bearing = Double.toString(Math.round(location.getBearing()));
                    accuracyMeterPerSec = Double.toString(Math.round(location.getSpeedAccuracyMetersPerSecond())) + "m/s";

                    if(mCurrentLocation.getSpeed()>=1.0){
                        velocity = Double.toString(Math.round(mCurrentLocation.getSpeed())) + "m/s";
                        deviceMoving = true;
                        locationRequest.setFastestInterval(1000);
                    }
                    updateUI(latitude, longitude, altitude, deviceMoving, velocity);
                }
            }
        });

        locationRequest = createLocationRequest();
        Task task = createCheckLocationSettingsTask(locationRequest);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                boolean deviceMoving = false;
                if(locationResult == null){
                    return;
                }
                for(Location location: locationResult.getLocations()){
                    latitude = Double.toString(location.getLatitude()) + " °N";
                    longitude = Double.toString(location.getLongitude()) + " °E";
                    altitude = Double.toString(Math.round(location.getAltitude())) + " m";
                    velocity = "0 m/s";
                    accuracy = Double.toString(Math.round(location.getAccuracy()));
                    bearing = Double.toString(Math.round(location.getBearing()));
                    accuracyMeterPerSec = Double.toString(Math.round(location.getSpeedAccuracyMetersPerSecond())) + "m/s";

                    if(mCurrentLocation.getSpeed()>=1.0){
                        velocity = Double.toString(Math.round(location.getSpeed())) + " m/s";
                        deviceMoving = true;
                        locationRequest.setFastestInterval(1000);
                    }
                    updateUI(latitude, longitude, altitude, deviceMoving, velocity);
                }
            }
        };
        startLocationUpdates();
    }

    @NonNull
    protected LocationRequest createLocationRequest(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @NonNull
    protected Task createCheckLocationSettingsTask(LocationRequest locationRequest){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //All location settings are satisfied. Location requests can be
                //initialized here
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException){
                    try{
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                    }catch (IntentSender.SendIntentException sendEx){
                        //ignore the error
                    }
                }
            }
        });
        return task;
    }

    private void startLocationUpdates(){
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void updateUI(String latitudeResult, String longitudeResult, String altitudeResult,
                          Boolean deviceMoving, String velocityResult){
        latitudeOutputLabel.setText(latitudeResult);
        longitudeOutputLabel.setText(longitudeResult);
        altitudeOutputLabel.setText(altitudeResult);
        if(deviceMoving)
            deviceMovementStatusOutputLabel.setText("Yes");
        else
            deviceMovementStatusOutputLabel.setText("No");
        deviceVelocityOutputLabel.setText(velocityResult);
    }
}