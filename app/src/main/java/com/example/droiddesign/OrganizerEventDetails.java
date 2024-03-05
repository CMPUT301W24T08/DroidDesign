package com.example.droiddesign;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrganizerEventDetails {
	private DatabaseReference mDatabase;

	public OrganizerEventDetails() {
		// Initialize Firebase Realtime Database
		mDatabase = FirebaseDatabase.getInstance().getReference();
	}

	public void postAnnouncement(String eventId, String announcementText) {
		// Create a unique ID for the announcement
		String announcementId = mDatabase.child("announcements").push().getKey();

		// Create an announcement object
		Announcement announcement = new Announcement(announcementId, announcementText);

		// Post the announcement to Firebase under the specific event
		mDatabase.child("events").child(eventId).child("announcements").child(announcementId).setValue(announcement);

		// Notify attendees (this is a simplified example, you would typically use Firebase Cloud Messaging for real notifications)
		notifyAttendees(eventId, announcement);
	}

	private void notifyAttendees(String eventId, Announcement announcement) {
		// Code to notify attendees, e.g., via Firebase Cloud Messaging
	}

	// Helper class for announcements
	public static class Announcement {
		public String id;
		public String text;

		public Announcement(String id, String text) {
			this.id = id;
			this.text = text;
		}
	}
}
