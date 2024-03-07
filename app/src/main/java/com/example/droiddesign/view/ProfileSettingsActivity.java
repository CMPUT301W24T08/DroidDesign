package com.example.droiddesign.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ProfileSettingsActivity extends AppCompatActivity {

	private EditText editTextUsername, editTextPhone;
	private Button btnSaveSettings;
	private String userId;
	private FirebaseFirestore db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_settings);

		db = FirebaseFirestore.getInstance();
		userId = getUserIdFromIntent(); // Assume this retrieves the userId passed from the previous activity


		// Initialize form fields
		loadProfileSettings();

		btnSaveSettings.setOnClickListener(v -> {
			saveProfileSettings();
		});
	}

	private void loadProfileSettings() {
		// Load the current profile settings for the user from Firestore
		db.collection("users").document(userId).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						String username = documentSnapshot.getString("Username");
						String phone = documentSnapshot.getString("Phone");

						editTextUsername.setText(username);
						editTextPhone.setText(phone);
					}
				})
				.addOnFailureListener(e -> Log.e("ProfileSettings", "Error loading settings", e));
	}

	private void saveProfileSettings() {
		String username = editTextUsername.getText().toString().trim();
		String phone = editTextPhone.getText().toString().trim();

		// Perform input validation here if needed

		Map<String, Object> userProfile = new HashMap<>();
		userProfile.put("Username", username);
		userProfile.put("Phone", phone);

		db.collection("users").document(userId)
				.set(userProfile, SetOptions.merge())
				.addOnSuccessListener(aVoid -> {
					Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
					finish(); // Close the activity or navigate the user elsewhere
				})
				.addOnFailureListener(e -> {
					Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
				});
	}

	private String getUserIdFromIntent() {
		// Get the userId from the intent that started this activity
		return getIntent().getStringExtra("USER_ID");
	}
}
