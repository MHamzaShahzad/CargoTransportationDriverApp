package com.example.cargotransportationdriverapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cargotransportationdriverapp.common.CommonFunctionsClass;
import com.example.cargotransportationdriverapp.common.Constants;
import com.example.cargotransportationdriverapp.R;
import com.example.cargotransportationdriverapp.controllers.MyFirebaseDatabase;
import com.example.cargotransportationdriverapp.models.Driver;
import com.example.cargotransportationdriverapp.models.DriverStatuses;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();
    private Context context;

    private Button btnLogin, btnCreateAccount;

    private static final int RC_SIGN_IN = 1;
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);

        initProviders();
        initLayoutWidgets();
    }

    private void initProviders() {
        // Choose authentication providers

        providers = Collections.singletonList(
                new AuthUI.IdpConfig.PhoneBuilder().build()
        );

        /*providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
        );*/


    }

    private void showSignInOptions() {

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .setTheme(R.style.MyNewTheme)
                        .build(),
                RC_SIGN_IN);

    }

    private void initLayoutWidgets() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);

        setClickListener();
    }

    private void setClickListener() {
        btnLogin.setOnClickListener(this);
        btnCreateAccount.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                showSignInOptions();
                break;
            case R.id.btnCreateAccount:
                showSignInOptions();
                break;
            default:
                Toast.makeText(context, "Unknown event performed!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "onActivityResult: " );
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK && data != null) {

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    checkIfDriverExistInDatabase(user.getUid());
                } else
                    Toast.makeText(context, "Something went wrong , please try again!", Toast.LENGTH_LONG).show();

            } else {
                if (response == null)
                    Toast.makeText(context, "You cancelled the sign in flow.", Toast.LENGTH_SHORT).show();
                if (response != null && response.getError() != null)
                    Toast.makeText(context, response.getError().getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }


    private void checkIfDriverExistInDatabase(String userId) {
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
                            }
                            else
                                CommonFunctionsClass.showCustomDialog(context, "Account Is't Active", "Your account have been de-activated by admin!");
                        SignOut();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    checkIfNotUser(userId);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkIfNotUser(String userId) {
        MyFirebaseDatabase.USER_REFERENCE.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    CommonFunctionsClass.showCustomDialog(context, "Not Driver", "Account already exist for user.");
                    SignOut();

                } else {
                    startCreateAccountActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void startHomeActivity() {
        startActivity(new Intent(context, DrawerHomeActivity.class));
        finish();
    }

    private void startCreateAccountActivity() {
        Log.e(TAG, "startCreateAccountActivity: ");
        startActivity(new Intent(context, CreateNewAccountActivity.class));
        finish();
    }

    public void SignOut() {
        Log.e(TAG, "SignOut: ");
        AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener(task -> {
                    Log.e(TAG, "SignOut: IS_SUCCESSFUL : " + task.isSuccessful());
                });
    }

}
