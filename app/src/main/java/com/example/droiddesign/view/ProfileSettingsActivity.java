package com.example.droiddesign.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.droiddesign.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileSettingsActivity extends AppCompatActivity {

    private EditText editUsername, editUserEmail, editUserContactNumber, editUserCompany;
    private Button editProfileButton, saveButton;
    private ImageButton backButton;
    private ImageView profileImageView;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize EditText fields
        editUsername = findViewById(R.id.editUsername);
        editUserEmail = findViewById(R.id.editUserEmail);
        editUserContactNumber = findViewById(R.id.editUserContactNumber);
        editUserCompany = findViewById(R.id.editUserCompany);
        profileImageView = findViewById(R.id.profile_image_view);

        // Initialize Buttons
        editProfileButton = findViewById(R.id.edit_profile_button);
        saveButton = findViewById(R.id.buttonSave);
        backButton = findViewById(R.id.button_back);

        // Set initial state of EditTexts to be non-editable
        setEditingEnabled(false);

        // Load existing profile settings
        if (currentUser != null) {
            loadProfileSettings();
        }

        // Set up button listeners
        backButton.setOnClickListener(v -> finish());
        editProfileButton.setOnClickListener(v -> setEditingEnabled(true));
        saveButton.setOnClickListener(v -> {
            saveProfileSettings();
            setEditingEnabled(false); // Disable editing after save
        });
    }

    private void setEditingEnabled(boolean isEnabled) {
        editUsername.setEnabled(isEnabled);
        editUserEmail.setEnabled(isEnabled);
        editUserContactNumber.setEnabled(isEnabled);
        editUserCompany.setEnabled(isEnabled);
        saveButton.setVisibility(isEnabled ? View.VISIBLE : View.INVISIBLE);
    }

    private void loadProfileSettings() {
        // Fetch user settings from Firestore and populate EditTexts

        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            editUsername.setText(documentSnapshot.getString("username"));
                            editUserEmail.setText(documentSnapshot.getString("email"));
                            editUserContactNumber.setText(documentSnapshot.getString("contactNumber"));
                            editUserCompany.setText(documentSnapshot.getString("company"));

                            // Load profile picture with Glide
                            String profilePicUrl = documentSnapshot.getString("profilePicUrl");
                            if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                                Glide.with(this).load(profilePicUrl).into(profileImageView);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }
    }

    private void saveProfileSettings() {
        // Save updated settings to Firestore
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String newUsername = editUsername.getText().toString();
            String newUserEmail = editUserEmail.getText().toString();
            String newUserContactNumber = editUserContactNumber.getText().toString();
            String newUserCompany = editUserCompany.getText().toString();

            db.collection("users").document(userId)
                    .update("username", newUsername, "email", newUserEmail, "contactNumber", newUserContactNumber, "company", newUserCompany)
                    .addOnSuccessListener(aVoid -> {
                        // Handle success
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }
    }
}
