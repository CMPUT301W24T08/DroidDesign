package com.example.droiddesign.view;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Activity to allow the user to select a role (Admin, Organizer, Attendee).
 * This class is responsible for handling user interactions with role selection buttons,
 * performing user registration for the selected role, and navigating to the next activity.
 */

public class RoleSelectionActivity extends AppCompatActivity {

	/**
	 * Buttons used for selecting the role of the new or existing user.
	 * Admin image button allows the user to select the 'Admin' role.
	 * Organizer image button allows the user to select the 'Organizer' role.
	 * Attendee image button allows the user to select the 'Attendee' role.
	 */

	private MaterialButton adminImage, organizerImage, attendeeImage;

	/**
	 * An instance of FirebaseFirestore, providing access to the Firebase Firestore database.
	 * Used here to interact with the 'users' collection in Firestore.
	 */
	private final FirebaseFirestore db = FirebaseFirestore.getInstance();

	/**
	 * Reference to the 'users' collection in the Firestore database.
	 * This collection contains documents where each document represents a user and their associated data.
	 */

	CollectionReference usersCollection = db.collection("users");

	/**
	 * Constant used as a key in SharedPreferences to store and retrieve the user's ID.
	 * This ID is used to identify the user across different sessions and activities.
	 */

	private static final String PREF_USER_ID = "defaultID";

	/**
	 * Called when the activity is starting.
	 * This is where most initialization should go: calling setContentView(int) to inflate
	 * the activity's UI, using findViewById(int) to programmatically interact with widgets in the UI.
	 * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
	 *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
	 *                           Note: Otherwise it is null.
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_role_selection_unregistered);

		// Initialize views and set click listeners
		adminImage = findViewById(R.id.admin_button);
		organizerImage = findViewById(R.id.organizer_button);
		attendeeImage = findViewById(R.id.attendee_button);

		setRoleSelectionListeners();
	}


	/**
	 * Sets click listeners for each role selection button.
	 * Defines the actions to be taken when each role button is clicked.
	 */
	private void setRoleSelectionListeners() {
		attendeeImage.setOnClickListener(v -> handleRoleSelection("attendee"));
		organizerImage.setOnClickListener(v -> handleRoleSelection("organizer"));
		adminImage.setOnClickListener(v -> handleRoleSelection("admin"));
	}

	/**
	 * Handles the role selection process.
	 * Depending on the role selected, it performs actions such as creating a new user or navigating to the EventMenuActivity.
	 * @param role The role selected by the user.
	 */

	private void handleRoleSelection(String role) {
		SharedPreferences prefs = getSharedPreferences("ConclavePrefs", MODE_PRIVATE);
		String userId = prefs.getString(PREF_USER_ID, null);

		if (isFirstTimeUser()) {
			// New user scenario
			userId = UUID.randomUUID().toString(); // Generate a new user ID
			prefs.edit().putString(PREF_USER_ID, userId).apply(); // Save the user ID locally

			// Perform anonymous authentication if not already done
			String finalUserId = userId;
			FirebaseAuth.getInstance().signInAnonymously()
					.addOnCompleteListener(this, task -> {
						if (task.isSuccessful()) {
							// Register user with the selected role
							createDefaultUser(role, finalUserId);
						} else {
							// Handle authentication failure
							Log.w(TAG, "signInAnonymously:failure", task.getException());
							Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
						}
					});
		} else {
			// Returning user scenario
			// Navigate to event menu directly or fetch additional details if necessary
			navigateToEventMenu(role, userId);
		}

	}

	/**
	 * Creates a default user in Firestore with the provided role and a generated user ID.
	 * @param role The role to be assigned to the user.
	 * @param userId The user ID generated for the new user.
	 */

	private void createDefaultUser(String role, String userId) {
		// Create a map to hold the user data
		Map<String, Object> newUser = new HashMap<>();
		newUser.put("userId", userId);
		newUser.put("role", role);
		newUser.put("registered", false); // Set as false for now, update based on your registration flow

		usersCollection.document(userId).set(newUser)
				.addOnSuccessListener(aVoid -> {
					// Navigate to event menu after successful registration
					navigateToEventMenu(role, userId);
					Intent intent = new Intent(RoleSelectionActivity.this, EventMenuActivity.class);
//					intent.putExtra("UserId", userId);
//					intent.putExtra("Role", role);
					startActivity(intent);
					// Document added successfully, show a toast message
					Toast.makeText(getApplicationContext(), "Default user created successfully", Toast.LENGTH_SHORT).show();
				})
				.addOnFailureListener(e -> {
					// Handle the failure to register
					Log.e("RoleSelectionActivity", "Error registering user", e);
					String errorMessage = "Error creating default user: " + e.getMessage();
					Log.e( errorMessage, String.valueOf(e));
					// Show error message as a Toast
					runOnUiThread(() -> {
						Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
					});
				});

	}

	/**
	 * Navigates to the EventMenuActivity.
	 * Passes the role and user ID as intent extras for use in the next activity.
	 * @param role The role of the user.
	 * @param userId The user ID of the user.
	 */
		private void navigateToEventMenu(String role, String userId) {
	// private void navigateToEventMenu(String role, String userId) {
		Intent intent = new Intent(this, EventMenuActivity.class);
		intent.putExtra("role", role);
//		intent.putExtra("userId", userId);
		startActivity(intent);
	}


	/**
	 * Checks if the app is being used for the first time by the user.
	 * Determines whether user data already exists in SharedPreferences.
	 * @return true if it is the first time the user is using the app, false otherwise.
	 */

	// Method to check if it's the user's first time using the app
	private boolean isFirstTimeUser() {
		SharedPreferences prefs = getSharedPreferences("ConclavePrefs", MODE_PRIVATE);
		return prefs.getString(PREF_USER_ID, null) == null;
	}


}

