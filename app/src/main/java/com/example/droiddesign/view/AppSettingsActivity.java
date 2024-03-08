package com.example.droiddesign.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.droiddesign.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppSettingsActivity extends AppCompatActivity {

	private SwitchCompat switchGeolocation;
	private String userId;
	private FirebaseFirestore db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_settings);

		db = FirebaseFirestore.getInstance();
		userId = getUserIdFromIntent(); // Assume this retrieves the userId passed from the previous activity

		// Find the back button by its ID
		ImageButton backButton = findViewById(R.id.button_back);

		// Set an OnClickListener on the backButton
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// This will close the current activity and go back to the previous screen
				finish();
			}
		});

//		switchGeolocation = findViewById(R.id.switch_geolocation); TODO: for later requirement
//
//		// Initialize the switch position
//		loadSettings();
//
//		// Set a listener on the switch to update settings in Firestore
//		switchGeolocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
//			Map<String, Object> appSettings = new HashMap<>();
//			appSettings.put("geolocation_enabled", isChecked);
//
//			db.collection("app_settings").document(userId)
//					.set(appSettings, SetOptions.merge())
//					.addOnSuccessListener(aVoid -> Log.d("AppSettings", "Settings updated"))
//					.addOnFailureListener(e -> Log.e("AppSettings", "Error updating settings", e));
//		});
	}

	private void loadSettings() {
		// Load the current settings for the user from Firestore and set the switch position
		db.collection("app_settings").document(userId).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						Boolean geolocationEnabled = documentSnapshot.getBoolean("geolocation_enabled");
						switchGeolocation.setChecked(geolocationEnabled != null && geolocationEnabled);
					}
				})
				.addOnFailureListener(e -> Log.e("AppSettings", "Error loading settings", e));
	}

	private String getUserIdFromIntent() {
		// Get the userId from the intent that started this activity
		return getIntent().getStringExtra("USER_ID");
	}
}

