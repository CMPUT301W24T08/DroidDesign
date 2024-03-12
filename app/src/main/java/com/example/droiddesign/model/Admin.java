package com.example.droiddesign.model;

import java.util.HashMap;

public class Admin extends User {
	private String type; // Additional fields specific to Admin

	public Admin(String userId, String role) {
		super(userId, role);
	}

	
	public HashMap<String, Object> toMap() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		Object role;
		map.put("role", role);
		return map;
	}
}

