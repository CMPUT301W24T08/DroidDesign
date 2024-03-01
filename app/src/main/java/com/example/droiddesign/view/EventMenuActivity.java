package com.example.droiddesign.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;
import com.google.android.material.navigation.NavigationView;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_menu);

		eventsRecyclerView = findViewById(R.id.events_recycler_view);
		navigationMenu = findViewById(R.id.navigation_menu);
		ImageButton menuButton = findViewById(R.id.button_menu);
		eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		eventsAdapter = new EventsAdapter(eventsList, event -> {
			// Handle the event click here
			Intent intent = new Intent(EventMenuActivity.this, EventDetailsActivity.class);
			intent.putExtra("EVENT_ID", event.getEventId()); // Make sure your Event class has a method getId().
			startActivity(intent);
		});

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
		ImageView addEventButton = findViewById(R.id.add_event);
		addEventButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Handle the ImageView click here
				showEventInputDialog(view);
			}
		});
	}
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

}
