package com.example.droiddesign.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The activity that starts when the app is opened.
 * Provides an interface for the user to log into the app with.
 */
public class LaunchScreenActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String PREF_USER_ID = "defaultID";


    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        // Initialize SharedPreferences within onCreate
        SharedPreferences prefs = getSharedPreferences("ConclavePrefs", MODE_PRIVATE);
        String userId = prefs.getString("userID", null);
        com.google.android.material.floatingactionbutton.FloatingActionButton skipButton = findViewById(R.id.skip_button);
        MaterialButton loginGoogle = findViewById(R.id.button_continue_with_google);

        loginGoogle.setOnClickListener(v -> showLoginDialog());
        skipButton.setOnClickListener(v -> createUnregisteredUser());

        MaterialButton signUpButton = findViewById(R.id.button_sign_up);

        signUpButton.setOnClickListener(v -> {
            BasicLoginFragment loginFragment = new BasicLoginFragment();
            loginFragment.show(getSupportFragmentManager(), "Sign Up");
        });



    }
    // Method to show login dialog
    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login");

        // Set up the input
        final EditText usernameText = new EditText(this);
        final EditText passwordText = new EditText(this);
        usernameText.setInputType(InputType.TYPE_CLASS_TEXT);
        passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        // Set hint for the username and password fields
        usernameText.setHint("Username");
        passwordText.setHint("Password");
        builder.setView(usernameText);
        builder.setView(passwordText);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String username = usernameText.getText().toString().trim();
            String password = passwordText.getText().toString().trim();
            authenticateUser(username, password);
        });
        builder.setNegativeButton("Cancel", (dialog, which) ->{
                dialog.cancel();
        });

        builder.show();
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
    // Method to authenticate user
    private void authenticateUser(String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LaunchScreenActivity.this, "Authentication successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, EventMenuActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LaunchScreenActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
//    private boolean isFirstTimeUser() {
//        return prefs.getString(PREF_USER_ID, null) == null;
//    }

}