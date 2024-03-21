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
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.example.droiddesign.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageEventActivity extends AppCompatActivity {

    private RecyclerView eventsRecyclerView;
    private EventsAdapter eventsAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> managedEventsIds;
    private List<Event> managedEventsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event);

        eventsRecyclerView = findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> finish());

        fetchManagedEventsIds();
    }

    private void fetchManagedEventsIds() {
        SharedPreferenceHelper prefsHelper = new SharedPreferenceHelper(this);
        String currentUserId = prefsHelper.getUserId();
        db.collection("Users").document(currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        User user = task.getResult().toObject(User.class);
                        if (user != null && user.getManagedEventsList() != null) {
                            managedEventsIds = user.getManagedEventsList();
                            fetchEventsDetails();
                        }
                    } else {
                        Log.w("ManageEventActivity", "Error getting managed events list.", task.getException());
                    }
                });
    }

    private void fetchEventsDetails() {
        if (managedEventsIds != null && !managedEventsIds.isEmpty()) {
            for (String eventId : managedEventsIds) {
                db.collection("EventsDB").document(eventId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Event event = task.getResult().toObject(Event.class);
                                if (event != null) {
                                    managedEventsList.add(event);
                                    if (managedEventsList.size() == managedEventsIds.size()) {
                                        updateUI(managedEventsList);
                                    }
                                }
                            } else {
                                Log.w("ManageEventsActivity", "Error fetching event details.", task.getException());
                            }
                        });
            }
        }
    }

    private void updateUI(List<Event> events) {
        eventsAdapter = new EventsAdapter(events, event -> {
            Intent detailIntent = new Intent(ManageEventActivity.this, EventDetailsActivity.class);
            detailIntent.putExtra("EVENT_ID", event.getEventId());
            startActivity(detailIntent);
        });
        eventsRecyclerView.setAdapter(eventsAdapter);
    }
}
