package com.example.emercare.find;

public class PlaceData {
    String currentAddress, placePhoto, place, placeId, placeName, placeRating, placeDistance, placeStatus;

    public PlaceData(String currentAddress, String placePhoto, String place, String placeId, String placeName, String placeRating, String placeDistance, String placeStatus) {
        this.currentAddress = currentAddress;
        this.placePhoto = placePhoto;
        this.place = place;
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeRating = placeRating;
        this.placeDistance = placeDistance;
        this.placeStatus = placeStatus;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public String getPlacePhoto() {
        return placePhoto;
    }

    public String getPlace() {
        return place;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceRating() {
        return placeRating;
    }

    public String getPlaceDistance() {
        return placeDistance;
    }

    public String getPlaceStatus() {
        return placeStatus;
    }
}
