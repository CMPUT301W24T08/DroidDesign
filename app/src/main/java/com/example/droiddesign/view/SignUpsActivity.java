package com.example.droiddesign.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.droiddesign.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying sign-ups.
 * This activity displays a list of users who have signed up for an event.
 */
public class SignUpsActivity extends AppCompatActivity {
    private ArrayAdapter<String> adapterWithName;
    private ArrayAdapter<String> adapterWithoutName;
    private FirebaseFirestore db;

    /**
     * Method called when the activity is created.
     * @param savedInstanceState The saved instance state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_ups);

        ListView listViewWithName = findViewById(R.id.attendance_names);
        ListView listViewWithoutName = findViewById(R.id.attendance_ids);

        adapterWithName = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        adapterWithoutName = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        listViewWithName.setAdapter(adapterWithName);
        listViewWithoutName.setAdapter(adapterWithoutName);

        db = FirebaseFirestore.getInstance();

        // Fetch attendee IDs from Firestore and populate the ListView
        fetchAttendeeIds();
    }

    /**
     * Fetches the IDs of the attendees for the event.
     */
    private void fetchAttendeeIds() {
        String eventID = "158c339b-77a0-4075-8e21-71d7cc231284";
        db.collection("EventsDB").document(eventID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> attendees = (List<String>) documentSnapshot.get("attendeeList");
                        if (attendees != null) {
                            adapterWithName.clear();
                            adapterWithoutName.clear();
                            for (String attendeeId : attendees) {
                                fetchUserName(attendeeId);
                            }
                        } else {
                            Toast.makeText(this, "No attendees found for this event", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Event document does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch attendees: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Fetches the name of the user with the given ID.
     * @param userId The ID of the user to fetch.
     */
    private void fetchUserName(String userId) {
        db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userName = documentSnapshot.getString("userName");
                        if (userName != null && !userName.isEmpty()) {
                            adapterWithName.add(userName);
                        } else {
                            adapterWithoutName.add(userId); // Use ID if name is missing
                        }
                        adapterWithName.notifyDataSetChanged();
                        adapterWithoutName.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "User document does not exist for ID: " + userId, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
