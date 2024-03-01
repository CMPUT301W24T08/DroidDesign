package com.example.droiddesign.model;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileImage extends Image {
	private List<String> associatedUserIds;

	public ProfileImage(String imageId) {
		super(imageId);
	}

	@Override
	public void uploadImage() {
		// Obtain an instance of FirebaseFirestore
		FirebaseFirestore db = FirebaseFirestore.getInstance();

		// Create a map to hold the data you want to set
		Map<String, Object> imageData = new HashMap<>();
		imageData.put("imageId", getImageId());
		imageData.put("associatedUserIds", associatedUserIds);

		// Add a new document with the imageId in the "profileImages" collection
		db.collection("profileImages").document(getImageId()).set(imageData)
				.addOnSuccessListener(aVoid -> {
					// Handle successful upload, e.g., show a message to the user
				})
				.addOnFailureListener(e -> {
					// Handle failed upload, e.g., show an error message to the user
				});
	}

	@Override
	public void downloadImage() {
		// Obtain an instance of FirebaseFirestore
		FirebaseFirestore db = FirebaseFirestore.getInstance();

		// Get the document with the specified ID from the "profileImages" collection
		db.collection("profileImages").document(getImageId()).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						// Set the associatedUserIds with the value from Firestore
						associatedUserIds = (List<String>) documentSnapshot.get("associatedUserIds");
						// Now you can use associatedUserIds in your application
					} else {
						// Handle the case where the profile image does not exist
					}
				})
				.addOnFailureListener(e -> {
					// Handle any errors that may occur, e.g., show an error message to the user
				});
	}

	// Getters and Setters
	public List<String> getAssociatedUserIds() {
		return associatedUserIds;
	}

	public void setAssociatedUserIds(List<String> associatedUserIds) {
		this.associatedUserIds = associatedUserIds;
	}
}
