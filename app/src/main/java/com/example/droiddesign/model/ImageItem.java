package com.example.droiddesign.model;

public class ImageItem {
	private String imageUrl;
	private String ownerId; // Attribute to store the owner ID

	// Constructor
	public ImageItem(String imageUrl, String ownerId) {
		this.imageUrl = imageUrl;
		this.ownerId = ownerId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
}
