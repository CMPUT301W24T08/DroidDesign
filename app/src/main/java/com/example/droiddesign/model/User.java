package com.example.droiddesign.model;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class User {
	protected String userId;
	protected String role;

	protected ArrayList<String> rolesList;


	// Default constructor

	public User(String userId, String role) {
		this.userId = userId;
		this.role = role;
	}



	// Getters and setters
	public String getUserId() {
		return userId;
	}

	public String getRole(){
		return role;
	}


	public abstract HashMap<String, Object> toMap();


}
