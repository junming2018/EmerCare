package com.example.emercare.find;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.emercare.BuildConfig;
import com.example.emercare.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class FindMap extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener {
    private View mapView;
    private GoogleMap googleMap;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private MaterialSearchBar materialSearchBar;
    private LatLng latLng;
    private Marker marker;
    private double latitude, longitude;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_map);

        ImageView imgBack = findViewById(R.id.find_map_img_back);
        imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        if (isNetworkConnected()) {

            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.find_map_fragment_map);
            if (supportMapFragment != null) {
                supportMapFragment.getMapAsync(this);
                mapView = supportMapFragment.getView();
            }

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(FindMap.this);

            Places.initialize(FindMap.this, BuildConfig.GMP_API_KEY);
            placesClient = Places.createClient(this);

            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

            materialSearchBar = findViewById(R.id.find_map_search_place);
            materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {
                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    startSearch(text.toString(), true, null, true);
                }

                @Override
                public void onButtonClicked(int buttonCode) {
                }
            });

            materialSearchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    FindAutocompletePredictionsRequest findAutocompletePredictionsRequest = FindAutocompletePredictionsRequest.builder()
                            .setSessionToken(token).setQuery(charSequence.toString()).build();

                    placesClient.findAutocompletePredictions(findAutocompletePredictionsRequest).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null) {
                                predictionList = predictionsResponse.getAutocompletePredictions();

                                List<String> suggestionsList = new ArrayList<>();
                                for (int i3 = 0; i3 < predictionList.size(); i3++) {
                                    AutocompletePrediction prediction = predictionList.get(i3);
                                    suggestionsList.add(prediction.getFullText(null).toString());
                                }
                                materialSearchBar.updateLastSuggestions(suggestionsList);
                                if (!materialSearchBar.isSuggestionsVisible()) {
                                    materialSearchBar.showSuggestionsList();
                                }
                            }
                        } else {
                            Toast.makeText(FindMap.this, "Prediction fetching task is unsuccessful!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (TextUtils.isEmpty(materialSearchBar.getText().trim())) {
                        if (materialSearchBar.isSuggestionsVisible()) {
                            materialSearchBar.clearSuggestions();
                        }
                        materialSearchBar.setPlaceHolder("Select a Please");
                    }
                }
            });

            materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
                @Override
                public void OnItemClickListener(int position, View v) {
                    if (position >= predictionList.size()) {
                        return;
                    }
                    AutocompletePrediction selectedPrediction = predictionList.get(position);

                    String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                    materialSearchBar.setText(suggestion);
                    new Handler().postDelayed(() -> materialSearchBar.clearSuggestions(), 2000);

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }

                    String placeId = selectedPrediction.getPlaceId();
                    List<Place.Field> placeFields = Collections.singletonList(Place.Field.LAT_LNG);
                    FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                    placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(fetchPlaceResponse -> {
                        Place place = fetchPlaceResponse.getPlace();
                        LatLng latLngOfPlace = place.getLatLng();
                        if (latLngOfPlace != null) {
                            latitude = latLngOfPlace.latitude;
                            longitude = latLngOfPlace.longitude;
                            moveMarkerCamera(latLngOfPlace);
                        }
                    }).addOnFailureListener(e -> {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            apiException.printStackTrace();
                        }
                    });
                }

                @Override
                public void OnItemDeleteListener(int position, View v) {
                }
            });

            Button btnConfirm = findViewById(R.id.find_map_btn_confirm);
            btnConfirm.setOnClickListener(view -> {
                if (TextUtils.isEmpty(materialSearchBar.getText().trim())) {
                    Toast.makeText(FindMap.this, "Please select a location!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("Latitude", latitude);
                    intent.putExtra("Longitude", longitude);
                    setResult(1, intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        } else {
            ConstraintLayout clMap = findViewById(R.id.find_map_cl_map);
            clMap.setVisibility(View.INVISIBLE);

            LinearLayout llBtn = findViewById(R.id.find_map_ll_btn);
            llBtn.setVisibility(View.INVISIBLE);

            CardView cvNo = findViewById(R.id.find_map_cv_no);
            cvNo.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 270);
        }
        requestCurrentLocation();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(FindMap.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(FindMap.this, locationSettingsResponse -> {
            if (ContextCompat.checkSelfPermission(FindMap.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(FindMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            } else {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(FindMap.this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(FindMap.this, 51);
                } catch (IntentSender.SendIntentException sendIntentException) {
                    sendIntentException.printStackTrace();
                }
            }
        });

        this.googleMap.setOnMyLocationButtonClickListener(() -> {
            if (lastKnownLocation == null) {
                getDeviceLocation();
            } else {
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();
                refreshSearchBar(latitude, longitude);
                moveMarkerCamera(new LatLng(latitude, longitude));
            }
            return false;
        });
        this.googleMap.setOnMapClickListener(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (requestCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    public void requestCurrentLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    @SuppressWarnings("ConstantConditions")
    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(FindMap.this);
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    lastKnownLocation = task.getResult();
                    if (lastKnownLocation == null) {
                        latitude = 37.419857;
                        longitude = -122.078827;
                    } else {
                        latitude = lastKnownLocation.getLatitude();
                        longitude = lastKnownLocation.getLongitude();
                    }
                    latLng = new LatLng(latitude, longitude);
                    if (lastKnownLocation != null) {
                        moveMarkerCamera(latLng);
                    } else {
                        requestCurrentLocation();
                        locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                if (locationResult == null) {
                                    return;
                                }
                                lastKnownLocation = locationResult.getLastLocation();
                                moveMarkerCamera(latLng);
                                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                            }
                        };
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                    refreshSearchBar(latitude, longitude);
                } else {
                    Toast.makeText(FindMap.this, "Unable to get the last location!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void refreshSearchBar(double lat, double lng) {
        Geocoder geocoder = new Geocoder(FindMap.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            String address = addresses.get(0).getAddressLine(0);
            materialSearchBar.setPlaceHolder(address);
            materialSearchBar.setText(address);
            new Handler().postDelayed(() -> materialSearchBar.clearSuggestions(), 2000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveMarkerCamera(LatLng latLng) {
        if (marker != null) {
            marker.remove();
        }
        marker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Selected Location")
        );
        googleMap.setOnMarkerDragListener(this);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        try {
            latitude = latLng.latitude;
            longitude = latLng.longitude;
            refreshSearchBar(latitude, longitude);
            moveMarkerCamera(latLng);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        latLng = new LatLng(latitude, longitude);
        refreshSearchBar(latitude, longitude);
        moveMarkerCamera(latLng);
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