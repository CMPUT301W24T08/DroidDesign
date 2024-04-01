package com.example.droiddesign.model;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class AttendanceDB {

    private FirebaseFirestore db;

    public AttendanceDB() {
        db = FirebaseFirestore.getInstance();
    }

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

