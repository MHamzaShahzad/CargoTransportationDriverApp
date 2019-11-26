package com.example.cargotransportationdriverapp.models;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private String imageUrl, name, email, phoneNumber, address, dateOfBirth, gender, accountStatus;
    private Date accountCreatedOn;

    public User() {
    }

    public User(String imageUrl, String name, String email, String phoneNumber, String address, String dateOfBirth, String gender, String accountStatus, Date accountCreatedOn) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.accountStatus = accountStatus;
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

    public String getAccountStatus() {
        return accountStatus;
    }

    public Date getAccountCreatedOn() {
        return accountCreatedOn;
    }

}

