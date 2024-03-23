package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageEventActivity extends AppCompatActivity {
	private RecyclerView recyclerViewEvent;
	private RecyclerView recyclerViewEventSignedUp;
	private EventsAdapter eventAdapter;
	private EventsAdapter eventSignedUpAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_events);

		recyclerViewEvent = findViewById(R.id.recycler_view_event_created);
		recyclerViewEventSignedUp = findViewById(R.id.recycler_view_event_signed_up);

		// Set layout managers for RecyclerViews
		recyclerViewEvent.setLayoutManager(new LinearLayoutManager(this));
		recyclerViewEventSignedUp.setLayoutManager(new LinearLayoutManager(this));

		// Fetch and display managed events and signed up events
		fetchEvents();

		ImageButton backButton = findViewById(R.id.button_back);
		backButton.setOnClickListener(v -> finish());
	}

	private void fetchEvents() {
		FirebaseFirestore db = FirebaseFirestore.getInstance();

		SharedPreferenceHelper prefsHelper = new SharedPreferenceHelper(this);
		String userId = prefsHelper.getUserId();

		CollectionReference manageListRef = db.collection("Users").document(userId).collection("managedEventsList");
//		Toast.makeText(ManageEventActivity.this, "Accessing managed EventsList collection", Toast.LENGTH_SHORT).show();
		manageListRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
			@Override
			public void onComplete(@NonNull Task<QuerySnapshot> task) {
				if (task.isSuccessful()) {
					List<Event> managedEvents = new ArrayList<>();
					for (QueryDocumentSnapshot document : task.getResult()) {
						Event event = document.toObject(Event.class);
						Toast.makeText(ManageEventActivity.this, "Accessing signed EventsList collection", Toast.LENGTH_SHORT).show();
						managedEvents.add(event);
					}
					// Update the managed events UI
					updateManagedEventsUI(managedEvents);
					// Logging the managed events
					for (Event event : managedEvents) {
						Log.d("ManageEventActivity", "Managed Event: " + event.toString());
					}
				} else {
					Log.w("ManageEventActivity", "Error getting managed events.", task.getException());
				}
			}
		});

		// Fetch signed up events
		CollectionReference signUpEventListRef = db.collection("Users").document(userId).collection("signedEventsList");
		signUpEventListRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
			@Override
			public void onComplete(@NonNull Task<QuerySnapshot> task) {
				if (task.isSuccessful()) {
					List<Event> signedUpEvents = new ArrayList<>();
					for (QueryDocumentSnapshot document : task.getResult()) {
						Event event = document.toObject(Event.class);
						Toast.makeText(ManageEventActivity.this, "Accessing signed EventsList collection", Toast.LENGTH_SHORT).show();
						signedUpEvents.add(event);
					}
					// Update the signed up events UI
					updateSignedUpEventsUI(signedUpEvents);
				} else {
					Log.w("ManageEventActivity", "Error getting signed up events.", task.getException());
				}
			}
		});
	}

	private void updateManagedEventsUI(List<Event> managedEvents) {

		eventAdapter = new EventsAdapter(managedEvents, event -> {
			Intent detailIntent = new Intent(ManageEventActivity.this, EventDetailsActivity.class);
			detailIntent.putExtra("EVENT_ID", event.getEventId());
			startActivity(detailIntent);
			finish();
		});
		recyclerViewEvent.setAdapter(eventAdapter);
	}

	private void updateSignedUpEventsUI(List<Event> signedUpEvents) {

		eventSignedUpAdapter = new EventsAdapter(signedUpEvents, event -> {
			Intent detailIntent = new Intent(ManageEventActivity.this, EventDetailsActivity.class);
			detailIntent.putExtra("EVENT_ID", event.getEventId());
			startActivity(detailIntent);
			finish();
		});
		recyclerViewEventSignedUp.setAdapter(eventSignedUpAdapter);

	}
}
