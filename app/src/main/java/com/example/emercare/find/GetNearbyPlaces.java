package com.example.emercare.find;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emercare.BuildConfig;
import com.example.emercare.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class GetNearbyPlaces extends AsyncTask<Object, String, String> {
    private String place, googlePlaceData, currentAddress;
    double latitude, longitude;

    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    private List<PlaceData> allPlaceList;

    @Override
    protected String doInBackground(Object... objects) {
        latitude = (Double) objects[0];
        longitude = (Double) objects[1];
        currentAddress = (String) objects[2];
        place = (String) objects[3];
        String url = (String) objects[4];
        activity = (Activity) objects[5];

        DownloadUrl downloadUrl = new DownloadUrl();

        try {
            googlePlaceData = downloadUrl.readUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return googlePlaceData;
    }

    private void addNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        allPlaceList = new ArrayList<>();

        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            HashMap<String, String> googleNearbyPlace = nearbyPlacesList.get(i);

            String placeReference = googleNearbyPlace.get("Place Reference");
            String placePhoto;

            if (Objects.requireNonNull(placeReference).equals("No Photo")) {
                placePhoto = "No Photo";
            } else {
                placePhoto = "https://maps.googleapis.com/maps/api/place/photo?"
                        + "photoreference=" + placeReference
                        + "&maxheight=500"
                        + "&maxwidth=500"
                        + "&sensor=true"
                        + "&key=" + BuildConfig.GMP_API_KEY;
            }
            String placeId = googleNearbyPlace.get("Place Id");
            String placeName = googleNearbyPlace.get("Place Name");
            String placeRating = googleNearbyPlace.get("Place Rating");
            String placeStatus = googleNearbyPlace.get("Place Status");

            Location nowLocation = new Location("point A");
            nowLocation.setLatitude(latitude);
            nowLocation.setLongitude(longitude);

            Location placeLocation = new Location("point B");
            placeLocation.setLatitude(Double.parseDouble(Objects.requireNonNull(googleNearbyPlace.get("Place Latitude"))));
            placeLocation.setLongitude(Double.parseDouble(Objects.requireNonNull(googleNearbyPlace.get("Place Longitude"))));

            @SuppressLint("DefaultLocale") String distance = String.format("%.2f", nowLocation.distanceTo(placeLocation) / 1000);

            if (!(Objects.requireNonNull(placeStatus).equals("No Data"))) {
                allPlaceList.add(new PlaceData(currentAddress, placePhoto, place, placeId, placeName, placeRating, distance, placeStatus));
            }
        }
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearbyPlacesList;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(s);
        addNearbyPlaces(nearbyPlacesList);

        RecyclerView recyclerView = activity.findViewById(R.id.find_list_rv_place);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        PlaceAdapter placeAdapter = new PlaceAdapter(allPlaceList, activity);
        if (placeAdapter.getItemCount() == 0) {
            CardView cvNo = activity.findViewById(R.id.find_list_cv_no);
            cvNo.setVisibility(View.VISIBLE);

            TextView tvNo = activity.findViewById(R.id.find_list_tv_no);
            tvNo.setText(activity.getResources().getText(R.string.no_record));
        }
        recyclerView.setAdapter(placeAdapter);
    }
}