package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * The LaunchScreenActivity class represents the main entry point for the application. It handles user authentication and navigation to different parts of the app based on user actions.
 */
public class LaunchScreenActivity extends AppCompatActivity implements BasicLoginFragment.UserCreationListener, EmailLoginFragment.UserLoginListener {


    /**
     * An instance of FirebaseFirestore providing access to the Firebase Firestore database.
     * This instance is used throughout the activity to interact with the Firestore database.
     */
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * An instance of FirebaseAuth used for handling user authentication.
     * This instance allows for user sign-in, sign-up, and status checking functionalities throughout the activity.
     */
    private FirebaseAuth mAuth;
    SharedPreferenceHelper prefsHelper;


    /**
     * Called when the activity is starting. This is where most initialization should go.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        // Initialize SharedPreferences
        prefsHelper = new SharedPreferenceHelper(this);
        // Not a new user scenario
        if (!prefsHelper.isFirstTimeUser()) {
            // Returning user scenario
            Intent intent = new Intent(LaunchScreenActivity.this, EventMenuActivity.class);
            startActivity(intent);
            finish();
        }

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        // Initialize SharedPreferences within onCreate
        com.google.android.material.floatingactionbutton.FloatingActionButton skipButton = findViewById(R.id.skip_button);
        MaterialButton loginGoogle = findViewById(R.id.button_continue_with_google);

        loginGoogle.setOnClickListener(v -> showLoginDialog());
        skipButton.setOnClickListener(v -> createUnregisteredUser());

        MaterialButton signUpButton = findViewById(R.id.button_sign_up);

        signUpButton.setOnClickListener(v -> {
            BasicLoginFragment loginFragment = new BasicLoginFragment();
            loginFragment.show(getSupportFragmentManager(), "Sign Up");
        });

        MaterialButton loginEmailButton = findViewById(R.id.continue_with_email);
        loginEmailButton.setOnClickListener(v->{
            EmailLoginFragment emailFragment = new EmailLoginFragment();
            emailFragment.show(getSupportFragmentManager(),"Log in");
        });

    }

    /**
     * Displays a login dialog to the user where they can enter their credentials.
     */

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

    /**
     * Creates an unregistered user in the system with a unique user ID and navigates to the RoleSelectionActivity.
     */
    private void createUnregisteredUser() {
        Intent intent = new Intent(LaunchScreenActivity.this, RoleSelectionActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Authenticates a user with the provided username and password.
     * @param username The username inputted by the user.
     * @param password The password inputted by the user.
     */
    // Method to authenticate user
    private void authenticateUser(String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LaunchScreenActivity.this, "Authentication successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, EventMenuActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LaunchScreenActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
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

    /**
     * Callback triggered when a user successfully logs in. This navigates the user to the EventMenuActivity.
     */
    @Override
    public void userLoggedIn() {
        Intent intent = new Intent(LaunchScreenActivity.this, EventMenuActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Callback triggered when a user fails to log in. Displays a toast message indicating login failure.
     */
    @Override
    public void userFailedLogin() {
        Toast.makeText(LaunchScreenActivity.this, "Login Failed",Toast.LENGTH_SHORT).show();
    }
}