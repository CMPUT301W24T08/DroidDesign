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

/**
 * The AppSettingsActivity class provides a user interface for the user to change application-related settings.
 * Currently, this class includes a method for obtaining a user ID from the intent and setting up the activity's
 * back button functionality. Future implementations can include more settings adjustments based on user preferences.
 */
public class AppSettingsActivity extends AppCompatActivity {

	/**
	 * Switch to toggle geolocation settings (commented out for potential future use).
	 */
	private SwitchCompat switchGeolocation;

	/**
	 * The user's ID, utilized for retrieving and storing specific settings for the user.
	 */
	private String userId;

	/**
	 * Instance of FirebaseFirestore to interact with Firebase Firestore database.
	 */
	private FirebaseFirestore db;

	/**
	 * Called when the activity is starting. This is where most initialization should go:
	 * calling setContentView(int) to inflate the activity's UI, using findViewById(int)
	 * to programmatically interact with widgets in the UI, setting up listeners, and so on.
	 *
	 * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
	 *                           this Bundle contains the most recent data supplied in onSaveInstanceState(Bundle).
	 *                           Note: Otherwise it is null.
	 */
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

	/**
	 * Loads the user's settings from Firestore and sets the switch's position accordingly.
	 */
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

	/**
	 * Retrieves the user ID from the intent that started this activity.
	 *
	 * @return The user ID passed through the intent.
	 */
	private String getUserIdFromIntent() {
		// Get the userId from the intent that started this activity
		return getIntent().getStringExtra("USER_ID");
	}
}

