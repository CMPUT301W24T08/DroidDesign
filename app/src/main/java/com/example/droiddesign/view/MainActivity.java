package com.example.droiddesign.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private com.google.android.material.floatingactionbutton.FloatingActionButton skipButton;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences prefs;
    private static final String PREF_USER_ID = "defaultID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        prefs = getSharedPreferences("ConclavePrefs", MODE_PRIVATE);
//        String userId = prefs.getString(PREF_USER_ID, null);

        if (isFirstTimeUser()) {
            // User is already logged in, proceed with automatic login
            startActivity(new Intent(this, EventDetailsActivity.class));
            finish();
        }
        skipButton = findViewById(R.id.skip_button);
        skipButton.setOnClickListener(v -> createUnregisteredUser());
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchScreenActivity.this, RoleSelectionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createUnregisteredUser() {
        String userId = UUID.randomUUID().toString(); // Generate a random user ID
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("registered", false);

        db.collection("usersDB").document(userId).set(user)
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent(LaunchScreenActivity.this, RoleSelectionActivity.class);
                    intent.putExtra("UserId", userId); // Pass the user ID to the next activity
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    // Handle the failure of adding a user
                    Log.e("LaunchScreenActivity", "Error creating unregistered user", e);
                });
    }

    private boolean isFirstTimeUser() {
        return prefs.getString(PREF_USER_ID, null) == null;
    }

}