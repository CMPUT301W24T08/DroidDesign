package com.example.droiddesign.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.credentials.Credential;
import android.credentials.CredentialManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.example.droiddesign.controller.UserController;
import com.example.droiddesign.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The activity that starts when the app is opened.
 * Provides an interface for the user to log into the app with.
 */
@SuppressLint("CustomSplashScreen")
public class LaunchScreenActivity extends AppCompatActivity implements EmailLoginFragment.UserLoginListener, UserController.UserDataListener {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private static final String PREF_USER_ID = "defaultID";

    /**
     * Called when the activity is starting. This is where most initialization should go: calling setContentView(int)
     * to inflate the activity's UI, using findViewById(int) to programmatically interact with widgets in the UI,
     * initializing class scope variables, and setting up listeners or adapters.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Initialize UserController
        UserController userController = new UserController(db);
        userController.setUserDataListener(this);

        setupUI();
    }

    /**
     * Initializes the UI components and sets their event listeners.
     */
    private void setupUI() {
        // Find the buttons from the layout and set their onClickListeners to handle user actions.
        FloatingActionButton skipButton = findViewById(R.id.skip_button);
        MaterialButton loginGoogle = findViewById(R.id.button_continue_with_google);
        MaterialButton signUpButton = findViewById(R.id.button_sign_up);
        MaterialButton loginEmailButton = findViewById(R.id.continue_with_email);

        loginGoogle.setOnClickListener(v -> showEmailLoginDialog()); // TODO implement google login
        skipButton.setOnClickListener(v -> createUnregisteredUser());
        signUpButton.setOnClickListener(v -> showSignUpDialog());
        loginEmailButton.setOnClickListener(v -> showEmailLoginDialog());
    }

    /**
     * Displays the email login dialog.
     */
    public void showEmailLoginDialog() {
        // Show the EmailLoginFragment dialog for email login.
        EmailLoginFragment emailLoginFragment = new EmailLoginFragment();
        emailLoginFragment.show(getSupportFragmentManager(), "Log in");
    }

    /**
     * Displays the sign-up dialog.
     */
    public void showSignUpDialog() {
        // Show the BasicLoginFragment dialog for signing up.
        BasicLoginFragment loginFragment = new BasicLoginFragment();
        loginFragment.show(getSupportFragmentManager(), "Sign up");
    }

    /**
     * Creates an unregistered user in the database and navigates to the EventMenuActivity as an attendee.
     */
    private void createUnregisteredUser() {
        // Generate a random user ID, create a new user object with "registered" set to false,
        // save the user to the database, and navigate to the EventMenuActivity with the role "attendee".
        String userId = UUID.randomUUID().toString();
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("registered", false);

        db.collection("usersDB").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> navigateToEventMenuActivity(userId, "attendee"))
                .addOnFailureListener(e -> logError("Error creating unregistered user", e));
    }

    /**
     * Fetches the role of the user from the database and navigates to the EventMenuActivity with the appropriate role.
     *
     * @param userId The unique identifier of the user.
     */
    private void fetchUserRole(String userId) {
        // Query the database for the user's role and navigate to the EventMenuActivity with the retrieved role.
        db.collection("usersDB").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String role = document.getString("role");
                    if (role != null) {
                        navigateToEventMenuActivity(userId, role);
                    } else {
                        // Handle the case where the role is not found
                        showToast(R.string.error_role_not_found);
                    }
                } else {
                    // Handle the case where the user document does not exist
                    showToast(R.string.error_user_data_not_found);
                }
            } else {
                // Handle the failure of fetching user data
                Log.e("LaunchScreenActivity", "Failed to fetch user role.", task.getException());
                showToast(R.string.error_user_data_not_found);
            }
        });
    }

    /**
     * Displays a toast message.
     *
     * @param messageResId The resource ID of the message string to be displayed.
     */
    private void showToast(@StringRes int messageResId) {
        // Display a toast with the specified message.
        Toast.makeText(LaunchScreenActivity.this, messageResId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Logs an error message and shows a generic error toast.
     *
     * @param message The error message to log.
     * @param e       The exception associated with the error.
     */
    private void logError(String message, Exception e) {
        // Log the error and display a generic error toast.
        Log.e("LaunchScreenActivity", message, e);
        showToast(R.string.error_generic);
    }

    /**
     * Navigates to the EventMenuActivity without passing any extras.
     */
    private void navigateToEventMenuActivity() {
        // Create an intent for the EventMenuActivity and start the activity.
        Intent intent = new Intent(this, EventMenuActivity.class);
        startActivity(intent);
    }

    /**
     * Navigates to the EventMenuActivity with the provided user ID and role.
     *
     * @param userId The unique identifier of the user.
     * @param role   The role of the user within the application context.
     */
    private void navigateToEventMenuActivity(String userId, String role) {
        // Overloaded method to navigate to the EventMenuActivity with userId and role extras.
        Intent intent = new Intent(this, EventMenuActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userRole", role);
        startActivity(intent);
    }

    /**
     * Callback method triggered when a user's login attempt has failed.
     */
    @Override
    public void userFailedLogin() {
        showToast(R.string.login_failed);
    }

    /**
     * Callback method triggered when a user has logged in without specifying a FirebaseUser object.
     */
    @Override
    public void userLoggedIn() {
        String dummyUUID = "123e4567-e89b-12d3-a456-426614174000";
        String dummyRole = "attendee";
        navigateToEventMenuActivity(dummyUUID, dummyRole); // TODO fix this!!!!
    }

    /**
     * Callback method triggered when a user has logged in with a FirebaseUser object.
     *
     * @param firebaseUser The FirebaseUser object representing the logged-in user.
     */
    @Override
    public void userLoggedIn(FirebaseUser firebaseUser) {
        // Handle user login with a FirebaseUser object.
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            // Fetch user role from the database
            fetchUserRole(userId);
        } else {
            // Handle the case where firebaseUser is null
            Toast.makeText(LaunchScreenActivity.this, "Error: User data not available.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback method triggered when user data has been fetched successfully.
     *
     * @param user The User object containing the fetched user data.
     */
    @Override
    public void onUserDataFetched(User user) {
        // Handle successful user data fetch.
        if (user != null) {
            navigateToEventMenuActivity(user.getUserId(), user.getRole());
        } else {
            Toast.makeText(LaunchScreenActivity.this, "User data fetch failed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback method triggered when the fetch of user data has failed.
     */
    @Override
    public void onUserDataFetchFailed() {
        showToast(R.string.failed_to_fetch_user_data);
    }

}