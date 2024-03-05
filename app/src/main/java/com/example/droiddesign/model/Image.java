package com.example.droiddesign.model;

public abstract class Image {
	protected String imageId;

	// Constructor
	public Image(String imageId) {
		this.imageId = imageId;
	}

	// Abstract method to upload image data to Firestore
	public abstract void uploadImage();

	// Abstract method to download image data from Firestore
	public abstract void downloadImage();

	// Getter and setter for imageId
	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
}
