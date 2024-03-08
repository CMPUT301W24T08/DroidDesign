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
		userId = getUserIdFromIntent();
		String userRole = getUserRoleFromIntent();

		if (userId == null) {
			Toast.makeText(this, "User ID is missing.", Toast.LENGTH_SHORT).show();
		}
		if (userRole == null) {
			Toast.makeText(this, "User role is missing.", Toast.LENGTH_SHORT).show();
		}

	}

	private void saveProfileSettings() {
		String username = editTextUsername.getText().toString().trim();
		String phone = editTextPhone.getText().toString().trim();

		// Perform input validation here if needed

		Map<String, Object> userProfile = new HashMap<>();
		userProfile.put("Username", username);
		userProfile.put("Phone", phone);

		db.collection("usersgit ").document(userId)
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
		return getIntent().getStringExtra("userId");
	}

	private String getUserRoleFromIntent() {
		return getIntent().getStringExtra("userRole");
	}
}
