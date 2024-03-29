package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GeoCheckFragment extends AppCompatActivity {
    private Button sendButton;
    private TextView titleEditText;
    private TextView messageEditText;
    private TextView dateEditText;
    private FirebaseFirestore firestore;
    private CollectionReference attendeeListRef;
    private RecyclerView announcementsRecyclerView;
    private AnnouncementAdapter announcementAdapter;
    private List<Map<String, Object>> announcementList = new ArrayList<>();
    private String userId, userRole,eventId;
    SharedPreferenceHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_send_announcement);
        prefsHelper = new SharedPreferenceHelper(this);
        String savedUserId = prefsHelper.getUserId();
        if (savedUserId != null) {
            // Use the userId from SharedPreferences
            userId = savedUserId;
            userRole = prefsHelper.getRole();
        } //At this point, user details are valid

        // Inflate the button based on user role
        findViewById(R.id.send_button).setVisibility("organizer".equalsIgnoreCase(userRole) ? View.VISIBLE : View.GONE );


        eventId = getIntent().getStringExtra("EVENT_ID");
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Event ID is missing.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        firestore = FirebaseFirestore.getInstance();
        attendeeListRef = firestore.collection("EventsDB").document(eventId).collection("attendeeList");
        firestore.collection("EventsDB").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String organizerOwnerId = documentSnapshot.getString("organizerOwnerId");
                    findViewById(R.id.only_owner).setVisibility(organizerOwnerId != null && organizerOwnerId.equals(userId) ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> Log.e("VisibilityCheck", "Error", e));
        titleEditText = findViewById(R.id.title_edit_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_button);

        sendButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String message = messageEditText.getText().toString().trim();

            if (title.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Please write a message.", Toast.LENGTH_SHORT).show();
            } else {
                saveMessage(title, message);
            }
        });
        announcementsRecyclerView = findViewById(R.id.organizer_message_recyclerview);
        announcementsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        announcementAdapter = new AnnouncementAdapter(announcementList);
        announcementsRecyclerView.setAdapter(announcementAdapter);

        fetchAnnouncements();

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void saveMessage(String title, String message) {
        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(this, "Event ID is not set.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new message map to be saved
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("title", title);
        messageMap.put("message", message);
        messageMap.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

        // Save the message under OrganizerMessages of the specific event
        firestore.collection("EventsDB").document(eventId)
                .update("organizerMessages", FieldValue.arrayUnion(messageMap))
                .addOnSuccessListener(documentReference -> {
                    // Clear the input fields after successful save
                    titleEditText.setText("");
                    messageEditText.setText("");
                    Toast.makeText(this, "Message sent successfully.", Toast.LENGTH_SHORT).show();
                    // After sending the message, refresh the activity to show updated data
                    refreshActivity();
                    notifyAttendees(title);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to send message.", Toast.LENGTH_SHORT).show());
    }

    private void fetchAnnouncements() {
        firestore.collection("EventsDB").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("organizerMessages")) {
                        List<Map<String, Object>> unsortedMessages = (List<Map<String, Object>>) documentSnapshot.get("organizerMessages");
                        if (unsortedMessages != null) {
                            // Sort messages by date in descending order
                            List<Map<String, Object>> sortedMessages = new ArrayList<>(unsortedMessages);
                            sortedMessages.sort((map1, map2) -> ((String) map2.get("date")).compareTo((String) map1.get("date")));

                            // Now use sortedMessages to update your RecyclerView
                            announcementList.clear();
                            announcementList.addAll(sortedMessages);
                            announcementAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("FetchAnnouncementsError", "Error loading announcements", e));
    }

    private void refreshActivity() {
        Intent intent = new Intent(this, SendAnnouncementFragment.class);
        intent.putExtra("EVENT_ID", eventId); // Pass the event ID back to the activity
        finish();
        startActivity(intent);
    }

    private void notifyAttendees(String title) {
        firestore.collection("EventsDB").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<String> attendeeList = (List<String>) documentSnapshot.get("attendeeList");
                    if (attendeeList != null) {
                        for (String userId : attendeeList) {
                            // Assuming there's a 'Notifications' collection for each user where we can add a new notification
                            Map<String, Object> notificationData = new HashMap<>();
                            notificationData.put("message", "New announcement from the organizer!");
                            notificationData.put("title", title);
                            notificationData.put("eventId", eventId);
                            notificationData.put("timestamp", FieldValue.serverTimestamp()); // Use server timestamp for consistency

                            firestore.collection("Users").document(userId)
                                    .collection("Notifications")
                                    .add(notificationData)
                                    .addOnFailureListener(e -> Log.e("NotifyAttendees", "Failed to notify user: " + userId));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("NotifyAttendees", "Failed to get attendee list for event: " + eventId));
    }
}
