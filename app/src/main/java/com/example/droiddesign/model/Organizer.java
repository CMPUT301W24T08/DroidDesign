package com.example.droiddesign.model;

import java.util.List;

public class Organizer extends User {
	private String type;
	private String username;
	private Geolocation geolocation;
	private List<String> eventsList;


	@Override
	public void saveToFirestore() {

	}

	@Override
	public void loadFromFirestore(String userId) {

	}
}
