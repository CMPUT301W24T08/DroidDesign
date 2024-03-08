package com.example.droiddesign.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventMenuActivity extends AppCompatActivity {
	private RecyclerView eventsRecyclerView;
	private EventsAdapter eventsAdapter;
	private List<Event> eventsList; // Populate this list with the events later using firestore.
	private NavigationView navigationMenu;
	private FirebaseFirestore db = FirebaseFirestore.getInstance();
	private String userId, userRole;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_menu);

		userRole = getIntent().getStringExtra("role");
		userId = getIntent().getStringExtra("userId");

		fetchEvents();

		eventsRecyclerView = findViewById(R.id.events_recycler_view);
		navigationMenu = findViewById(R.id.navigation_menu);
		ImageButton menuButton = findViewById(R.id.button_menu);

		eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		eventsAdapter = new EventsAdapter(eventsList, event -> {
			Intent intent = new Intent(EventMenuActivity.this, EventDetailsActivity.class);
			intent.putExtra("EVENT_ID", event.getEventId());
			startActivity(intent);
		});
		eventsRecyclerView.setAdapter(eventsAdapter);

		menuButton.setOnClickListener(v -> toggleNavigationMenu());
		setupRecyclerView();

		ImageButton backButton = findViewById(R.id.button_back);
		backButton.setOnClickListener(v -> finish());

		FloatingActionButton addEventButton = findViewById(R.id.fab_add_event);
		if ("attendee".equals(userRole)) {
			// Make the button invisible and not take up layout space
			addEventButton.setVisibility(View.GONE);
		} else {
			// Set the click listener only if the user is not an attendee
			addEventButton.setOnClickListener(view -> {
				Intent intent = new Intent(EventMenuActivity.this, AddEventActivity.class);
				startActivity(intent);
			});
		}

		// Takes the user to the add event activity
		addEventButton.setOnClickListener(view -> {
			Intent intent = new Intent(EventMenuActivity.this, AddEventActivity.class);
			startActivity(intent);
		});


		navigationMenu.getMenu().clear();

		// Inflate the menu based on user role
		if ("organizer".equals(userRole)) {
			navigationMenu.inflateMenu(R.menu.menu_navigation_organizer);
		} else if ("admin".equals(userRole)) {
			navigationMenu.inflateMenu(R.menu.menu_navigation_admin);
		} else { // Default to attendee if no role or attendee role
			navigationMenu.inflateMenu(R.menu.menu_navigation_attendee);
		}

		// Set the navigation item selection listener
		navigationMenu.setNavigationItemSelectedListener(item -> {
			int id = item.getItemId();
			Intent intent = null;

			if (id == R.id.browse_events) {
				intent = new Intent(this, EventMenuActivity.class);
			} else if (id == R.id.profile) {
				intent = new Intent(this, ProfileSettingsActivity.class);
			} else if (id == R.id.settings) {
				intent = new Intent(this, AppSettingsActivity.class);
			} else if ("organizer".equals(userRole) && id == R.id.nav_manage_events) {

				// Assuming you have an activity to handle sharing of events
			}

			if (intent != null) {
				startActivity(intent);
			}

			return true;
		});

	}

	private void createEvent(String eventName, String eventDescription) {
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		Map<String, Object> event = new HashMap<>();
		event.put("EventDetails.Name", eventName);
		event.put("EventDetails.Description", eventDescription);

		db.collection("events").add(event)
				.addOnSuccessListener(documentReference -> Log.d("createEvent", "DocumentSnapshot added with ID: " + documentReference.getId()))
				.addOnFailureListener(e -> Log.w("createEvent", "Error adding document", e));
	}

	private void setupRecyclerView() {
		eventsRecyclerView = findViewById(R.id.events_recycler_view);
		eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		eventsList = initializeEventsList();

		eventsAdapter = new EventsAdapter(eventsList, event -> {
			Intent intent = new Intent(EventMenuActivity.this, EventDetailsActivity.class);
			intent.putExtra("EVENT_ID", event.getEventId());
			startActivity(intent);
		});

		eventsRecyclerView.setAdapter(eventsAdapter);
	}

	private List<Event> initializeEventsList() {
		return new ArrayList<>();
	}

	private void toggleNavigationMenu() {
		if (navigationMenu.getVisibility() == View.VISIBLE) {
			navigationMenu.setVisibility(View.GONE);
		} else {
			navigationMenu.setVisibility(View.VISIBLE);
		}
	}

	@SuppressLint("NotifyDataSetChanged")
	private void fetchEvents() {
		CollectionReference eventsCollection = db.collection("events");

		eventsCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
			eventsList = new ArrayList<>();
			for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
				Event event = documentSnapshot.toObject(Event.class);
				if (event != null) {
					eventsList.add(event);
				}
			}

			eventsAdapter = new EventsAdapter(eventsList, event -> {
				Intent intent = new Intent(EventMenuActivity.this, EventDetailsActivity.class);
				intent.putExtra("EVENT_ID", event.getEventId());
				startActivity(intent);
			});
			eventsRecyclerView.setAdapter(eventsAdapter);
			eventsAdapter.notifyDataSetChanged();
		}).addOnFailureListener(e -> {
			Log.e("EventMenuActivity", "Error fetching events", e);
			Toast.makeText(this, "Error fetching events", Toast.LENGTH_SHORT).show();
		});
	}
}