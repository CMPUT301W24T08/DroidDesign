package com.example.droiddesign.model;

public class OrganizerMessage {
	private String date;
	private String text;
	private String imageUploadedPictureID;

	// Constructor, getters, and setters

	public OrganizerMessage(String date, String text, String imageUploadedPictureID) {
		this.date = date;
		this.text = text;
		this.imageUploadedPictureID = imageUploadedPictureID;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getImageUploadedPictureID() {
		return imageUploadedPictureID;
	}

	public void setImageUploadedPictureID(String imageUploadedPictureID) {
		this.imageUploadedPictureID = imageUploadedPictureID;
	}
}
