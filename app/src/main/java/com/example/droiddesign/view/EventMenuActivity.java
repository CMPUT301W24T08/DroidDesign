package com.example.droiddesign.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

//		userId = getIntent().getStringExtra("UserId");
//		userRole = getIntent().getStringExtra("role");
		// Fetch events from Firestore and populate RecyclerView
		fetchEvents();

		eventsRecyclerView = findViewById(R.id.events_recycler_view);
		navigationMenu = findViewById(R.id.navigation_menu);
		ImageButton menuButton = findViewById(R.id.button_menu);

		eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		eventsAdapter = new EventsAdapter(eventsList, event -> {
			// Handle the event click here
			Intent intent = new Intent(EventMenuActivity.this, EventDetailsActivity.class);
			intent.putExtra("EVENT_ID", event.getEventId()); // Make sure your Event class has a method getId().
			startActivity(intent);		});
		eventsRecyclerView.setAdapter(eventsAdapter);
		eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		menuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleNavigationMenu();
			}
		});
		setupRecyclerView();
		ImageButton backButton = findViewById(R.id.button_back);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		FloatingActionButton addEventButton = findViewById(R.id.fab_add_event);
		addEventButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(EventMenuActivity.this, AddEventActivity.class);
				startActivity(intent);
			}
		});

		// Adjust UI elements based on the user's role
//		adjustUIBasedOnRole(userRole, (ImageButton) addEventButton);
		navigationMenu.inflateMenu(R.menu.navigation_drawer);
		// Set the navigation item selection listener if needed
		navigationMenu.setNavigationItemSelectedListener(item -> {
			// Handle navigation view item clicks here.
			int id = item.getItemId();
			// Logic to handle item clicks
			Intent intent;

			if (id == R.id.browse_events) {
				// Intent for the admin dashboard
				intent = new Intent(this, EventMenuActivity.class);
				startActivity(intent);
			} else if (id == R.id.profile_settings) {
				// Intent for the organizer dashboard
				intent = new Intent(this, ProfileSettingsActivity.class);
				startActivity(intent);
			} else if (id == R.id.app_settings) {
				// Intent for the attendee dashboard
				intent = new Intent(this, AppSettingsActivity.class);
				startActivity(intent);
			}
			return true;
		});
	}

//	private void adjustUIBasedOnRole(String role, ImageButton addEventButton) {
//		switch (role) {
//			case "admin":
//				// For admin, set the addEventButton to visible and load the admin menu
//				addEventButton.setVisibility(View.VISIBLE);
//				navigationMenu.inflateMenu(R.menu.menu_navigation_admin); // Replace with your actual admin menu
//				break;
//			case "organizer":
//				// For organizer, leave the addEventButton to visible and load the organizer menu
//				navigationMenu.inflateMenu(R.menu.menu_navigation_organizer); // Replace with your actual organizer menu
//				break;
//			case "attendee":
//				// For attendee, set the addEventButton to gone and load the attendee menu
//				addEventButton.setVisibility(View.GONE);
//				navigationMenu.inflateMenu(R.menu.menu_navigation_attendee); // Replace with your actual attendee menu
//				break;
//		}
//
//	}

	public void showEventInputDialog(View view) {
		// Initialize the inflater
		LayoutInflater inflater = getLayoutInflater();

		// Inflate the custom dialog layout
		View dialogView = inflater.inflate(R.layout.add_event_input_dialog, null);

		// Get the dialog components
		EditText eventNameInput = dialogView.findViewById(R.id.event_name_input);
		EditText eventDescriptionInput = dialogView.findViewById(R.id.event_description_input);
		Button createEventButton = dialogView.findViewById(R.id.create_event_button);

		// Create the AlertDialog
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setView(dialogView)
				.setTitle("Create New Event")
				.create();

		// Set the click listener for the create button
		createEventButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String eventName = String.valueOf(eventNameInput.getText());
				String eventDescription = String.valueOf(eventDescriptionInput.getText());

				// Create the event with the provided input
				createEvent(eventName, eventDescription);

				// Dismiss the dialog
				dialog.dismiss();
			}
		});

		// Show the dialog
		dialog.show();
	}
	private void createEvent(String eventName, String eventDescription) {
		// Get an instance of the Firestore database
		FirebaseFirestore db = FirebaseFirestore.getInstance();

		// Create a new event object
		Map<String, Object> event = new HashMap<>();
		event.put("EventDetails.Name", eventName);
		event.put("EventDetails.Description", eventDescription);
		// Add additional fields such as start time, end time, etc.

		// Add the new event to the Firestore database
		db.collection("events").add(event)
				.addOnSuccessListener(documentReference -> {
					// Handle success
					Log.d("createEvent", "DocumentSnapshot added with ID: " + documentReference.getId());
				})
				.addOnFailureListener(e -> {
					// Handle the error
					Log.w("createEvent", "Error adding document", e);
				});
	}
	private void setupRecyclerView() {
		// Set up RecyclerView with EventsAdapter and layout manager
		eventsRecyclerView = findViewById(R.id.events_recycler_view);
		eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		// Assuming you have a method to initialize your events list
		eventsList = initializeEventsList();

		eventsAdapter = new EventsAdapter(eventsList, new EventsAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(Event event) {
				Intent intent = new Intent(EventMenuActivity.this, EventDetailsActivity.class);
				intent.putExtra("EVENT_ID", event.getEventId()); // Make sure your Event class has a method getId().
				startActivity(intent);
			}
		});

		eventsRecyclerView.setAdapter(eventsAdapter);
	}

	private List<Event> initializeEventsList() {
		// Initialize your events list here
		return new ArrayList<>();
	}

	private void toggleNavigationMenu() {
		// If the navigation menu is visible, hide it. Otherwise, show it.
		if (navigationMenu.getVisibility() == View.VISIBLE) {
			navigationMenu.setVisibility(View.GONE);
		} else {
			navigationMenu.setVisibility(View.VISIBLE);
		}
	}

	@SuppressLint("NotifyDataSetChanged")
	private void fetchEvents() {
		// Get reference to the events collection
		CollectionReference eventsCollection = db.collection("events");

		// Query Firestore for all events
		eventsCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
			// Initialize events list
			eventsList = new ArrayList<>();

			// Iterate through each document snapshot
			for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
				// Convert document snapshot to Event object
				Event event = documentSnapshot.toObject(Event.class);
				if (event != null) {
					// Add event to the events list
					eventsList.add(event);
				}
			}

			// Populate RecyclerView with events
			eventsAdapter = new EventsAdapter(eventsList, event -> {
				// Handle event click here
				Intent intent = new Intent(EventMenuActivity.this, EventDetailsActivity.class);
				intent.putExtra("EVENT_ID", event.getEventId()); // Assuming you have a method to get event ID
				startActivity(intent);
			});
			eventsRecyclerView.setAdapter(eventsAdapter);

			// Notify adapter that data set has changed
			eventsAdapter.notifyDataSetChanged();
		}).addOnFailureListener(e -> {
			// Handle failure to fetch events
			Log.e("EventMenuActivity", "Error fetching events", e);
			Toast.makeText(this, "Error fetching events", Toast.LENGTH_SHORT).show();
		});
	}

}