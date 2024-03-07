package com.example.droiddesign.model;

import java.util.HashMap;
<<<<<<< HEAD

public class Admin {
=======
import java.util.Map;

public class Admin extends User {
	private String type; // Additional fields specific to Admin

	public Admin(String userId, String role) {
		super(userId, role);
	}
	@Override
	public HashMap<String, Object> toMap() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("role",role);
		return map;
	}

>>>>>>> 736f5d5205a9ea65fcca4fd46b2600799e177ae0

}
