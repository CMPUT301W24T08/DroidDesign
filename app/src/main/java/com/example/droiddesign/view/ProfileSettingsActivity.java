package com.example.droiddesign.view; // Make sure this matches your actual package structure

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R; // Ensure this is the correct R import for your resources
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileSettingsActivity extends AppCompatActivity {

	private EditText editUsername, editUserEmail, editUserContactNumber;
	private FirebaseFirestore db;
	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_settings); // Verify this is the correct layout name

		db = FirebaseFirestore.getInstance();
		userId = getUserIdFromIntent(); // Make sure you have the USER_ID intent set correctly when starting this activity

		// Initialize the EditText fields; verify that the IDs are correct
		editUsername = findViewById(R.id.editUsername);
		editUserEmail = findViewById(R.id.editUserEmail);
		editUserContactNumber = findViewById(R.id.editUserContactNumber);

		loadProfileSettings(); // Implement this method to load settings from Firestore

		ImageButton backButton = findViewById(R.id.button_back);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		Button saveButton = findViewById(R.id.buttonSave);
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveProfileSettings(); // Implement this method to save settings to Firestore
			}
		});
	}

	private void loadProfileSettings() {
		// Implementation to load user settings from Firestore...
	}

	private void saveProfileSettings() {
		String newUsername = editUsername.getText().toString();
		String newUserEmail = editUserEmail.getText().toString();
		String newUserContactNumber = editUserContactNumber.getText().toString();

		// Implementation to save the updated settings back to Firestore...
	}

	private String getUserIdFromIntent() {
		return getIntent().getStringExtra("USER_ID"); // Ensure USER_ID is the key used when passing the intent
	}
}
