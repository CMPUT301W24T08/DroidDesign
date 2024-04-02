package com.example.droiddesign.view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import com.example.droiddesign.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AppSettingsActivity extends AppCompatActivity {

	private SwitchCompat switchGeolocation;
	private Spinner spinnerNotificationPreference;
	private FirebaseFirestore db; // Firestore database reference
	private String currentUserId = getCurrentUserId();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_settings);

		// Initialize Firestore
		db = FirebaseFirestore.getInstance();

		// Setup UI components
		setupBackButton();
		setupSwitchGeolocation();
		setupSpinner();

		// Load user settings
		loadUserSettings();
	}

	private void setupBackButton() {
		ImageButton backButton = findViewById(R.id.button_back);
		backButton.setOnClickListener(v -> finish());
	}

	private void setupSwitchGeolocation() {
		switchGeolocation = findViewById(R.id.switch_geo_location);
		Drawable thumbDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.thumb_selector);
		Drawable trackDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.track_selector);
		switchGeolocation.setThumbDrawable(thumbDrawable);
		switchGeolocation.setTrackDrawable(trackDrawable);
	}

	private void setupSpinner() {
		spinnerNotificationPreference = findViewById(R.id.settings_spinner);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this,
				R.layout.custom_spinner_item,
				new String[]{"Selected Events", "None", "All Events"});
		adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
		spinnerNotificationPreference.setAdapter(adapter);
	}

	private void updateFirestore(String field, Object value) {
		Map<String, Object> updates = new HashMap<>();
		updates.put(field, value);
		db.collection("Users").document(currentUserId)
				.update(updates)
				.addOnSuccessListener(aVoid -> Log.d("Firestore", "Successfully updated " + field))
				.addOnFailureListener(e -> Log.e("Firestore", "Error updating " + field, e));
	}

	private String getCurrentUserId() {
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if (user != null) {
			return user.getUid();
		} else {
			return null; // Handle this case properly in your app
		}
	}

	private void loadUserSettings() {
		db.collection("Users").document(currentUserId).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						Boolean geolocationEnabled = documentSnapshot.getBoolean("geolocation");
						String notificationPreference = documentSnapshot.getString("notificationPreference");

						if (geolocationEnabled != null) {
							switchGeolocation.setChecked(geolocationEnabled);
						}

						if (notificationPreference != null) {
							int position = ((ArrayAdapter<String>) spinnerNotificationPreference.getAdapter())
									.getPosition(notificationPreference);
							spinnerNotificationPreference.setSelection(position);
						}

						// Setup the listeners after settings have been loaded to avoid overriding them upon initialization
						setupListeners();
					}
				})
				.addOnFailureListener(e -> Log.e("AppSettings", "Error loading user settings", e));
	}

	private void setupListeners() {
		switchGeolocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
			updateFirestore("geolocation", isChecked);
			Toast.makeText(AppSettingsActivity.this,
					"Geolocation is " + (isChecked ? "enabled" : "disabled"),
					Toast.LENGTH_SHORT).show();
		});

		spinnerNotificationPreference.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedItem = parent.getItemAtPosition(position).toString();
				updateFirestore("notificationPreference", selectedItem);
				Toast.makeText(AppSettingsActivity.this,
						"Selected notification preference: " + selectedItem,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				//do nothing
			}
		});
	}
}
