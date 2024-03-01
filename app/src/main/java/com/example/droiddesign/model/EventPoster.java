package com.example.droiddesign.model;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EventPoster extends Image {
	// Additional fields specific to EventPoster
	private String posterUrl;

	public EventPoster(String imageId) {
		super(imageId);
	}

	@Override
	public void uploadImage() {
		// Implementation for uploading an event poster image
		// Obtain an instance of FirebaseFirestore
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		// Create a map to hold the data you want to set
		Map<String, Object> poster = new HashMap<>();
		poster.put("imageId", getImageId());
		poster.put("posterUrl", posterUrl);

		// Add a new document with a generated ID in a collection named "posters"
		db.collection("posters").document(getImageId()).set(poster)
				.addOnSuccessListener(aVoid -> {
					// Handle successful upload
				})
				.addOnFailureListener(e -> {
					// Handle failed upload
				});
	}

	@Override
	public void downloadImage() {
		// Implementation for downloading an event poster image
		// Obtain an instance of FirebaseFirestore
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		// Get the document with the specified ID from the "posters" collection
		db.collection("posters").document(getImageId()).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						// Set the posterUrl with the value from Firestore
						posterUrl = documentSnapshot.getString("posterUrl");
						// Now you can use posterUrl in your application
					} else {
						// Handle the case where the poster does not exist
					}
				})
				.addOnFailureListener(e -> {
					// Handle any errors that may occur
				});
	}
	// Getter and setter for posterUrl
	public String getPosterUrl() {
		return posterUrl;
	}

	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}
}
