package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.example.droiddesign.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SignedEventsActivity extends AppCompatActivity {

    private RecyclerView eventsRecyclerView;
    private EventsAdapter eventsAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> signedEventsIds;
    private List<Event> signedEventsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_events);

        eventsRecyclerView = findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> finish());

        fetchSignedEventsIds();
    }

    private void fetchSignedEventsIds() {
        SharedPreferenceHelper prefsHelper = new SharedPreferenceHelper(this);
        String currentUserId = prefsHelper.getUserId();
        db.collection("Users").document(currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        User user = task.getResult().toObject(User.class);
                        if (user != null && user.getSignedEventsList() != null) {
                            signedEventsIds = user.getSignedEventsList();
                            fetchEventsDetails();
                        }
                    } else {
                        Log.w("SignedEventsActivity", "Error getting signed events list.", task.getException());
                    }
                });
    }

    private void fetchEventsDetails() {
        if (signedEventsIds != null && !signedEventsIds.isEmpty()) {
            for (String eventId : signedEventsIds) {
                db.collection("EventsDB").document(eventId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Event event = task.getResult().toObject(Event.class);
                                if (event != null) {
                                    signedEventsList.add(event);
                                    if (signedEventsList.size() == signedEventsIds.size()) {
                                        updateUI(signedEventsList);
                                    }
                                }
                            } else {
                                Log.w("SignedEventsActivity", "Error fetching event details.", task.getException());
                            }
                        });
            }
        }
    }

    private void updateUI(List<Event> events) {
        eventsAdapter = new EventsAdapter(events, event -> {
            Intent detailIntent = new Intent(SignedEventsActivity.this, EventDetailsActivity.class);
            detailIntent.putExtra("EVENT_ID", event.getEventId());
            detailIntent.putExtra("ORIGIN", "SignedEventsActivity");
            startActivity(detailIntent);
        });
        eventsRecyclerView.setAdapter(eventsAdapter);
    }
}
