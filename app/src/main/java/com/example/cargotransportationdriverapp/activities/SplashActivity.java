package com.example.cargotransportationdriverapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cargotransportationdriverapp.common.CommonFunctionsClass;
import com.example.cargotransportationdriverapp.common.Constants;
import com.example.cargotransportationdriverapp.R;
import com.example.cargotransportationdriverapp.controllers.MyFirebaseDatabase;
import com.example.cargotransportationdriverapp.models.Driver;
import com.example.cargotransportationdriverapp.models.DriverStatuses;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getName();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = this;

    }

    @Override
    protected void onStart() {
        super.onStart();
        //FirebaseApp.initializeApp(context);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.e(TAG, "onStart: USER_EXISTS");
            checkIfUserExistInDatabaseOnStart(user.getUid());
        } else
            startMainActivity();
    }

    private void checkIfUserExistInDatabaseOnStart(String userId) {
        MyFirebaseDatabase.DRIVERS_REFERENCE.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {
                        Driver driver = dataSnapshot.child(Constants.STRING_DETAILS).getValue(Driver.class);
                        DriverStatuses statuses = dataSnapshot.child(Constants.STRING_STATUSES).getValue(DriverStatuses.class);
                        if (driver != null && statuses != null)
                            if (statuses.getAccountStatus().equals(Constants.ACCOUNT_ACTIVE)) {
                                startHomeActivity();
                                return;
                            } else
                                CommonFunctionsClass.showCustomDialog(context, "Account Is't Active", "Your account have been de-activated by admin!");
                        SignOut();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    SignOut();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void startHomeActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(context, DrawerHomeActivity.class));
                finish();
            }
        }, 3000);

    }

    private void startMainActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        }, 3000);

    }

    public void SignOut() {
        AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener(task -> {
                    startMainActivity();
                    Log.e(TAG, "SignOut: IS_SUCCESSFUL : " + task.isSuccessful());
                });
    }

}
