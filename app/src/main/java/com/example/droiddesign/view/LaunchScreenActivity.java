package com.example.droiddesign.view;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * The LaunchScreenActivity class represents the main entry point for the application. It handles user authentication and navigation to different parts of the app based on user actions.
 */
public class LaunchScreenActivity extends AppCompatActivity implements BasicLoginFragment.UserCreationListener{


    /**
     * An instance of FirebaseFirestore providing access to the Firebase Firestore database.
     * This instance is used throughout the activity to interact with the Firestore database.
     */
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    SharedPreferenceHelper prefsHelper;


    /**
     * Called when the activity is starting. This is where most initialization should go.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        // Initialize SharedPreferences
        prefsHelper = new SharedPreferenceHelper(this);
        // Not a new user scenario
        if (!prefsHelper.isFirstTimeUser()) {
            // Returning user scenario
            Intent intent = new Intent(LaunchScreenActivity.this, EventMenuActivity.class);
            startActivity(intent);
            finish();
        }

        findViewById(R.id.button_enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of BasicLoginFragment
                BasicLoginFragment basicLoginFragment = new BasicLoginFragment();
                basicLoginFragment.setCancelable(false);
                basicLoginFragment.show(getSupportFragmentManager(), "BasicLoginFragment");
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        System.out.println(token);
                        Log.d(TAG, "Refreshed token: " + token);
                        // Log and toast
                        Toast.makeText(LaunchScreenActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Callback triggered when a new user is successfully created. This navigates the user to the EventMenuActivity.
     */
    @Override
    public void userCreated() {
        Intent intent = new Intent(LaunchScreenActivity.this,EventMenuActivity.class);
        startActivity(intent);
        finish();
    }
}