package com.example.droiddesign.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.droiddesign.R;
import com.example.droiddesign.controller.MessageEvent;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

public class ProfileSettingsActivity extends AppCompatActivity {

    private EditText editUsername, editUserEmail, editUserContactNumber, editUserCompany, editDisplayUserName, editDisplayUserCompany;
    private Button saveButton, editProfilePicButton, deleteProfilePicButton;
    private ImageButton backButton;
    private ImageView profileImageView;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private Uri imageUri;

    String profilePicUrl;
    private StorageReference mStorageRef;

    private String imageUrl;

    private boolean isImageSaved = false;

    String userId;

    String avatarUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
        avatarUrl = "https://robohash.org/" + userId;

        mStorageRef = FirebaseStorage.getInstance().getReference("profile-pics");


        // Initialize EditText fields
        editUsername = findViewById(R.id.editUsername);
        editUserEmail = findViewById(R.id.editUserEmail);
        editUserContactNumber = findViewById(R.id.editUserContactNumber);
        editUserCompany = findViewById(R.id.editUserCompany);
        editDisplayUserName= findViewById(R.id.UserDisplayName);
        editDisplayUserCompany = findViewById(R.id.UserCompanyDisplay);
        profileImageView = findViewById(R.id.profile_image_view);
        editProfilePicButton = findViewById(R.id.edit_image_button);
        deleteProfilePicButton = findViewById(R.id.delete_image_button);

        deleteProfilePicButton.setOnClickListener(v -> {

            Glide.with(this).load(avatarUrl).into(profileImageView);

            if (currentUser != null) {
                db.collection("Users").document(currentUser.getUid())
                        .update("profilePic", avatarUrl)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ProfileSettingsActivity.this, "Profile picture removed.", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ProfileSettingsActivity.this, "Failed to remove profile picture.", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        editProfilePicButton.setOnClickListener(view -> ImagePicker.with(ProfileSettingsActivity.this)
                .crop()  // Crop image (optional)
                .compress(1024)  // Final image size will be less than 1 MB (optional)
                .maxResultSize(1080, 1080)  // Final image resolution will be less than 1080 x 1080 (optional)
                .start());



        editUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editDisplayUserName.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editUserCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editDisplayUserCompany.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editDisplayUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newText = charSequence.toString();
                if (!editUsername.getText().toString().equals(newText)) {
                    editUsername.setText(newText);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editDisplayUserCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newText = charSequence.toString();
                if (!editUserCompany.getText().toString().equals(newText)) {
                    editUserCompany.setText(newText);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        // Initialize Buttons
        Button editProfileButton = findViewById(R.id.edit_profile_button);
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
        editDisplayUserName.setEnabled(isEnabled);
        editDisplayUserCompany.setEnabled(isEnabled);
        saveButton.setVisibility(isEnabled ? View.VISIBLE : View.INVISIBLE);
    }

    private void loadProfileSettings() {
        // Fetch user settings from Firestore and populate EditTexts

        if (currentUser != null) {
            db.collection("Users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            editUsername.setText(documentSnapshot.getString("userName"));
                            editUserEmail.setText(documentSnapshot.getString("email"));
                            editUserContactNumber.setText(documentSnapshot.getString("phone"));
                            editUserCompany.setText(documentSnapshot.getString("company"));
                            editDisplayUserName.setText(documentSnapshot.getString("userName"));
                            editDisplayUserCompany.setText(documentSnapshot.getString("company"));

                            // Load profile picture with Glide
                            profilePicUrl = documentSnapshot.getString("profilePic");

                            if (profilePicUrl == null || profilePicUrl.isEmpty()) {
                                profilePicUrl = avatarUrl;
                            }
                            Glide.with(this).load(profilePicUrl).into(profileImageView);
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
            String newUsername = editUsername.getText().toString();
            String newUserEmail = editUserEmail.getText().toString();
            String newUserContactNumber = editUserContactNumber.getText().toString();
            String newUserCompany = editUserCompany.getText().toString();

            db.collection("Users").document(userId)
                    .update("userName", newUsername, "email", newUserEmail, "phone", newUserContactNumber, "company", newUserCompany, "profilePic", profilePicUrl)
                    .addOnSuccessListener(aVoid -> {
                        // Handle success
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }
    }

    private void saveProfilePicture() {

        if (imageUri == null) {
            Toast.makeText(this, "Please upload a picture first", Toast.LENGTH_SHORT).show();
            return;
        }
        String filename = UUID.randomUUID().toString() + ".png";
        StorageReference fileRef = mStorageRef.child(filename);

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    isImageSaved = true;
                    imageUrl = uri.toString();
                    profilePicUrl = imageUrl;
                    String userId = currentUser.getUid();
                    db.collection("Users").document(userId)
                            .update("profilePic", imageUrl)
                            .addOnSuccessListener(aVoid -> {
                                // Handle success
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                            });

                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
            saveProfilePicture();
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
