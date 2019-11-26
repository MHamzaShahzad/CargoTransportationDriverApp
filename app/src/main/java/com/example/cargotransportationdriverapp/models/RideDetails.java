package com.example.cargotransportationdriverapp.models;

import java.io.Serializable;

public class RideDetails implements Serializable {

    public static final String RIDE_DRIVER_ID_REF = "driverId";
    public static final String RIDE_STARTED_AT_REF = "rideStartedAt";
    public static final String RIDE_ENDED_AT_REF = "rideEndedAt";
    public static final String RIDE_FARE_REF = "rideFare";
    public static final String RIDE_COLLECTED_FARE_REF = "collectedRideFare";
    public static final String RIDE_STATUS_REF = "rideStatus";

    private String
            rideId, userId, driverId, vehicle, pickUpAddress,
            dropOffAddress, pickUpLat, pickUpLong, dropOffLat,
            dropOffLng, loadingDuration, unloadingDuration, rideStatus,
            rideDistance, rideCreatedDate, rideStartedAt, rideEndedAt, rideFare, collectedRideFare;

    public RideDetails() {
    }

    public RideDetails(String rideId, String userId, String driverId, String vehicle, String pickUpAddress, String dropOffAddress, String pickUpLat, String pickUpLong, String dropOffLat, String dropOffLng, String loadingDuration, String unloadingDuration, String rideStatus, String rideDistance, String rideCreatedDate, String rideStartedAt, String rideEndedAt, String rideFare, String collectedRideFare) {
        this.rideId = rideId;
        this.userId = userId;
        this.driverId = driverId;
        this.vehicle = vehicle;
        this.pickUpAddress = pickUpAddress;
        this.dropOffAddress = dropOffAddress;
        this.pickUpLat = pickUpLat;
        this.pickUpLong = pickUpLong;
        this.dropOffLat = dropOffLat;
        this.dropOffLng = dropOffLng;
        this.loadingDuration = loadingDuration;
        this.unloadingDuration = unloadingDuration;
        this.rideStatus = rideStatus;
        this.rideDistance = rideDistance;
        this.rideCreatedDate = rideCreatedDate;
        this.rideStartedAt = rideStartedAt;
        this.rideEndedAt = rideEndedAt;
        this.rideFare = rideFare;
        this.collectedRideFare = collectedRideFare;
    }

    public String getRideId() {
        return rideId;
    }

    public String getUserId() {
        return userId;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getVehicle() {
        return vehicle;
    }

    public String getPickUpAddress() {
        return pickUpAddress;
    }

    public String getDropOffAddress() {
        return dropOffAddress;
    }

    public String getPickUpLat() {
        return pickUpLat;
    }

    public String getPickUpLong() {
        return pickUpLong;
    }

    public String getDropOffLat() {
        return dropOffLat;
    }

    public String getDropOffLng() {
        return dropOffLng;
    }

    public String getLoadingDuration() {
        return loadingDuration;
    }

    public String getUnloadingDuration() {
        return unloadingDuration;
    }

    public String getRideStatus() {
        return rideStatus;
    }

    public String getRideDistance() {
        return rideDistance;
    }

    public String getRideCreatedDate() {
        return rideCreatedDate;
    }

    public String getRideStartedAt() {
        return rideStartedAt;
    }

    public String getRideEndedAt() {
        return rideEndedAt;
    }

    public String getRideFare() {
        return rideFare;
    }

    public String getCollectedRideFare() {
        return collectedRideFare;
    }


}
