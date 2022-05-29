package com.example.emercare.sign;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.emercare.MainActivity;
import com.example.emercare.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class SignSplash extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_splash);

        if (isGMSAvailable()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SignSplash.this);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(SignSplash.this, MainActivity.class));
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }, 1500);
                }
            };
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isGMSAvailable()) {
            if (fusedLocationProviderClient != null) {
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            }
        }
    }

    @SuppressLint("MissingPermission")
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onResume() {
        super.onResume();

        if (isGMSAvailable()) {
            LocationSettingsRequest.Builder locationSettingBuilder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            SettingsClient settingsClient = LocationServices.getSettingsClient(SignSplash.this);

            Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingBuilder.build());
            task.addOnSuccessListener(SignSplash.this, locationSettingsResponse -> {
                if (ContextCompat.checkSelfPermission(SignSplash.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SignSplash.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                } else {
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }
            }).addOnFailureListener(SignSplash.this, e -> {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        resolvableApiException.startResolutionForResult(SignSplash.this, 0x1);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
            });
        }
    }

    public boolean isGMSAvailable() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(SignSplash.this);
        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(SignSplash.this, available, 9001);
            Objects.requireNonNull(dialog).show();
        } else {
            Toast.makeText(this, "You cannot make any Google requests!", Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return false;
    }
}