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

public class RoleSelectionActivity extends AppCompatActivity {

	private MaterialButton adminImage, organizerImage, attendeeImage;
	private final FirebaseFirestore db = FirebaseFirestore.getInstance();
	// Construct the reference to the collection where user documents are stored
	CollectionReference usersCollection = db.collection("users");

	// Retrieve the document snapshot asynchronously
	private static final String PREF_USER_ID = "defaultID";
//	SharedPreferences prefs = getSharedPreferences(PREF_USER_ID, MODE_PRIVATE);

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

	private void setRoleSelectionListeners() {
		attendeeImage.setOnClickListener(v -> handleRoleSelection("attendee"));
		organizerImage.setOnClickListener(v -> handleRoleSelection("organizer"));
		adminImage.setOnClickListener(v -> handleRoleSelection("admin"));
	}

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

	private void navigateToEventMenu(String role, String userId) {
		Intent intent = new Intent(this, EventMenuActivity.class);
//		intent.putExtra("role", role);
//		intent.putExtra("userId", userId);
		startActivity(intent);
	}


	// Method to check if it's the user's first time using the app
	private boolean isFirstTimeUser() {
		SharedPreferences prefs = getSharedPreferences("ConclavePrefs", MODE_PRIVATE);
		return prefs.getString(PREF_USER_ID, null) == null;
	}


}

