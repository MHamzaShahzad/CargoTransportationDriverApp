package com.example.cargotransportationdriverapp.models;

public class DriverStatuses {

    private String accountStatus, networkStatus, currentStatus;

    public DriverStatuses() {
    }

    public DriverStatuses(String accountStatus, String networkStatus, String currentStatus) {
        this.accountStatus = accountStatus;
        this.networkStatus = networkStatus;
        this.currentStatus = currentStatus;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public String getNetworkStatus() {
        return networkStatus;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }
}
