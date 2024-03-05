package com.example.droiddesign.model;

import java.util.HashMap;
import java.util.Map;

public class Attendee extends User {
	private boolean signed;
	private String username;
	private Geolocation attendeeGeolocation;

	@Override
	public void saveToFirestore() {
		Map<String, Object> attendeeMap = new HashMap<>();
		attendeeMap.put("userId", this.userId);
		attendeeMap.put("email", this.email);
		attendeeMap.put("phone", this.phone);
		attendeeMap.put("signed", this.signed);
		attendeeMap.put("username", this.username);
		// Add other properties to the map

		db.collection("users").document(this.userId).set(attendeeMap)
				.addOnSuccessListener(aVoid -> {
					// Handle successful save
				})
				.addOnFailureListener(e -> {
					// Handle failed save
				});
	}

	@Override
	public void loadFromFirestore(String userId) {
		db.collection("users").document(userId).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						Attendee attendee = documentSnapshot.toObject(Attendee.class);
						// Now you can use the setter methods to set the data in your current object if needed
						this.setUserId(attendee.getUserId());
						this.setEmail(attendee.getEmail());
						this.setPhone(attendee.getPhone());
						// Set other properties
					} else {
						// Handle the case where the user does not exist
					}
				})
				.addOnFailureListener(e -> {
					// Handle any errors that may occur
				});
	}


//	public void getAttendeeFromFirestore(String attendeeId) {
//		FirebaseFirestore db = FirebaseFirestore.getInstance();
//		db.collection("Users").document("Attendees").collection("IndividualAttendees").document(attendeeId)
//				.get()
//				.addOnSuccessListener(documentSnapshot -> {
//					if (documentSnapshot.exists()) {
//						Attendee attendee = documentSnapshot.toObject(Attendee.class);
//						// Now you have your attendee object populated with the data from Firestore
//						// You can use the data from the attendee object
//					} else {
//						// Handle the case where the attendee does not exist
//					}
//				})
//				.addOnFailureListener(e -> {
//					// Handle any errors that may occur
//				});
//	}

}
