package com.example.droiddesign.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Represents an attendee of events, extending the generic User class with attendee-specific information.
 * It captures details pertinent to an attendee such as profile name, email, phone, etc.
 */
public class Attendee extends User {
	/**
	 * The profile name of the attendee.
	 */
	private String profileName;

	/**
	 * The email address of the attendee.
	 */
	private String email;

	/**
	 * The phone number of the attendee.
	 */
	private String phone;

	/**
	 * The URL or path to the profile picture of the attendee.
	 */
	private String profilePic;

	/**
	 * A flag indicating whether geolocation features are enabled for the attendee.
	 */
	private boolean geolocation;

	/**
	 * A list of event IDs that this attendee is associated with.
	 */
	private ArrayList<String> eventsList;


	/**
	 * Constructs an empty Attendee instance with default values.
	 */
	public Attendee() {
		super("", "");
		this.profileName = "";
		this.email = "";
		this.phone = "";
		this.profilePic = "";
		this.geolocation = false;
		this.eventsList = new ArrayList<>();
	}


	/**
	 * Constructs an Attendee instance with the specified user ID and role, initializing attendee-specific fields to default values.
	 *
	 * @param userId The unique identifier for the attendee.
	 * @param role The role of the attendee within the system.
	 */
	public Attendee(String userId, String role) {
		super(userId, role);
		this.profileName = "";
		this.email = "";
		this.phone = "";
		this.profilePic = "";
		this.geolocation = false;
		this.eventsList = new ArrayList<>();
	}

	/**
	 * Converts the attendee's data to a map structure for easy storage and retrieval.
	 *
	 * @return A map of attendee attributes to their respective values.
	 */
	@Override
	public HashMap<String, Object> toMap() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("userId",userId);
		map.put("role", role);
		map.put("email",email);
		map.put("phone",phone);
		map.put("profileName",profileName);
		map.put("profilePic",profilePic);
		map.put("eventsList",eventsList);
		return map;
	}


	/**
	 * Sets the profile name of the attendee.
	 *
	 * @param profileName The new profile name to be set.
	 */
	public void setProfileName(String profileName){
		this.profileName = profileName;
	}

	/**
	 * Gets the profile name of the attendee.
	 *
	 * @return The profile name of the attendee.
	 */
	public String getProfileName(){
		return profileName;
	}

	/**
	 * Sets the email address of the attendee.
	 *
	 * @param email The new email address to be set.
	 */
	public void setEmail(String email){
		this.email = email;
	}

	/**
	 * Gets the email address of the attendee.
	 *
	 * @return The email address of the attendee.
	 */
	public String getEmail(){
		return email;
	}


	/**
	 * Sets the phone number of the attendee.
	 *
	 * @param phone The new phone number to be set.
	 */
	public void setPhone(String phone){
		this.phone = phone;
	}

	/**
	 * Gets the phone number of the attendee.
	 *
	 * @return The phone number of the attendee.
	 */
	public String getPhone(){
		return phone;
	}

	/**
	 * Sets the URL or path to the profile picture of the attendee.
	 *
	 * @param profilePic The new URL or path to the profile picture.
	 */
	public void setProfilePic(String profilePic){
		this.profilePic = profilePic;
	}

	/**
	 * Sets the geolocation availability for the attendee.
	 *
	 * @param geolocation The new geolocation status to be set.
	 */
	public void setGeolocation(boolean geolocation){
		this.geolocation = geolocation;
	}

	/**
	 * Checks if geolocation is enabled for the attendee.
	 *
	 * @return true if geolocation is enabled, false otherwise.
	 */
	public boolean getGeolocation(){
		return geolocation;
	}

	/**
	 * Adds an event to the attendee's list of events by event ID.
	 *
	 * @param event The event to be added to the attendee's list.
	 */
	public void addEvent(Event event){
		eventsList.add(event.getEventId());
	}

	/**
	 * Gets the list of event IDs the attendee is associated with.
	 *
	 * @return The list of event IDs.
	 */
	public ArrayList<String> getEventsList(){
		return eventsList;
	}
}
