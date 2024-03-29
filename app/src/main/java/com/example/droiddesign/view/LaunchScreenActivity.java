package com.example.droiddesign.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
            }
        }
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseMessaging.getInstance().subscribeToTopic("annoucementChannel")
                .addOnCompleteListener(new OnCompleteListener<Void>( ) {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Done";
                        if (!task.isSuccessful( )) {
                            msg = "Failed";
                        }
                    }
                });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }


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


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted. You can proceed with showing the notification.
            } else {
                // Permission denied. You can notify the user that the feature requires permission.
            }
        }
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
    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // Inform the user that your app will not show notifications.
                    new AlertDialog.Builder(this.getBaseContext())
                            .setTitle("Notification Permission Denied")
                            .setMessage("Without notification permission, you might miss important updates and messages. Please consider enabling it in your app settings to make the most out of our app.")
                            .setPositiveButton("Go to Settings", (dialogInterface, i) -> {
                                // Intent to open app settings
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", this.getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            })
                            .setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();
                }
            });


    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}