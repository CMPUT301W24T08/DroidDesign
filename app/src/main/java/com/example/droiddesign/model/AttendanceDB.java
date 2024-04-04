package com.example.droiddesign.model;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * The AttendanceDB class is responsible for managing the attendance data in the Firestore database.
 */
public class AttendanceDB {

    private FirebaseFirestore db;

    /**
     * Creates a new instance of the AttendanceDB class.
     */
    public AttendanceDB() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Checks in a user to an event by saving the check-in data in the Firestore database.
     *
     * @param eventId   The unique identifier of the event.
     * @param userId    The unique identifier of the user.
     * @param latitude  The latitude of the user's location.
     * @param longitude The longitude of the user's location.
     */
    public void checkInUser(String eventId, String userId, double latitude, double longitude) {
        // Use a composite key to uniquely identify each check-in
        String documentId = eventId + "_" + userId;

        db.collection("attendance")
                .document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    long checkInCount = 1;
                    if (documentSnapshot.exists()) {
                        checkInCount = documentSnapshot.getLong("check_in_count") + 1;
                    }

                    Map<String, Object> checkInData = new HashMap<>();
                    checkInData.put("event_id", eventId);
                    checkInData.put("user_id", userId);
                    checkInData.put("latitude", latitude);
                    checkInData.put("longitude", longitude);
                    checkInData.put("check_in_count", checkInCount);
                    checkInData.put("timestamp", System.currentTimeMillis()); // Save current time as timestamp

                    db.collection("attendance")
                            .document(documentId)
                            .set(checkInData, SetOptions.merge());
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }
}

