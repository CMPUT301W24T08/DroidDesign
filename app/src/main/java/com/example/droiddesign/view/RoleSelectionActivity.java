package com.example.droiddesign.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RoleSelectionActivity extends AppCompatActivity {

	private MaterialButton adminImage, organizerImage, attendeeImage;
	private FirebaseFirestore db = FirebaseFirestore.getInstance();
	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_role_selection_unregistered);

		userId = getIntent().getStringExtra("UserId");
		checkUserRegistrationStatus(userId);

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
		if (userId == null) {
			userId = UUID.randomUUID().toString(); // Generate a new user ID for unregistered users
		}
		// Register user with the selected role and navigate to event menu
		registerUserAndNavigate(role, userId);
	}

	private void registerUserAndNavigate(String role, String userId) {
		Map<String, Object> newUser = new HashMap<>();
		newUser.put("userId", userId);
		newUser.put("role", role);
		newUser.put("registered", false); // Set as false for now, update based on your registration flow

		db.collection("usersDB").document(userId).set(newUser)
				.addOnSuccessListener(aVoid -> {
					// Navigate to event menu after successful registration
					Intent intent = new Intent(RoleSelectionActivity.this, EventMenuActivity.class);
					intent.putExtra("UserId", userId);
					intent.putExtra("Role", role);
					startActivity(intent);
				})
				.addOnFailureListener(e -> {
					// Handle the failure to register
					Log.e("RoleSelectionActivity", "Error registering user", e);
				});
	}

	private void checkUserRegistrationStatus(String userId) {
		if (userId == null) {
			setContentView(R.layout.activity_role_selection_unregistered);
			return; // Stop further execution and wait for user to select a role
		}

		// Proceed with fetching user details from Firestore if userId is not null
		fetchUserDetailsAndSetupUI(userId);
	}

	private void fetchUserDetailsAndSetupUI(String userId) {
		db.collection("usersDB").document(userId).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						Boolean registered = documentSnapshot.getBoolean("registered");
						if (Boolean.TRUE.equals(registered)) {
							setContentView(R.layout.activity_role_selection);
						} else {
							setContentView(R.layout.activity_role_selection_unregistered);
						}
						// After setting content view, re-initialize views and set listeners
						setRoleSelectionListeners();
					} else {
						new AlertDialog.Builder(this)
								.setTitle("Error")
								.setMessage("User Status error")
								.setPositiveButton(android.R.string.ok, null)
								.setIcon(android.R.drawable.ic_dialog_alert)
								.show();
					}
				})
				.addOnFailureListener(e -> Log.e("RoleSelectionActivity", "Error fetching user data", e));
	}
}

