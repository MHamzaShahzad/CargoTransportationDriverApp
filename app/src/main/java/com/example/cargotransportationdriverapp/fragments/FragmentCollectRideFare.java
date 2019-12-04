package com.example.cargotransportationdriverapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cargotransportationdriverapp.common.Constants;
import com.example.cargotransportationdriverapp.R;
import com.example.cargotransportationdriverapp.activities.DrawerHomeActivity;
import com.example.cargotransportationdriverapp.controllers.MyFirebaseDatabase;
import com.example.cargotransportationdriverapp.controllers.SendPushNotificationFirebase;
import com.example.cargotransportationdriverapp.models.RideDetails;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class FragmentCollectRideFare extends Fragment {

    private static final String TAG = FragmentCollectRideFare.class.getName();
    private Context context;
    private View view;
    public static final String DATE_TIME_FORMAT = "dd MMMM yyyy hh:mm";
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);

    private TextView rideFarePlace;
    private EditText rideFareCollected;
    private Button btnPaymentCollected;

    private FirebaseUser firebaseUser;

    public FragmentCollectRideFare() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        context = container.getContext();
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_collect_ride_fare, container, false);


            initLayoutWidgets();
            getArgumentsData();
        }
        return view;
    }

    private void initLayoutWidgets() {
        rideFarePlace = view.findViewById(R.id.rideFarePlace);
        btnPaymentCollected = view.findViewById(R.id.btnPaymentCollected);
        rideFareCollected = view.findViewById(R.id.rideFareCollected);
    }

    private void getArgumentsData() {

        Bundle bundleArguments = getArguments();
        if (bundleArguments != null) {

            RideDetails rideDetails = (RideDetails) bundleArguments.getSerializable(Constants.RIDE_OBJECT);

            if (rideDetails != null) {
                calculateFare(rideDetails);
                btnPaymentCollected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> map = new HashMap<>();
                        map.put(RideDetails.RIDE_COLLECTED_FARE_REF, rideFareCollected.getText().toString().trim());
                        map.put(RideDetails.RIDE_STATUS_REF, Constants.STATUS_RIDE_FARE_COLLECTED);
                        MyFirebaseDatabase.RIDES_REFERENCE.child(rideDetails.getRideId()).updateChildren(map);
                        map.clear();
                        map.put(Constants.STRING_CURRENT_STATUS, Constants.STATUS_RIDE_FARE_COLLECTED);
                        MyFirebaseDatabase.USER_REFERENCE.child(rideDetails.getUserId()).child(Constants.STRING_STATUSES).updateChildren(map);
                        MyFirebaseDatabase.DRIVERS_REFERENCE.child(rideDetails.getDriverId()).child(Constants.STRING_STATUSES).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                removeSelfCurrentRideCredentials();
                                ((Activity) context).finish();
                                startActivity(new Intent(context, DrawerHomeActivity.class));
                            }
                        });
                        SendPushNotificationFirebase.buildAndSendNotification(
                                context,
                                rideDetails.getUserId(),
                                "Fare Collected",
                                "Driver has collected " + rideFareCollected.getText().toString() + " from you!"
                        );
                    }
                });
            }

        }

    }

    private void removeSelfCurrentRideCredentials() {
        MyFirebaseDatabase.DRIVERS_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_CURRENT_RIDE_MODEL).removeValue();
    }

    private void calculateFare(RideDetails rideDetails) {

        //long distance = Long.parseLong(rideDetails.getRideDistance());
        //int rideDurationInMinutes = getDurationInMinutes(rideDetails.getRideStartedAt(), rideDetails.getRideEndedAt());

        DirectionsResult result = getDirectionsResults(
                new LatLng(Double.valueOf(rideDetails.getPickUpLat()), Double.valueOf(rideDetails.getPickUpLong())),
                new LatLng(Double.valueOf(rideDetails.getDropOffLat()), Double.valueOf(rideDetails.getDropOffLng())));
        if (result != null) {

            long distance = result.routes[0].legs[0].distance.inMeters;
            long rideDurationInMinutes = result.routes[0].legs[0].durationInTraffic.inSeconds / 60;

            int fare = getEstimatedFare(rideDetails.getVehicle(), distance);

            Log.e(TAG, "calculateFare: " + fare);
            MyFirebaseDatabase.RIDES_REFERENCE.child(rideDetails.getRideId()).child(RideDetails.RIDE_FARE_REF).setValue(String.valueOf(fare));
            SendPushNotificationFirebase.buildAndSendNotification(
                    context,
                    rideDetails.getUserId(),
                    "Ride Fare",
                    "Your ride total fare is " + fare + "Rs."
            );
            rideFarePlace.setText(""+fare);
            rideFareCollected.setText(""+fare);

        }
    }

    private int getDurationInMinutes(String startDateTime, String endDateTime) {
        int diffmin = 0;
        try {
            Date sdt = sdf.parse(startDateTime);
            Date edt = sdf.parse(endDateTime);

            long diff = edt.getTime() - sdt.getTime();
            diffmin = (int) (diff / (60 * 1000));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diffmin;
    }

    private int getEstimatedFare(String vehicleType, long distanceInMeters) {
        int yourFare = 0;
        switch (vehicleType) {
            case Constants.DRIVER_VEHICLE_TYPE_LOADER_RIKSHAW:
                yourFare = 200;
                if (distanceInMeters > 3000) {

                    if (distanceInMeters < 5000) {

                        yourFare += (distanceInMeters - 3000) * 0.04;

                    } else {

                        yourFare += 80;
                        yourFare += (distanceInMeters - 5000) * 0.025;

                    }
                }
                break;
            case Constants.DRIVER_VEHICLE_TYPE_RAVI:
                yourFare = 500;
                if (distanceInMeters > 3000) {

                    if (distanceInMeters < 5000) {

                        yourFare += (distanceInMeters - 3000) * 0.1;

                    } else {

                        yourFare += 150;
                        yourFare += (distanceInMeters - 5000) * 0.063;

                    }
                }
                break;
            case Constants.DRIVER_VEHICLE_TYPE_SHAZOR:

                yourFare = 1000;
                if (distanceInMeters > 3000) {

                    if (distanceInMeters < 5000) {

                        yourFare += (distanceInMeters - 3000) * 0.15;

                    } else {

                        yourFare += 150;
                        yourFare += (distanceInMeters - 5000) * 0.0945;

                    }
                }

                break;
        }
        return yourFare;
    }

    private DirectionsResult getDirectionsResults(LatLng pickUpLatLng, LatLng dropOffLatLng) {

        DateTime now = new DateTime();
        DirectionsResult result = null;
        try {
            result = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.DRIVING).origin(pickUpLatLng.latitude + "," + pickUpLatLng.longitude)
                    .destination(dropOffLatLng.latitude + "," + dropOffLatLng.longitude).departureTime(now)
                    .await();
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (com.google.maps.errors.ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(1)
                .setApiKey(getResources().getString(R.string.google_maps_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

}
