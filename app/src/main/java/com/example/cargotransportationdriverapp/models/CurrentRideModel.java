package com.example.cargotransportationdriverapp.models;

public class CurrentRideModel {

    private String userId, rideId;

    public CurrentRideModel() {
    }

    public CurrentRideModel(String userId, String rideId) {
        this.userId = userId;
        this.rideId = rideId;
    }

    public String getUserId() {
        return userId;
    }

    public String getRideId() {
        return rideId;
    }
}
