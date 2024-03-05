package com.example.droiddesign.model;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {
    private String eventId;
    private String eventName;
    private String eventDate;
    private String startTime;
    private String endTime;
    private String date;
    private String geolocation;
    private String organizerOwnerId;
    private String imagePosterId;
    private String description;
    private Integer signupLimit;
    private Integer attendeesCount;
    private String qrCode;
    private List<String> attendeeList;
    private List<OrganizerMessage> organizerMessages;
    private static final String COLLECTION_PATH = "events";
    private transient DocumentReference eventRef;

    public Event(String eventId, String eventName, String eventDate, String startTime, String endTime, String geolocation, String organizerOwnerId, String imagePosterId, String description, Integer signupLimit, Integer attendeesCount, String qrCode) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.geolocation = geolocation;
        this.organizerOwnerId = organizerOwnerId;
        this.imagePosterId = imagePosterId;
        this.description = description;
        this.signupLimit = signupLimit;
        this.attendeesCount = attendeesCount;
        this.qrCode = qrCode;
    }
    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
        updateFirestore("eventId", eventId);
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
        updateFirestore("eventName", eventName);
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
        updateFirestore("eventDate", eventDate);
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
        updateFirestore("startTime", startTime);
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
        updateFirestore("endTime", endTime);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        updateFirestore("date", date);
    }

    public String getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(String geolocation) {
        this.geolocation = geolocation;
        updateFirestore("geolocation", geolocation);
    }

    public String getOrganizerOwnerId() {
        return organizerOwnerId;
    }

    public void setOrganizerOwnerId(String organizerOwnerId) {
        this.organizerOwnerId = organizerOwnerId;
        updateFirestore("organizerOwnerId", organizerOwnerId);
    }

    public String getImagePosterId() {
        return imagePosterId;
    }

    public void setImagePosterId(String imagePosterId) {
        this.imagePosterId = imagePosterId;
        updateFirestore("imagePosterId", imagePosterId);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        updateFirestore("description", description);
    }

    public Integer getSignupLimit() {
        return signupLimit;
    }

    public void setSignupLimit(Integer signupLimit) {
        this.signupLimit = signupLimit;
        updateFirestore("signupLimit", signupLimit);
    }

    public Integer getAttendeesCount() {
        return attendeesCount;
    }

    public void setAttendeesCount(Integer attendeesCount) {
        this.attendeesCount = attendeesCount;
        updateFirestore("attendeesCount", attendeesCount);
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
        updateFirestore("qrCode", qrCode);
    }

    public List<String> getAttendeeList() {
        return attendeeList;
    }

    public void setAttendeeList(List<String> attendeeList) {
        this.attendeeList = attendeeList;
        updateFirestore("attendeeList", attendeeList);
    }

    public List<OrganizerMessage> getOrganizerMessages() {
        return organizerMessages;
    }

    public void setOrganizerMessages(List<OrganizerMessage> organizerMessages) {
        this.organizerMessages = organizerMessages;
        updateFirestore("organizerMessages", organizerMessages);
    }

    // Method to save event to Firestore
    public void saveToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> eventData = toMap();
        db.collection(COLLECTION_PATH).document(eventId).set(eventData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    // Method to update Firestore with a single field
    private void updateFirestore(String fieldName, Object value) {
        if (eventId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> updateData = new HashMap<>();
            updateData.put(fieldName, value);
            db.collection(COLLECTION_PATH).document(eventId).set(updateData, SetOptions.merge());
        }
    }

    // Method to convert event object to a map
    private Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("eventName", eventName);
        map.put("eventDate", eventDate);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("date", date);
        map.put("geolocation", geolocation);
        map.put("organizerOwnerId", organizerOwnerId);
        map.put("imagePosterId", imagePosterId);
        map.put("description", description);
        map.put("signupLimit", signupLimit);
        map.put("attendeesCount", attendeesCount);
        map.put("qrCode", qrCode);
        map.put("attendeeList", attendeeList);
        map.put("organizerMessages", organizerMessages);
        return map;
    }

    // Method to load event from Firestore
    public static void loadFromFirestore(String eventId, FirestoreCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_PATH).document(eventId).get()
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

    // Callback interface for asynchronous Firestore operations
    public interface FirestoreCallback {
        void onCallback(Event event);
    };

}

