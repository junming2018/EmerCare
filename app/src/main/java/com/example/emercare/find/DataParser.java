package com.example.emercare.find;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DataParser {

    private HashMap<String, String> getSingleNearbyPlace(JSONObject googlePlaceJSON) {
        HashMap<String, String> googlePlaceHashMap = new HashMap<>();

        String placeReference = "No Photo";
        String placeId;
        String placeName = "No Data";
        String placeRating = "No Rating Yet";
        String placeStatus = "Not Available";
        String placeLatitude;
        String placeLongitude;

        try {
            if (!(googlePlaceJSON.isNull("photos"))) {
                JSONArray jsonArray = googlePlaceJSON.getJSONArray("photos");
                for (int i = 0; i < jsonArray.length(); i++) {
                    placeReference = ((JSONObject) jsonArray.get(i)).getString("photo_reference");
                }
            }
            placeId = googlePlaceJSON.getString("place_id");

            if (!(googlePlaceJSON.isNull("name"))) {
                placeName = googlePlaceJSON.getString("name");
            }
            if (!(googlePlaceJSON.isNull("rating"))) {
                placeRating = googlePlaceJSON.getString("rating");
            }
            if (!(googlePlaceJSON.isNull("opening_hours"))) {
                placeStatus = String.valueOf(googlePlaceJSON.getJSONObject("opening_hours").getBoolean("open_now"));
            }
            placeLatitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            placeLongitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");

            googlePlaceHashMap.put("Place Reference", placeReference);
            googlePlaceHashMap.put("Place Id", placeId);
            googlePlaceHashMap.put("Place Name", placeName);
            googlePlaceHashMap.put("Place Rating", placeRating);
            googlePlaceHashMap.put("Place Status", placeStatus);
            googlePlaceHashMap.put("Place Latitude", placeLatitude);
            googlePlaceHashMap.put("Place Longitude", placeLongitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return googlePlaceHashMap;
    }

    private List<HashMap<String, String>> getAllNearbyPlaces(JSONArray jsonArray) {
        int counter = jsonArray.length();
        List<HashMap<String, String>> nearbyPlacesList = new ArrayList<>();
        HashMap<String, String> nearbyPlaceHashMap;

        for (int i = 0; i < counter; i++) {
            try {
                nearbyPlaceHashMap = getSingleNearbyPlace((JSONObject) jsonArray.get(i));
                nearbyPlacesList.add(nearbyPlaceHashMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return nearbyPlacesList;
    }

    public List<HashMap<String, String>> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getAllNearbyPlaces(Objects.requireNonNull(jsonArray));
    }
}
