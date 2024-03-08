package com.example.droiddesign.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents an organizer user in the system, extending the generic User class with additional fields and functionalities.
 * Organizers can manage events, including creating and updating them, within the platform.
 */
public class Organizer extends User {
	/**
	 * The username of the organizer.
	 */
	private String username;

	/**
	 * The email address of the organizer.
	 */
	private String email;

	/**
	 * The phone number of the organizer.
	 */
	private String phone;

	/**
	 * The URL or identifier for the organizer's profile picture.
	 */
	private String profilePic;

	/**
	 * Flag indicating whether geolocation services are enabled for the organizer.
	 */
	private boolean geolocation;

	/**
	 * A list of event IDs that the organizer is managing.
	 */
	private ArrayList<String> eventsIdList;

	/**
	 * Constructs an Organizer instance with specified user ID, role, and email, initializing other fields to default values.
	 *
	 * @param userId The unique identifier for the user.
	 * @param role The role of the user within the system. Expected to be "organizer" for instances of this class.
	 * @param email The email address of the organizer.
	 */
	public Organizer(String userId, String role, String email) {
		super(userId, role);
		this.username = "";
		this.email = email;
		this.phone = "";
		this.profilePic = "";
		this.geolocation = false;
		this.eventsIdList = new ArrayList<>();
	}

	/**
	 * Converts the organizer object into a map structure suitable for database storage, including additional fields specific to an organizer.
	 *
	 * @return A map representation of the organizer object.
	 */
	@Override
	public HashMap<String, Object> toMap() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("role", role);
		map.put("email", email);
		map.put("phone", phone);
		map.put("username", username);
		map.put("profilePic", profilePic);
		map.put("geolocation", geolocation);
		map.put("eventsList", eventsIdList);
		return map;
	}

	/**
	 * Adds an event to the organizer's list of events.
	 *
	 * @param event The event to be added.
	 */
	public void addEvent(Event event){
		eventsIdList.add(event.getEventId());
	}
}

