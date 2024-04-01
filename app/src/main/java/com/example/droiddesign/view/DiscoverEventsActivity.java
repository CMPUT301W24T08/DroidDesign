package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * DiscoverEventsActivity presents a list of events fetched from Firestore for users to browse.
 * It allows users to view details about each event by clicking on them in the list.
 */
public class DiscoverEventsActivity extends AppCompatActivity {

    private RecyclerView eventsRecyclerView;
    private EventsAdapter eventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_events); //

        eventsRecyclerView = findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchEvents();

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> finish());
    }

    private void fetchEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return; // Handle the case where the user is not logged in
        }

        try {
            db.collection("Users").document(currentUserId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            List<String> signedUpEvents = user.getSignedEventsList();

                            try {
                                db.collection("EventsDB")
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                List<Event> fetchedEvents = new ArrayList<>();
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Event event = document.toObject(Event.class);
                                                    if (!signedUpEvents.contains(event.getEventId())) {
                                                        fetchedEvents.add(event);
                                                    }
                                                }
                                                updateUI(fetchedEvents);
                                            } else {
                                                Log.w("DiscoverEventsActivity", "Error getting documents.", task.getException());
                                            }
                                        });
                            } catch (Exception e) {
                                Log.e("DiscoverEventsActivity", "Error fetching events", e);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.w("DiscoverEventsActivity", "Error fetching user data", e));
        } catch (Exception e) {
            Log.e("DiscoverEventsActivity", "Error fetching user document", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchEvents();
    }

    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            return null;
        }
    }

    private void updateUI(List<Event> events) {
        if (!events.isEmpty()) {
            eventsAdapter = new EventsAdapter(events, event -> {
                Intent detailIntent = new Intent(DiscoverEventsActivity.this, EventDetailsActivity.class);
                detailIntent.putExtra("EVENT_ID", event.getEventId());
                startActivity(detailIntent);
            });
            eventsRecyclerView.setAdapter(eventsAdapter);
        }
    }
}
