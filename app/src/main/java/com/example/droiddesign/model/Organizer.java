package com.example.droiddesign.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Organizer extends User {
	private String username;
	private String email;
	private String phone;
	private String profilePic;
	private boolean geolocation;
	private ArrayList<String> eventsIdList;


	public Organizer(String userId, String role) {
		super(userId, role);
		this.username = "";
		this.email = "";
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
}
