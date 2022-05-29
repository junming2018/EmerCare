package com.example.emercare.find;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.emercare.BuildConfig;
import com.example.emercare.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FindPlace extends AppCompatActivity {
    private String placePhone, placeAddress;
    private Uri placeWebsite;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_place);

        ImageView imgBack = findViewById(R.id.find_place_img_back);
        imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        if (isNetworkConnected()) {

            Intent intent = getIntent();

            String currentAddress = intent.getStringExtra("Current Address");

            String place = intent.getStringExtra("Place");
            TextView tvPlace = findViewById(R.id.find_place_tv_place);
            tvPlace.setText(place);

            String placeName = intent.getStringExtra("Place Name");
            TextView tvName = findViewById(R.id.find_place_tv_name);
            tvName.setText(placeName);

            String placeRating = intent.getStringExtra("Place Rating");
            TextView tvRating = findViewById(R.id.find_place_tv_rating);
            if (placeRating.equals("No Rating Yet")) {
                tvRating.setText(placeRating);
            } else {
                tvRating.setText(placeRating.concat(" / 5.0"));
            }

            String placeId = intent.getStringExtra("Place Id");
            List<Place.Field> placeField = Arrays.asList(Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.OPENING_HOURS);
            FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, placeField);
            Places.initialize(FindPlace.this, BuildConfig.GMP_API_KEY);
            PlacesClient placesClient = Places.createClient(this);
            placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(fetchPlaceResponse -> {
                Place selectedPlace = fetchPlaceResponse.getPlace();

                String placePhoto = intent.getStringExtra("Place Photo");
                ImageView imgPhoto = findViewById(R.id.find_place_img_place);
                if (placePhoto.equals("No Photo")) {
                    imgPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    switch (place) {
                        case "Clinic":
                            imgPhoto.setImageResource(R.drawable.clinic);
                            break;
                        case "Hospital":
                            imgPhoto.setImageResource(R.drawable.hospital);
                            break;
                        case "Police Station":
                            imgPhoto.setImageResource(R.drawable.police_station);
                            break;
                        case "Fire Station":
                            imgPhoto.setImageResource(R.drawable.fire_station);
                            break;
                    }
                } else {
                    PhotoMetadata photoMetadata = Objects.requireNonNull(selectedPlace.getPhotoMetadatas()).get(0);
                    FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                            .setMaxWidth(500)
                            .setMaxHeight(500)
                            .build();
                    placesClient.fetchPhoto(photoRequest).addOnSuccessListener(fetchPhotoResponse -> {
                        Bitmap bitmap = fetchPhotoResponse.getBitmap();
                        imgPhoto.setImageBitmap(bitmap);
                    }).addOnFailureListener(e -> {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            apiException.printStackTrace();
                        }
                    });
                }

                TextView tvAddress = findViewById(R.id.find_place_tv_address);
                placeAddress = selectedPlace.getAddress();
                if (!((placeAddress) == null)) {
                    tvAddress.setText(placeAddress);
                }

                TextView tvPhone = findViewById(R.id.find_place_tv_phone);
                placePhone = selectedPlace.getPhoneNumber();
                if (!(placePhone == null)) {
                    tvPhone.setText(placePhone);
                }

                TextView tvWebsite = findViewById(R.id.find_place_tv_website);
                placeWebsite = selectedPlace.getWebsiteUri();
                if (!(placeWebsite == null)) {
                    tvWebsite.setText(Objects.requireNonNull(placeWebsite).toString());
                }

                if (!(selectedPlace.getOpeningHours() == null)) {
                    try {
                        TextView tvOpening = findViewById(R.id.find_place_tv_hours);
                        tvOpening.setText(Objects.requireNonNull(selectedPlace.getOpeningHours()).getWeekdayText().get(0).concat("\n\n")
                                .concat(Objects.requireNonNull(selectedPlace.getOpeningHours()).getWeekdayText().get(1)).concat("\n\n")
                                .concat(Objects.requireNonNull(selectedPlace.getOpeningHours()).getWeekdayText().get(2)).concat("\n\n")
                                .concat(Objects.requireNonNull(selectedPlace.getOpeningHours()).getWeekdayText().get(3)).concat("\n\n")
                                .concat(Objects.requireNonNull(selectedPlace.getOpeningHours()).getWeekdayText().get(4)).concat("\n\n")
                                .concat(Objects.requireNonNull(selectedPlace.getOpeningHours()).getWeekdayText().get(5)).concat("\n\n")
                                .concat(Objects.requireNonNull(selectedPlace.getOpeningHours()).getWeekdayText().get(6)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(e -> {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    apiException.printStackTrace();
                }
            });

            String placeStatus = intent.getStringExtra("Place Status");
            TextView tvStatus = findViewById(R.id.find_place_tv_status);

            if (placeStatus.equals("true")) {
                String openNow = "Open Now";
                tvStatus.setText(openNow);
                tvStatus.setTextColor(getResources().getColor(R.color.green));
            }

            ImageView imgPhone = findViewById(R.id.find_place_img_phone);
            imgPhone.setOnClickListener(view -> {
                if (!(placePhone == null)) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel: " + placePhone));
                    try {
                        startActivity(callIntent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } catch (Exception e) {
                        Toast.makeText(FindPlace.this, "There is no application can handle this action!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FindPlace.this, "This place has no phone to call!", Toast.LENGTH_SHORT).show();
                }
            });

            ImageView imgAddress = findViewById(R.id.find_place_img_address);
            imgAddress.setOnClickListener(view -> {
                if (!(placeAddress == null)) {
                    String currentAddressForUri = currentAddress.replaceAll("/", "%2F");
                    String placeAddressForUri = placeAddress.replaceAll("/", "%2F");

                    Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + currentAddressForUri + "/" + placeAddressForUri);
                    Intent addressIntent = new Intent(Intent.ACTION_VIEW, uri);
                    addressIntent.setPackage("com.google.android.apps.maps");
                    if (addressIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(addressIntent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        Toast.makeText(FindPlace.this, "There is no application can handle this action!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FindPlace.this, "This place has no address!", Toast.LENGTH_SHORT).show();
                }
            });

            ImageView imgWebsite = findViewById(R.id.find_place_img_website);
            imgWebsite.setOnClickListener(view -> {
                if (!(placeWebsite == null)) {
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, placeWebsite);
                    if (websiteIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(websiteIntent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        Toast.makeText(FindPlace.this, "There is no application can handle this action!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FindPlace.this, "This place has no website!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ScrollView svPlace = findViewById(R.id.find_place_sv_place);
            svPlace.setVisibility(View.INVISIBLE);

            LinearLayout llImg = findViewById(R.id.find_place_ll_img);
            llImg.setVisibility(View.INVISIBLE);

            CardView cvNo = findViewById(R.id.find_place_cv_no);
            cvNo.setVisibility(View.VISIBLE);
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