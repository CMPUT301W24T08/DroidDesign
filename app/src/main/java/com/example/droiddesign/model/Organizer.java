package com.example.droiddesign.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Organizer extends User {
	private String username;
	private String email;
	private String phone;
	private String profilePic;
	private boolean geolocation;
	private ArrayList<String> eventsIdList;


	public Organizer(String userId, String role, String email) {
		super(userId, role);
		this.username = "";
		this.email = email;
		this.phone = "";
		this.profilePic = "";
		this.geolocation = false;
		this.eventsIdList = new ArrayList<>();
	}

	@Override
	public HashMap<String, Object> toMap() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("userId",userId);
		map.put("role", role);
		map.put("email",email);
		map.put("phone",phone);
		map.put("username",username);
		map.put("profilePic",profilePic);
		map.put("geolocation",geolocation);
		map.put("eventsList",eventsIdList);
		return map;
	}

	public void addEvent(Event event){
		eventsIdList.add(event.getEventId());
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public boolean isGeolocation() {
		return geolocation;
	}

	public void setGeolocation(boolean geolocation) {
		this.geolocation = geolocation;
	}

	public ArrayList<String> getEventsIdList() {
		return eventsIdList;
	}

	public void setEventsIdList(ArrayList<String> eventsIdList) {
		this.eventsIdList = eventsIdList;
	}
}
