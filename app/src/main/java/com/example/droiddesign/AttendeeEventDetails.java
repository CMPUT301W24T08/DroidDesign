package com.example.droiddesign;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AttendeeEventDetails {
	private DatabaseReference mDatabase;

	public AttendeeEventDetails() {
		// Initialize Firebase Realtime Database
		mDatabase = FirebaseDatabase.getInstance().getReference();
	}

	public void listenForAnnouncements(String eventId) {
		// Listen for changes in the announcements section of the event
		mDatabase.child("events").child(eventId).child("announcements")
				.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						for (DataSnapshot announcementSnapshot: dataSnapshot.getChildren()) {
							// Here you receive the announcement details
							OrganizerEventDetails.Announcement announcement = announcementSnapshot.getValue(OrganizerEventDetails.Announcement.class);
							// Now you can handle the announcement, e.g., update the UI
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						// Getting post failed, log a message
					}
				});
	}

}

