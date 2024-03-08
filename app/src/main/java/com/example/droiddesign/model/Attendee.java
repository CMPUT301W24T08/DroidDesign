package com.example.droiddesign.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Attendee extends User {
	private String profileName;
	private String email;
	private String phone;
	private String profilePic;
	private boolean geolocation;
	private ArrayList<String> eventsIdList;

	public Attendee() {
		super("", "");
		this.profileName = "";
		this.email = "";
		this.phone = "";
		this.profilePic = "";
		this.geolocation = false;
		this.eventsIdList = new ArrayList<>();
	}


	public Attendee(String userId, String role) {
		super(userId, role);
		this.profileName = "";
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
		map.put("profileName",profileName);
		map.put("profilePic",profilePic);
		map.put("eventsList",eventsIdList);
		return map;
	}

	public void setProfileName(String profileName){
		this.profileName = profileName;
	}
	public String getProfileName(){
		return profileName;
	}

	public void setEmail(String email){
		this.email = email;
	}
	public String getEmail(){
		return email;
	}
	public void setPhone(String phone){
		this.phone = phone;
	}
	public String getPhone(){
		return phone;
	}
	public void setProfilePic(String profilePic){
		this.profilePic = profilePic;
	}
	public void setGeolocation(boolean geolocation){
		this.geolocation = geolocation;
	}
	public boolean getGeolocation(){
		return geolocation;
	}
	public void addEvent(Event event){
		eventsIdList.add(event.getEventId());
	}

	public ArrayList<String> getEventsIdList(){
		return eventsIdList;
	}
}
