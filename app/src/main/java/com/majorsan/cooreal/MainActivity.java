package com.majorsan.cooreal;



import android.os.Bundle;
import androidx.annotation.NonNull;
import android.content.IntentSender;
import android.location.Location;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textview.MaterialTextView;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    protected Location mCurrentLocation;
    protected int REQUEST_CHECK_SETTINGS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final MaterialTextView latitudeOutputLabel = findViewById(R.id.latitudeOutputLabel);
        latitudeOutputLabel.setText("Unknown");

        final MaterialTextView longitudeOutputLabel = findViewById(R.id.longitudeOutputLabel);
        longitudeOutputLabel.setText("Unknown");

        final MaterialTextView altitudeOutputLabel = findViewById(R.id.altitudeOutputLabel);
        altitudeOutputLabel.setText("Unknown");

        final MaterialTextView deviceMovementStatusOutputLabel = findViewById(R.id.deviceMovementStatusOutputLabel);
        deviceMovementStatusOutputLabel.setText("Unknown");

        final MaterialTextView deviceVelocityOutputLabel = findViewById(R.id.deviceVelocityOutputLabel);
        deviceVelocityOutputLabel.setText("Unknown");

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    mCurrentLocation = location;
                    String latitude = Double.toString(mCurrentLocation.getLatitude()) + " °N";
                    String longitude = Double.toString(mCurrentLocation.getLongitude()) + " °E";
                    String altitude = Double.toString(mCurrentLocation.getAltitude()) + " m";
                    latitudeOutputLabel.setText(latitude);
                    longitudeOutputLabel.setText(longitude);
                    altitudeOutputLabel.setText(altitude);
                    if(mCurrentLocation.hasSpeed()){
                        deviceMovementStatusOutputLabel.setText("Yes");
                        String velocity = Double.toString(mCurrentLocation.getSpeed()) + " m/s";
                        deviceVelocityOutputLabel.setText(velocity);
                    }
                }
            }
        });

        /*try {
        String latitude = Double.toString(mCurrentLocation.getLatitude()) + " °N";
        String longitude = Double.toString(mCurrentLocation.getLongitude()) + " °E";
        String altitude = Double.toString(mCurrentLocation.getAltitude()) + " m";
        latitudeOutputLabel.setText(latitude);
        longitudeOutputLabel.setText(longitude);
        altitudeOutputLabel.setText(altitude);
        if(mCurrentLocation.hasSpeed()){
            deviceMovementStatusOutputLabel.setText("Yes");
            String velocity = Double.toString(mCurrentLocation.getSpeed()) + " m/s";
            deviceVelocityOutputLabel.setText(velocity);
        }
        }catch (NullPointerException e){
            Toast.makeText(getApplicationContext(),  "No Location", Toast.LENGTH_SHORT).show();
        }*/
        LocationRequest locationRequest = createLocationRequest();
        Task task = createCheckLocationSettingsTask(locationRequest);
    }

    @NonNull
    protected LocationRequest createLocationRequest(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        //locationRequest.setFastestInterval(5000);
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
}