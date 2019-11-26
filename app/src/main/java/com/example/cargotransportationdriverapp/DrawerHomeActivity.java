package com.example.cargotransportationdriverapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import com.example.cargotransportationdriverapp.controllers.MediaControllingClass;
import com.example.cargotransportationdriverapp.controllers.MyFirebaseDatabase;
import com.example.cargotransportationdriverapp.controllers.SendPushNotificationFirebase;
import com.example.cargotransportationdriverapp.models.CurrentRideModel;
import com.example.cargotransportationdriverapp.models.Driver;
import com.example.cargotransportationdriverapp.models.DriverStatuses;
import com.example.cargotransportationdriverapp.models.RideDetails;
import com.example.cargotransportationdriverapp.models.UpdateLocationsModel;
import com.example.cargotransportationdriverapp.models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class DrawerHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = DrawerHomeActivity.class.getName();
    private Context context;

    private GoogleApiClient mLocationClient;
    private LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private LatLng latLng;
    private static MarkerOptions markerOptions;

    private boolean shouldMoveToCurrentLocation = true;

    private ValueEventListener driverDetailsValueEventListener, generalStatusesListener, driverAccountStatusValueEventListener;
    private FirebaseUser firebaseUser;

    private Button btnOnlineOffline;
    private MediaControllingClass mediaControllingClass;
    // initBottomUp Sheet
    private LinearLayout bottom_sheet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_home);

        context = this;
        mediaControllingClass = MediaControllingClass.getInstance(context);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null)
            SignOut();
        mLocationRequest = new LocationRequest();
        bottom_sheet = findViewById(R.id.bottom_sheet);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // initialize
        initLayoutWidgets();
        initDriverDetailsListener();
        initGeneralStatusesListener();
        initDriverAccountStatusListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser != null)
            FirebaseMessaging.getInstance().subscribeToTopic(firebaseUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        Log.d(TAG, "Topic subscribed!");
                    else
                        Log.e(TAG, "Can't subscribe to topic");
                }
            });
        else
            SignOut();
    }

    private void initLayoutWidgets() {
        btnOnlineOffline = findViewById(R.id.btnOnlineOffline);

        setBtnOnlineOffline();
    }

    private void setBtnOnlineOffline() {
        btnOnlineOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: CLICKED_ON_ONLINE_OFFLINE");

                switch (btnOnlineOffline.getText().toString()) {
                    case Constants.STATUS_OFFLINE_TEXT:
                        updateNetworkStatus(Constants.DRIVER_ONLINE);
                        break;
                    case Constants.STATUS_ONLINE_TEXT:
                        updateNetworkStatus(Constants.DRIVER_OFFLINE);
                        break;
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SignOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new FragmentRidesHistory()).addToBackStack(null).commit();

        } else if (id == R.id.nav_share) {
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new ContactUsFragment()).addToBackStack(null).commit();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "onMapReady: ");
        mMap = googleMap;
        setGoogleClientForMap();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "onConnected : Permission not granted!");
            //Permission not granted by user so cancel the further execution.
            return;
        }
        mMap.setMyLocationEnabled(true);
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, DrawerHomeActivity.this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (generalStatusesListener == null)
            initGeneralStatusesListener();
        if (firebaseUser == null) {
            startMainActivity();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(context, "Could not get Location", Toast.LENGTH_SHORT).show();
        } else {

            latLng = new LatLng(location.getLatitude(), location.getLongitude());

            /*
            markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Your current Location!");
            mMap.addMarker(markerOptions);
            mMap.clear();
            mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(100)
                    .strokeColor(Color.BLUE)
                    .strokeWidth(1f)
                    .fillColor(0x550000FF));*/

            if (shouldMoveToCurrentLocation) {

                float zoomLevel = 16.0f; //This goes up to 21
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                shouldMoveToCurrentLocation = false;
            }

            updateLocationOnFirebase(latLng.latitude, latLng.longitude, location.getBearing());

        }


    }

    private void updateNetworkStatus(String status) {
        MyFirebaseDatabase.DRIVERS_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_STATUSES).child(Constants.STRING_DRIVER_NETWORK_STATUS).setValue(status);
    }

    private void updateCurrentStatusSelf(String status) {
        MyFirebaseDatabase.DRIVERS_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_STATUSES).child(Constants.STRING_CURRENT_STATUS).setValue(status);
    }

    private void updateCurrentStatusUser(String uid, String status) {
        MyFirebaseDatabase.USER_REFERENCE.child(uid).child(Constants.STRING_STATUSES).child(Constants.STRING_CURRENT_STATUS).setValue(status);
    }

    private void updateLocationOnFirebase(double latitude, double longitude, float bearingTo) {
        UpdateLocationsModel locationsModel = new UpdateLocationsModel(String.valueOf(bearingTo), String.valueOf(latitude), String.valueOf(longitude));
        MyFirebaseDatabase.DRIVERS_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_LOCATIONS).setValue(locationsModel);
    }

    private void setGoogleClientForMap() {
        mLocationClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(5000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationClient.connect();
    }

    private String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                //cityName = returnedAddress.getLocality();
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.e("@LocationAddress", "My Current loction address" + strReturnedAddress.toString());
            } else {
                Log.e("@AddressNotFound", "My Current loction address No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("@ErrinInAAddress", "My Current loction address Canont get Address!");
        }
        return strAdd;
    }

    // my statuses life cycle...
    private void initGeneralStatusesListener() {
        generalStatusesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    Log.e(TAG, "onDataChange: MY_STATUS : " + dataSnapshot.getValue());

                    switch ((String) dataSnapshot.getValue()) {
                        case Constants.STATUS_DEFAULT:
                            mediaControllingClass.stopPlaying();
                            break;
                        case Constants.DRIVER_STATUS_RECEIVING_RIDE:
                            mediaControllingClass.startPlaying();
                            getRideAndUserDetails(Constants.DRIVER_STATUS_RECEIVING_RIDE);
                            break;
                        case Constants.STATUS_ACCEPTED_RIDE:
                            mediaControllingClass.stopPlaying();
                            getRideAndUserDetails(Constants.STATUS_ACCEPTED_RIDE);
                            break;
                        case Constants.STATUS_DRIVER_ON_THE_WAY:
                            getRideAndUserDetails(Constants.STATUS_DRIVER_ON_THE_WAY);
                            break;
                        case Constants.STATUS_DRIVER_REACHED:
                            getRideAndUserDetails(Constants.STATUS_DRIVER_REACHED);
                            break;
                        case Constants.STATUS_START_LOADING:
                            getRideAndUserDetails(Constants.STATUS_START_LOADING);
                            break;
                        case Constants.STATUS_END_LOADING:
                            getRideAndUserDetails(Constants.STATUS_END_LOADING);
                            break;
                        case Constants.STATUS_START_RIDE:
                            getRideAndUserDetails(Constants.STATUS_START_RIDE);
                            break;
                        case Constants.STATUS_END_RIDE:
                            getRideAndUserDetails(Constants.STATUS_END_RIDE);
                            break;
                        case Constants.STATUS_START_UNLOADING:
                            getRideAndUserDetails(Constants.STATUS_START_UNLOADING);
                            break;
                        case Constants.STATUS_END_UNLOADING:
                            getRideAndUserDetails(Constants.STATUS_END_UNLOADING);
                            break;
                        case Constants.STATUS_COMPLETED_RIDE:
                            getRideAndUserDetails(Constants.STATUS_COMPLETED_RIDE);
                            break;
                        case Constants.STATUS_RIDE_FARE_COLLECTED:
                            getRideAndUserDetails(Constants.STATUS_RIDE_FARE_COLLECTED);
                            updateCurrentStatusSelf(Constants.STATUS_DEFAULT);
                            break;
                        default:
                            Log.e(TAG, "onDataChange: MY_STATUS_NOT_DEFINED : " + (String) dataSnapshot.getValue());


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        MyFirebaseDatabase.DRIVERS_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_STATUSES).child(Constants.STRING_CURRENT_STATUS)
                .addValueEventListener(generalStatusesListener);
    }

    //to show the user name on nav_bar
    private void initDriverDetailsListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        final TextView navUserName = headerView.findViewById(R.id.nav_name);
        final TextView navUserEmail = headerView.findViewById(R.id.nav_email);
        final CircleImageView navUserPhoto = headerView.findViewById(R.id.nav_photo);

        driverDetailsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    try {

                        Driver databaseUser = dataSnapshot.getValue(Driver.class);

                        if (databaseUser != null) {

                            navUserEmail.setText(databaseUser.getEmail());
                            navUserName.setText(databaseUser.getName());

                            // now we will use Glide to load user image
                            if (databaseUser.getImageUrl() != null && !databaseUser.getImageUrl().equals("null") && !databaseUser.getImageUrl().equals(""))
                                Picasso.with(context)
                                        .load(databaseUser.getImageUrl())
                                        .error(R.drawable.avatar)
                                        .placeholder(R.drawable.avatar)
                                        .centerInside().fit()
                                        .into(navUserPhoto);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        MyFirebaseDatabase.DRIVERS_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_DETAILS)
                .addValueEventListener(driverDetailsValueEventListener);
    }

    //to show the user name on nav_bar
    private void initDriverAccountStatusListener() {
        driverAccountStatusValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: STATUS : " + dataSnapshot.getValue());
                if (dataSnapshot.getValue() != null) {
                    try {
                        DriverStatuses statuses = dataSnapshot.getValue(DriverStatuses.class);


                        if (statuses != null) {
                            Log.e(TAG, "onDataChange -> NETWORK_STATUS : " + statuses.getNetworkStatus());

                            if (statuses.getAccountStatus() == null || statuses.getAccountStatus().equals(Constants.ACCOUNT_INACTIVE))
                                SignOut();

                            if (statuses.getNetworkStatus() != null) {
                                switch (statuses.getNetworkStatus()) {
                                    case Constants.DRIVER_OFFLINE:
                                        btnOnlineOffline.setText(Constants.STATUS_OFFLINE_TEXT);
                                        break;
                                    case Constants.DRIVER_ONLINE:
                                        btnOnlineOffline.setText(Constants.STATUS_ONLINE_TEXT);
                                        break;
                                }
                            } else
                                updateNetworkStatus(Constants.DRIVER_OFFLINE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        MyFirebaseDatabase.DRIVERS_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_STATUSES)
                .addValueEventListener(driverAccountStatusValueEventListener);
    }


    private void getRideAndUserDetails(String currentStatus) {

        MyFirebaseDatabase.DRIVERS_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_CURRENT_RIDE_MODEL).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        CurrentRideModel model = dataSnapshot.getValue(CurrentRideModel.class);
                        if (model != null) {

                            getBookingDetails(model.getUserId(), model.getRideId(), currentStatus);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getBookingDetails(final String userId, final String rideId, String currentStatus) {
        MyFirebaseDatabase.USER_REFERENCE.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Log.e(TAG, "onDataChange: BOOKED_USER_SNAPSHOT" + dataSnapshot);
                    try {
                        User user = dataSnapshot.child(Constants.STRING_DETAILS).getValue(User.class);
                        if (user != null) {
                            Log.e(TAG, "onDataChange: " + user.getName() + " : " + user.getPhoneNumber());
                            getRideDetails(rideId, user, currentStatus);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getRideDetails(String rideId, final User user, String currentStatus) {
        MyFirebaseDatabase.RIDES_REFERENCE.child(rideId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    try {
                        RideDetails rideDetails = dataSnapshot.getValue(RideDetails.class);
                        if (rideDetails != null) {

                            if (currentStatus.equals(Constants.STATUS_COMPLETED_RIDE)) {
                                FragmentCollectRideFare fragmentCollectRideFare = new FragmentCollectRideFare();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constants.RIDE_OBJECT, rideDetails);
                                fragmentCollectRideFare.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragmentCollectRideFare).commit();
                            } else {

                                showBottomUpSheet(user, rideDetails, currentStatus);

                                try {
                                    mMap.clear();
                                    LatLng pickUpLatLng = new LatLng(Double.valueOf(rideDetails.getPickUpLat()), Double.valueOf(rideDetails.getPickUpLong()));
                                    LatLng dropOffLatLng = new LatLng(Double.valueOf(rideDetails.getDropOffLat()), Double.valueOf(rideDetails.getDropOffLng()));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickUpLatLng, 16));
                                    mMap.addMarker(new MarkerOptions().position(pickUpLatLng).snippet(rideDetails.getPickUpAddress())).showInfoWindow();
                                    mMap.addMarker(new MarkerOptions().position(dropOffLatLng).snippet(rideDetails.getDropOffAddress())).showInfoWindow();
                                    Log.e(TAG, "onDataChange: " + pickUpLatLng + " : " + dropOffLatLng);
                                    addPolyline(getDirectionsResults(pickUpLatLng, dropOffLatLng), mMap);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showBottomUpSheet(final User user, RideDetails rideDetails, String currentStatus) {

        // Layout Widgets
        TextView riderName, pickUpLocationPlace, dropOffLocationPlace, messageUser, callUser, currentRideStatus;
        CircleImageView riderImage;

        Button btnAcceptRide, btnOnTheWay, btnReachedToPickup, btnStartLoading,
                btnCompleteLoading, btnStartRide, btnReachedToDropOff, btnStartUnloading,
                btnCompleteUnloading, btnRideEnded;


        bottom_sheet.setVisibility(View.VISIBLE);

        BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        // callback for do something
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        //bottom up sheet initialized

        btnAcceptRide = findViewById(R.id.btnAcceptRide);
        btnOnTheWay = findViewById(R.id.btnOnTheWay);
        btnReachedToPickup = findViewById(R.id.btnReachedToPickup);
        btnStartLoading = findViewById(R.id.btnStartLoading);
        btnCompleteLoading = findViewById(R.id.btnCompleteLoading);
        btnStartRide = findViewById(R.id.btnStartRide);
        btnReachedToDropOff = findViewById(R.id.btnReachedToDropOff);
        btnStartUnloading = findViewById(R.id.btnStartUnloading);
        btnCompleteUnloading = findViewById(R.id.btnCompleteUnloading);
        btnRideEnded = findViewById(R.id.btnRideEnded);

        riderName = findViewById(R.id.userName);
        currentRideStatus = findViewById(R.id.currentRideStatus);
        pickUpLocationPlace = findViewById(R.id.pickUpLocationPlace);
        dropOffLocationPlace = findViewById(R.id.dropOffLocationPlace);
        riderImage = findViewById(R.id.userImage);
        messageUser = findViewById(R.id.messageUser);
        callUser = findViewById(R.id.callUser);

        if (rideDetails != null) {
            currentRideStatus.setText(CommonFunctionsClass.getRideStringStatus(rideDetails.getRideStatus()));
            pickUpLocationPlace.setText(rideDetails.getPickUpAddress());
            dropOffLocationPlace.setText(rideDetails.getDropOffAddress());
        }

        if (user != null) {

            riderName.setText(user.getName());
            Picasso.with(context)
                    .load(user.getImageUrl())
                    .error(R.drawable.avatar)
                    .placeholder(R.drawable.avatar)
                    .centerInside().fit()
                    .into(riderImage);

            callUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonFunctionsClass.call_to_owner(context, user.getPhoneNumber());
                }
            });
            messageUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonFunctionsClass.send_sms_to_owner(context, user.getPhoneNumber());
                }
            });

        }

        if (currentStatus.equals(Constants.DRIVER_STATUS_RECEIVING_RIDE)) {
            btnAcceptRide.setVisibility(View.VISIBLE);
            btnAcceptRide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCurrentStatusSelf(Constants.STATUS_ACCEPTED_RIDE);
                    updateCurrentStatusUser(rideDetails.getUserId(), Constants.STATUS_ACCEPTED_RIDE);
                    btnAcceptRide.setVisibility(View.INVISIBLE);
                    MyFirebaseDatabase.RIDES_REFERENCE.child(rideDetails.getRideId()).child(RideDetails.RIDE_DRIVER_ID_REF).setValue(firebaseUser.getUid());
                    SendPushNotificationFirebase.buildAndSendNotification(
                            context,
                            rideDetails.getUserId(),
                            "Ride Accepted",
                            "Your ride for has been accepted by driver!"
                    );
                }
            });
        } else if (currentStatus.equals(Constants.STATUS_ACCEPTED_RIDE)) {
            btnOnTheWay.setVisibility(View.VISIBLE);
            btnOnTheWay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCurrentStatusSelf(Constants.STATUS_DRIVER_ON_THE_WAY);
                    updateCurrentStatusUser(rideDetails.getUserId(), Constants.STATUS_DRIVER_ON_THE_WAY);
                    btnOnTheWay.setVisibility(View.INVISIBLE);
                    SendPushNotificationFirebase.buildAndSendNotification(
                            context,
                            rideDetails.getUserId(),
                            "Driver on the way",
                            "Driver is coming to your pick up location!"
                    );
                }
            });
        } else if (currentStatus.equals(Constants.STATUS_DRIVER_ON_THE_WAY)) {
            btnReachedToPickup.setVisibility(View.VISIBLE);
            btnReachedToPickup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCurrentStatusSelf(Constants.STATUS_DRIVER_REACHED);
                    updateCurrentStatusUser(rideDetails.getUserId(), Constants.STATUS_DRIVER_REACHED);
                    btnReachedToPickup.setVisibility(View.INVISIBLE);
                    SendPushNotificationFirebase.buildAndSendNotification(
                            context,
                            rideDetails.getUserId(),
                            "Driver Reached",
                            "Driver reached to your pick up location!"
                    );
                }
            });
        } else if (currentStatus.equals(Constants.STATUS_DRIVER_REACHED)) {
            btnStartLoading.setVisibility(View.VISIBLE);
            btnStartLoading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCurrentStatusSelf(Constants.STATUS_START_LOADING);
                    updateCurrentStatusUser(rideDetails.getUserId(), Constants.STATUS_START_LOADING);
                    btnStartLoading.setVisibility(View.INVISIBLE);
                    SendPushNotificationFirebase.buildAndSendNotification(
                            context,
                            rideDetails.getUserId(),
                            "Loading started",
                            "Loading of your goods has been started!!"
                    );
                }
            });
        } else if (currentStatus.equals(Constants.STATUS_START_LOADING)) {
            btnCompleteLoading.setVisibility(View.VISIBLE);
            btnCompleteLoading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCurrentStatusSelf(Constants.STATUS_END_LOADING);
                    updateCurrentStatusUser(rideDetails.getUserId(), Constants.STATUS_END_LOADING);
                    btnCompleteLoading.setVisibility(View.INVISIBLE);
                    SendPushNotificationFirebase.buildAndSendNotification(
                            context,
                            rideDetails.getUserId(),
                            "Loading Completed",
                            "Loading of your goods into vehicle have been completed!"
                    );
                }
            });
        } else if (currentStatus.equals(Constants.STATUS_END_LOADING)) {
            btnStartRide.setVisibility(View.VISIBLE);
            btnStartRide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCurrentStatusSelf(Constants.STATUS_START_RIDE);
                    updateCurrentStatusUser(rideDetails.getUserId(), Constants.STATUS_START_RIDE);
                    btnStartRide.setVisibility(View.INVISIBLE);
                    MyFirebaseDatabase.RIDES_REFERENCE
                            .child(rideDetails.getRideId())
                            .child(RideDetails.RIDE_STARTED_AT_REF)
                            .setValue(
                                    new SimpleDateFormat("dd MMMM yyyy hh:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime())
                            );
                    SendPushNotificationFirebase.buildAndSendNotification(
                            context,
                            rideDetails.getUserId(),
                            "Ride Started",
                            "Driver is going to your drop off location!"
                    );
                }
            });
        } else if (currentStatus.equals(Constants.STATUS_START_RIDE)) {
            btnReachedToDropOff.setVisibility(View.VISIBLE);
            btnReachedToDropOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCurrentStatusSelf(Constants.STATUS_END_RIDE);
                    updateCurrentStatusUser(rideDetails.getUserId(), Constants.STATUS_END_RIDE);
                    btnReachedToDropOff.setVisibility(View.INVISIBLE);
                    MyFirebaseDatabase.RIDES_REFERENCE
                            .child(rideDetails.getRideId())
                            .child(RideDetails.RIDE_ENDED_AT_REF)
                            .setValue(
                                    new SimpleDateFormat("dd MMMM yyyy hh:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime())
                            );
                    SendPushNotificationFirebase.buildAndSendNotification(
                            context,
                            rideDetails.getUserId(),
                            "Driver Reached Drop Off",
                            "Driver reached to your drop off location!"
                    );
                }
            });
        } else if (currentStatus.equals(Constants.STATUS_END_RIDE)) {
            btnStartUnloading.setVisibility(View.VISIBLE);
            btnStartUnloading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCurrentStatusSelf(Constants.STATUS_START_UNLOADING);
                    updateCurrentStatusUser(rideDetails.getUserId(), Constants.STATUS_START_UNLOADING);
                    btnStartUnloading.setVisibility(View.INVISIBLE);
                    SendPushNotificationFirebase.buildAndSendNotification(
                            context,
                            rideDetails.getUserId(),
                            "Un-Loading Started!",
                            "Unloading of your goods has been started!"
                    );
                }
            });
        } else if (currentStatus.equals(Constants.STATUS_START_UNLOADING)) {
            btnCompleteUnloading.setVisibility(View.VISIBLE);
            btnCompleteUnloading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCurrentStatusSelf(Constants.STATUS_END_UNLOADING);
                    updateCurrentStatusUser(rideDetails.getUserId(), Constants.STATUS_END_UNLOADING);
                    btnCompleteUnloading.setVisibility(View.INVISIBLE);
                    SendPushNotificationFirebase.buildAndSendNotification(
                            context,
                            rideDetails.getUserId(),
                            "Un-Loading Completed",
                            "Unloading of your goods has been completed!"
                    );
                }
            });
        } else if (currentStatus.equals(Constants.STATUS_END_UNLOADING)) {
            btnRideEnded.setVisibility(View.VISIBLE);
            btnRideEnded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCurrentStatusSelf(Constants.STATUS_COMPLETED_RIDE);
                    updateCurrentStatusUser(rideDetails.getUserId(), Constants.STATUS_COMPLETED_RIDE);
                    btnRideEnded.setVisibility(View.INVISIBLE);
                    SendPushNotificationFirebase.buildAndSendNotification(
                            context,
                            rideDetails.getUserId(),
                            "Ride Ended",
                            "Your ride has ended!"
                    );
                }
            });
        }

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

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        try {
            List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
            //mMap.clear();
            mMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(Color.BLUE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeAccountStatusListener() {
        if (driverAccountStatusValueEventListener != null) {
            MyFirebaseDatabase.DRIVERS_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_STATUSES)
                    .removeEventListener(driverAccountStatusValueEventListener);
        }
    }

    private void removeUserDetailsListener() {
        if (driverDetailsValueEventListener != null) {
            MyFirebaseDatabase.DRIVERS_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_DETAILS)
                    .removeEventListener(driverDetailsValueEventListener);
        }
    }

    private void removeGeneralStatusesListener() {
        if (generalStatusesListener != null)
            MyFirebaseDatabase.DRIVERS_REFERENCE.child(firebaseUser.getUid()).child(Constants.STRING_STATUSES)
                    .removeEventListener(generalStatusesListener);
    }

    private void removeLocationUpdates() {
        if (mLocationClient != null && mLocationClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient, this);
            mLocationClient.disconnect();
        }
    }

    public void SignOut() {
        AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener(task -> {
                    Log.e(TAG, "SignOut: IS_SUCCESSFUL : " + task.isSuccessful());

                    removeAccountStatusListener();
                    removeGeneralStatusesListener();
                    removeUserDetailsListener();
                    removeLocationUpdates();
                    updateNetworkStatus(Constants.DRIVER_OFFLINE);

                    startMainActivity();
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeLocationUpdates();
    }

    private void startMainActivity() {
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }
}