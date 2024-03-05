package com.example.droiddesign.model;

import com.google.firebase.firestore.FirebaseFirestore;

public abstract class User {
	protected String userId;
	protected String email;
	protected String phone;

	// Firestore instance
	protected transient FirebaseFirestore db = FirebaseFirestore.getInstance();

	// Constructor, getters, and setters
	public User() {
		// Default constructor
	}

	// Method to save the user to Firestore
	public abstract void saveToFirestore();

	// Method to load the user from Firestore
	public abstract void loadFromFirestore(String userId);

	// Common setters and getters for all subclasses
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}