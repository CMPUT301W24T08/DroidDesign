package com.example.droiddesign.model;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {
    // Dummy class that we can either forget about or flesh out more.
    // ArrayList<Organizer> organizers;
    private String eventId;
    private String qrCode;
    private Geolocation geolocation;
    private String organizerOwnerId;
    private List<String> attendeeList;
    private List<OrganizerMessage> organizerMessages;
    private String imagePosterId;
    private EventDetails eventDetails;

    // Constructor
    public Event() {
        // Default constructor for Firestore serialization
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    // ... (other getters and setters for all fields)

    // Method to save event to Firestore
    public void saveToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(this.eventId).set(this.toMap())
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    // Method to load event from Firestore
    public static void loadFromFirestore(String eventId, FirestoreCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event event = documentSnapshot.toObject(Event.class);
                        callback.onCallback(event);
                    } else {
                        callback.onCallback(null);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    callback.onCallback(null);
                });
    }

    // Helper method to convert the event object to a Map
    private Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("qrCode", qrCode);
        map.put("geolocation", geolocation); // Make sure Geolocation class has proper serialization
        map.put("organizerOwnerId", organizerOwnerId);
        map.put("attendeeList", attendeeList);
        map.put("organizerMessages", organizerMessages); // Make sure OrganizerMessage class has proper serialization
        map.put("imagePosterId", imagePosterId);
        map.put("eventDetails", eventDetails); // Make sure EventDetails class has proper serialization
        return map;
    }

    // Callback interface for asynchronous Firestore operations
    public interface FirestoreCallback {
        void onCallback(Event event);
    };
}