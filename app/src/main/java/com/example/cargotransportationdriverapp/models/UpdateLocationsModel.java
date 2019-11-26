package com.example.cargotransportationdriverapp.models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class UpdateLocationsModel {

    private String bearingTo, latitude, longitude;

    public UpdateLocationsModel() {
    }

    public UpdateLocationsModel(String bearingTo, String latitude, String longitude) {
        this.bearingTo = bearingTo;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getBearingTo() {
        return bearingTo;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
