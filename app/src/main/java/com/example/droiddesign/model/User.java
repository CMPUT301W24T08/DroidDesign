package com.example.droiddesign.model;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a generic user in the system with common attributes like user ID and role.
 * This class is abstract and intended to be subclassed by specific types of users.
 */
public abstract class User {
	/**
	 * Unique identifier for the user.
	 */
	protected String userId;

	/**
	 * The primary role of the user in the system.
	 */
	protected String role;

	/**
	 * A list of roles associated with the user. Though not initialized or used in the given code,
	 * this implies that users might have multiple roles.
	 */
	protected ArrayList<String> rolesList;

	/**
	 * Constructs a User instance with the specified user ID and role.
	 *
	 * @param userId The unique identifier for the user.
	 * @param role The role of the user within the system.
	 */
	public User(String userId, String role) {
		this.userId = userId;
		this.role = role;
	}

	/**
	 * Retrieves the user ID.
	 *
	 * @return The user ID.
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Retrieves the user's role.
	 *
	 * @return The role of the user.
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Converts the user object into a map structure suitable for database storage.
	 * This method is abstract and must be implemented by subclasses.
	 *
	 * @return A map representation of the user object.
	 */
	public abstract HashMap<String, Object> toMap();
}

