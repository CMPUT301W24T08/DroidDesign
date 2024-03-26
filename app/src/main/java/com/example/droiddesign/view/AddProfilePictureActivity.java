package com.example.droiddesign.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.droiddesign.controller.MessageEvent;


import com.example.droiddesign.R;
import com.github.dhaval2404.imagepicker.ImagePicker;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.UUID;


import org.greenrobot.eventbus.EventBus;

public class AddProfilePictureActivity extends AppCompatActivity {

    private ImageView imageView;
    private Uri imageUri;
    private StorageReference mStorageRef;

    private String imageUrl;

    private boolean isImageSaved = false;

    private String existingImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile_picture);

        imageView = findViewById(R.id.profile_image_view);
        Button editButton = findViewById(R.id.edit_image_button);
        Button saveButton = findViewById(R.id.save_button);
        Button backButton = findViewById(R.id.back_button);

        Intent intent = getIntent();
        existingImageUrl = intent.getStringExtra("image_url");
        Log.d("ProfileActivity", "Received image URL: " + existingImageUrl);
        if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
            Glide.with(this).load(existingImageUrl).into(imageView);
        }

        mStorageRef = FirebaseStorage.getInstance().getReference("profile-pics");

        editButton.setOnClickListener(view -> ImagePicker.with(AddProfilePictureActivity.this)
                .crop()  // Crop image (optional)
                .compress(1024)  // Final image size will be less than 1 MB (optional)
                .maxResultSize(1080, 1080)  // Final image resolution will be less than 1080 x 1080 (optional)
                .start());

        saveButton.setOnClickListener(v -> saveProfilePicture());

        backButton.setOnClickListener(v -> finish());
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
                    imageUrl = uri.toString();
                    EventBus.getDefault().post(new MessageEvent(imageUrl)); // Send the new image URL
                    finish();
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
            imageView.setImageURI(imageUri);
            saveProfilePicture();  // This will save the new profile picture and update Firebase
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isImageSaved && existingImageUrl != null) {
            EventBus.getDefault().post(new MessageEvent(existingImageUrl));
        }
    }
}