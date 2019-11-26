package com.example.cargotransportationdriverapp.models;

import java.io.Serializable;
import java.util.Date;

public class Driver implements Serializable {

    private String imageUrl, name, email, phoneNumber, address, dateOfBirth, gender, vehicleType, vehicleNumber, vehicleModel;
    private Date accountCreatedOn;

    public Driver() {
    }

    public Driver(String imageUrl, String name, String email, String phoneNumber, String address, String dateOfBirth, String gender, String vehicleType, String vehicleNumber, String vehicleModel, Date accountCreatedOn) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.vehicleModel = vehicleModel;
        this.accountCreatedOn = accountCreatedOn;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public Date getAccountCreatedOn() {
        return accountCreatedOn;
    }

}

