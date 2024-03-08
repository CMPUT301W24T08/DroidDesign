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
import com.example.droiddesign.model.Attendee;
import com.example.droiddesign.model.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Activity representing the main menu for the event application.
 * It provides the user with a list of events they have signed up for and allows navigation to other features.
 */
public class EventMenuActivity extends AppCompatActivity {
	/**
	 * RecyclerView for displaying the list of events.
	 */
	private RecyclerView eventsRecyclerView;

	/**
	 * Adapter for the events RecyclerView.
	 */
	private EventsAdapter eventsAdapter;

	/**
	 * List holding the events to be displayed.
	 */
	private List<Event> eventsList;

	/**
	 * Navigation menu for accessing different sections of the app.
	 */
	private NavigationView navigationMenu;

	/**
	 * Firebase Firestore instance for database interaction.
	 */
	private FirebaseFirestore db = FirebaseFirestore.getInstance();

	/**
	 * User ID and role for personalizing the user experience.
	 */
	private String userId, userRole;

	/**
	 * List of events the user has signed up for.
	 */
	private List<Event> signedUpEvents;

	/**
	 * Initializes the activity, setting up UI components and event listeners.
	 * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
	 *                           this Bundle contains the data it most recently supplied. Otherwise, it is null.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_menu);

		userRole = getIntent().getStringExtra("role");
		userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
		fetchUserSignedUpEvents();

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

		addEventButton.setOnClickListener(view -> {
			Intent intent = new Intent(EventMenuActivity.this, AddEventActivity.class);
			startActivity(intent);
		});

		FloatingActionButton fabQuickScan = findViewById(R.id.fab_quick_scan);
		fabQuickScan.setOnClickListener(v -> {
			// Intent to start Quick Scan Activity or any specific logic
			Intent intent = new Intent(EventMenuActivity.this, QrCodeScanActivity.class);
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
				intent = new Intent(this, DiscoverEventsActivity.class);
			} else if (id == R.id.profile) {
				intent = new Intent(this, ProfileSettingsActivity.class);
				intent.putExtra("USER_ID", userId);
			} else if (id == R.id.settings) {
				intent = new Intent(this, AppSettingsActivity.class);
			} else if ("organizer".equals(userRole) && id == R.id.nav_manage_events) {
			}

			if (intent != null) {
				startActivity(intent);
			}

			return true;
		});
	}


	/**
	 * Sets up the RecyclerView with its layout manager and adapter.
	 */
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

	/**
	 * Initializes the events list.
	 * @return An empty ArrayList of Event objects.
	 */

	private List<Event> initializeEventsList() {
		return new ArrayList<>();
	}

	/**
	 * Toggles the visibility of the navigation menu.
	 */
	private void toggleNavigationMenu() {
		if (navigationMenu.getVisibility() == View.VISIBLE) {
			navigationMenu.setVisibility(View.GONE);
		} else {
			navigationMenu.setVisibility(View.VISIBLE);
		}
	}


	/**
	 * Fetches the events the user has signed up for and updates the UI accordingly.
	 */
	private void fetchUserSignedUpEvents() {
		String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
		db.collection("Users").document(currentUserId).get().addOnSuccessListener(documentSnapshot -> {
			Attendee user = documentSnapshot.toObject(Attendee.class);
			if (user != null && user.getEventsList() != null && !user.getEventsList().isEmpty()) {
				fetchEventsByIds(user.getEventsList());
			} else {
				Log.w("EventMenuActivity", "User has no signed-up events or could not be fetched.");
			}
		}).addOnFailureListener(e -> Log.e("EventMenuActivity", "Error fetching user", e));
	}

	/**
	 * Fetches details for each event the user has signed up for using their IDs.
	 * @param eventIds List of event IDs the user has signed up for.
	 */

	private void fetchEventsByIds(List<String> eventIds) {
		signedUpEvents = new ArrayList<>();
		for (String eventId : eventIds) {
			db.collection("EventsDB").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
				Event event = documentSnapshot.toObject(Event.class);
				if (event != null) {
					signedUpEvents.add(event);
					if (signedUpEvents.size() == eventIds.size()) {
						updateUI();
					}
				}
			}).addOnFailureListener(e -> Log.e("EventMenuActivity", "Error fetching event", e));
		}
	}

	/**
	 * Called when the activity resumes. Fetches the signed-up events again to refresh the list.
	 */

	@Override
	protected void onResume() {
		super.onResume();
		fetchUserSignedUpEvents();
	}


	/**
	 * Updates the UI to display the latest list of events.
	 */

	private void updateUI() {
		eventsAdapter.setEvents(signedUpEvents);
		eventsAdapter.notifyDataSetChanged();
		Log.d("EventMenuActivity", "Adapter item count: " + eventsAdapter.getItemCount());
	}
	
}