package com.example.droiddesign.model;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public abstract class User {
	protected String userId;
	protected String userName;
	protected Boolean registered;
	protected String email;
	protected String phone;

	// Firestore collection name
	private static final String COLLECTION_NAME = "User";

	// Firestore instance
	protected transient FirebaseFirestore db = FirebaseFirestore.getInstance();

	// Default constructor
	public User() {
	}

	public User(String userId, String userName, Boolean registered, String email, String phone) {
		this.userId = userId;
		this.userName = userName;
		this.registered = registered;
		this.email = email;
		this.phone = phone;
	}

	// Method to save the user to Firestore
	public void saveToFirestore() {
		// Create a map to store user data
		Map<String, Object> userData = new HashMap<>();
		userData.put("UserID", this.userId);
		userData.put("Username", this.userName);
		userData.put("Email", this.email);
		userData.put("Phone", this.phone);
		userData.put("Registered", this.registered);
		// Add other fields like Type, Geolocation, AttendedEventsList, and OrganizedEventsList as needed

		// Save to Firestore
		db.collection(COLLECTION_NAME).document(this.userId).set(userData)
				.addOnSuccessListener(aVoid -> System.out.println("User saved successfully"))
				.addOnFailureListener(e -> System.out.println("Error saving user: " + e.getMessage()));
	}

	// Method to load the user from Firestore
	public void loadFromFirestore(String userId) {
		DocumentReference docRef = db.collection(COLLECTION_NAME).document(userId);
		docRef.get().addOnCompleteListener(task -> {
			if (task.isSuccessful() && task.getResult() != null) {
				Map<String, Object> data = task.getResult().getData();
				if (data != null) {
					this.userId = (String) data.get("UserID");
					this.userName = (String) data.get("Username");
					this.email = (String) data.get("Email");
					this.phone = (String) data.get("Phone");
					this.registered = (Boolean) data.get("Registered");
					// Handle other fields like Type, Geolocation, AttendedEventsList, and OrganizedEventsList
				}
			} else {
				System.out.println("Error loading user: " + task.getException());
			}
		});
	}

	// Getters and setters
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Boolean getRegistered() {
		return registered;
	}

	public void setRegistered(Boolean registered) {
		this.registered = registered;
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
