package com.example.cargotransportationdriverapp.controllers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyFirebaseDatabase {

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference RIDES_REFERENCE = database.getReference("RIDES");
    public static DatabaseReference USER_REFERENCE = database.getReference("USERS");
    public static DatabaseReference DRIVERS_REFERENCE = database.getReference("DRIVERS");

}
