package com.example.emercare.find;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emercare.BuildConfig;
import com.example.emercare.R;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

public class FindList extends AppCompatActivity {
    private String placeType;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_list);

        ImageView imgBack = findViewById(R.id.find_list_img_back);
        imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        if (isNetworkConnected()) {

            Intent intent = getIntent();
            String place = intent.getStringExtra("Place");
            double latitude = intent.getDoubleExtra("Latitude", 0);
            double longitude = intent.getDoubleExtra("Longitude", 0);
            String address = intent.getStringExtra("Address");

            TextView tvPlace = findViewById(R.id.find_list_tv_place);
            tvPlace.setText(place);

            Object[] transferData = new Object[6];

            transferData[0] = latitude;
            transferData[1] = longitude;
            transferData[2] = address;

            switch (place) {
                case "Clinic":
                    placeType = "doctor";
                    break;
                case "Hospital":
                    placeType = "hospital";
                    break;
                case "Police Station":
                    placeType = "police";
                    break;
                case "Fire Station":
                    placeType = "fire_station";
                    break;
            }
            transferData[3] = place;

            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                    + "location=" + latitude + "," + longitude
                    + "&language=en"
                    + "&rankBy=distance"
                    + "&radius=50000"
                    + "&types=" + placeType
                    + "&sensor=true"
                    + "&key=" + BuildConfig.GMP_API_KEY;
            transferData[4] = url;

            Activity activity = this;
            transferData[5] = activity;

            GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
            getNearbyPlaces.execute(transferData);
        } else {
            RecyclerView rvPlace = findViewById(R.id.find_list_rv_place);
            rvPlace.setVisibility(View.INVISIBLE);

            CardView cvNo = findViewById(R.id.find_list_cv_no);
            cvNo.setVisibility(View.VISIBLE);

            TextView tvNo = findViewById(R.id.find_list_tv_no);
            tvNo.setText(getResources().getText(R.string.no_internet));
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}