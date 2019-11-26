package com.example.cargotransportationdriverapp.controllers;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyFirebaseStorage {

    public static FirebaseStorage storage = FirebaseStorage.getInstance();

    public static StorageReference USER_IMAGES_REFERENCE = storage.getReference().child("user_images/");
    public static StorageReference DRIVER_IMAGES_REFERENCE = storage.getReference().child("driver_images/");
    public static StorageReference VEHICLE_IMAGES_REFERENCE = storage.getReference().child("post_images/");

}
