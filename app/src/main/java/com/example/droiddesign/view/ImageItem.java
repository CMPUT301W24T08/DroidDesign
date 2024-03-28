package com.example.droiddesign.view;

public class ImageItem {
	private String imageUrl;

	// Constructor
	public ImageItem(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	// Getter for the image URL
	public String getImageUrl() {
		return imageUrl;
	}

	// Optionally, if you might want to set or change the URL later
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
