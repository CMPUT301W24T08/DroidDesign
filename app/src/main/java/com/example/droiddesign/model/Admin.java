package com.example.droiddesign.model;

import java.util.HashMap;

/**
 * Represents an administrator in the system, extending the generic User class with admin-specific functionality.
 * This class captures additional administrative attributes and behaviors.
 */
public class Admin extends User {

	/**
	 * The type of administrator, describing the admin's specific role or permissions within the system.
	 */
	private String type; // You might want to utilize this field or remove the comment if it's not used.

	/**
	 * Constructs a new Admin instance with specified user ID and role.
	 * Initializes the User object with the provided userId and role specific to the admin.
	 *
	 * @param userId The unique identifier for the admin user.
	 * @param role The role of the admin within the system.
	 */
	public Admin(String userId, String role) {
		super(userId, role);
	}

	/**
	 * Converts the admin user data to a map structure for easy storage and retrieval.
	 * Includes essential user attributes like user ID and role in the mapping.
	 *
	 * @return A map of admin user attributes to their respective values.
	 */
	@Override
	public HashMap<String, Object> toMap() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("role", role);
		// If you decide to use 'type', don't forget to include it in the map.
		return map;
	}

	// If you plan to utilize the 'type' field, you should have getter and setter for it,
	// and also include it in the toMap() method. Here's an example of how you might document those:

	/**
	 * Gets the type of the admin.
	 *
	 * @return The type of admin.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type of the admin.
	 *
	 * @param type The type to set for this admin.
	 */
	public void setType(String type) {
		this.type = type;
	}
}


