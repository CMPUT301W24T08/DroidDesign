package com.example.droiddesign.model;

import android.util.Log;

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
        String documentId = eventId + "_" + userId;

        db.collection("AttendanceDB")
                .document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    long checkInCount = 1;  // Default to 1 if document doesn't exist
                    if (documentSnapshot.exists() && documentSnapshot.contains("check_in_count")) {
                        checkInCount = documentSnapshot.getLong("check_in_count");
                        Log.d("AttendanceDB", "Current check-in count: " + checkInCount);
                        checkInCount++;  // Increment the count
                    }

                    Log.d("AttendanceDB", "New check-in count: " + checkInCount);

                    Map<String, Object> checkInData = new HashMap<>();
                    checkInData.put("event_id", eventId);
                    checkInData.put("user_id", userId);
                    checkInData.put("latitude", latitude);
                    checkInData.put("longitude", longitude);
                    checkInData.put("check_in_count", checkInCount);
                    checkInData.put("timestamp", System.currentTimeMillis());

                    db.collection("AttendanceDB")
                            .document(documentId)
                            .set(checkInData, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> Log.d("AttendanceDB", "Check-in count updated successfully"))
                            .addOnFailureListener(e -> Log.d("AttendanceDB", "Error updating check-in count", e));
                })
                .addOnFailureListener(e -> {
                    Log.e("AttendanceDB", "Error fetching document", e);
                });
    }
}

