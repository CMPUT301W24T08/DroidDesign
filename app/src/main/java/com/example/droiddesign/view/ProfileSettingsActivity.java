package com.example.droiddesign.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileSettingsActivity extends AppCompatActivity {

	private EditText editTextEmail;
	private String userId;
	private FirebaseFirestore db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_settings);

		db = FirebaseFirestore.getInstance();
		userId = getUserIdFromIntent();



		editTextEmail = findViewById(R.id.EditUserEmail);


		loadProfileSettings();

		ImageButton backButton = findViewById(R.id.button_back);
		backButton.setOnClickListener(v -> finish());
	}

	private void loadProfileSettings() {
		db.collection("Users").document(userId).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						String email = documentSnapshot.getString("email");

						editTextEmail.setText(email);
					} else {
						Log.w("ProfileSettings", "User does not exist.");
					}
				})
				.addOnFailureListener(e -> {
					Log.e("ProfileSettings", "Error loading settings", e);
				});
	}

	private String getUserIdFromIntent() {
		String userId = getIntent().getStringExtra("USER_ID");
		Log.d("ProfileSettingsActivity", "Fetched user ID: " + userId);
		return userId;
	}
}
