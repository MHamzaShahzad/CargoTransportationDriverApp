package com.example.cargotransportationdriverapp.models;

public class DriverVehicle {

    private String vehicleId, vehicleNumber, vehicleModel, vehicleDriverLicence, vehicleType, vehicleManualImage;

    public DriverVehicle() {
    }

    public DriverVehicle(String vehicleId, String vehicleNumber, String vehicleModel, String vehicleDriverLicence, String vehicleType, String vehicleManualImage) {
        this.vehicleId = vehicleId;
        this.vehicleNumber = vehicleNumber;
        this.vehicleModel = vehicleModel;
        this.vehicleDriverLicence = vehicleDriverLicence;
        this.vehicleType = vehicleType;
        this.vehicleManualImage = vehicleManualImage;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public String getVehicleDriverLicence() {
        return vehicleDriverLicence;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getVehicleManualImage() {
        return vehicleManualImage;
    }
}
