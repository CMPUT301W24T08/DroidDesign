package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.example.droiddesign.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
	private String userId, userRole, userEmail;
	SharedPreferenceHelper prefsHelper;

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

		eventsRecyclerView = findViewById(R.id.events_recycler_view);
		navigationMenu = findViewById(R.id.navigation_menu);
		ImageButton menuButton = findViewById(R.id.button_menu);
		FloatingActionButton fabQuickScan = findViewById(R.id.fab_quick_scan);
		FloatingActionButton addEventButton = findViewById(R.id.fab_add_event);
		TextView textViewEvents = findViewById(R.id.text_upcoming_events);



		prefsHelper = new SharedPreferenceHelper(this);
		String savedUserId = prefsHelper.getUserId();
		if (savedUserId != null) {
			// Use the userId from SharedPreferences
			userId = savedUserId;
			userRole = prefsHelper.getRole();
		} else {
			// No userId found in SharedPreferences, fetch it from FirebaseAuth
			fetchUserRole();
			userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
			prefsHelper.saveUserProfile(userId,userRole,userEmail);
		}

		if ("Organizer".equalsIgnoreCase(userRole)) {
			textViewEvents.setText(R.string.created_events);
		} else if ("Attendee".equalsIgnoreCase(userRole)) {
			textViewEvents.setText(R.string.upcoming_events);
		}


		eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		eventsAdapter = new EventsAdapter(eventsList, event -> {
			Intent intent = new Intent(EventMenuActivity.this, EventDetailsActivity.class);
			intent.putExtra("EVENT_ID", event.getEventId());
			toggleNavigationMenu();
			startActivity(intent);
		});
		eventsRecyclerView.setAdapter(eventsAdapter);
		fetchEvents();

		menuButton.setOnClickListener(v -> toggleNavigationMenu());
		setupRecyclerView();

		// Check if the userRole is "attendee"
		if ("attendee".equalsIgnoreCase(userRole)) {
			// If userRole is "attendee", hide the addEventButton
			addEventButton.setVisibility(View.INVISIBLE);
			// Update the layout parameters to position the button at the bottom center
			ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) fabQuickScan.getLayoutParams();
			params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
			params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
			params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
			params.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.fab_margin_bottom));
			fabQuickScan.setLayoutParams(params);

		} else {
			// If userRole is not "attendee", i.e admin or organizer show the addEventButton
			addEventButton.setVisibility(View.VISIBLE);
		}
		addEventButton.setOnClickListener(view -> {
			Intent intent = new Intent(EventMenuActivity.this, AddEventActivity.class);
			startActivity(intent);
		});

		fabQuickScan.setOnClickListener(v -> {
			// Intent to start Quick Scan Activity or any specific logic
			Intent intent = new Intent(EventMenuActivity.this, QrCodeScanActivity.class);
			startActivity(intent);
		});


		navigationMenu.getMenu().clear();

		// Inflate the menu based on user role
		if ("organizer".equalsIgnoreCase(userRole)) {
			navigationMenu.inflateMenu(R.menu.menu_navigation_organizer);
		} else if ("admin".equalsIgnoreCase(userRole)) {
			navigationMenu.inflateMenu(R.menu.menu_admin_event_menu);
		} else { // Default to attendee if no role or attendee role
			navigationMenu.inflateMenu(R.menu.menu_navigation_attendee);
		}

		// Set the navigation item selection listener
		String finalUserId = userId;
		navigationMenu.setNavigationItemSelectedListener(item -> {
			int id = item.getItemId();
			Intent intent = null;

			if (id == R.id.browse_events) {
				intent = new Intent(this, DiscoverEventsActivity.class);
			} else if (id == R.id.profile) {
				intent = new Intent(this, ProfileSettingsActivity.class);
				intent.putExtra("USER_ID", finalUserId);
			} else if (id == R.id.settings) {
				intent = new Intent(this, AppSettingsActivity.class);
			} else if (id == R.id.log_out) {
				intent = new Intent(this, LaunchScreenActivity.class);
				// Clear stored preferences
				prefsHelper.clearPreferences();
				// Set userId and userRole to null
				userId = null;
				userRole = null;
				toggleNavigationMenu();
				startActivity(intent);
				finish();
			} else if ("organizer".equalsIgnoreCase(userRole) && id == R.id.nav_manage_events) {
				intent = new Intent(this, SignedEventsActivity.class);
			}

			if (intent != null) {
				toggleNavigationMenu();
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
			Intent intent;
			// Check if the user is an organizer
//			if ("organizer".equalsIgnoreCase(userRole)) {
//				// If the user is an organizer, navigate to EditEventActivity
//				intent = new Intent(EventMenuActivity.this, EditEventFragment.class);
//			} else {
//				// For other roles, navigate to EventDetailsActivity
//				intent = new Intent(EventMenuActivity.this, EventDetailsActivity.class);
//			}
			intent = new Intent(EventMenuActivity.this, EventDetailsActivity.class);
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
	private void fetchEvents() {
		String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
		db.collection("Users").document(currentUserId).get().addOnSuccessListener(documentSnapshot -> {
			User user = documentSnapshot.toObject(User.class);
			if (user != null) {
				List<String> eventIdsToFetch;
				switch (userRole.toLowerCase()) {
					case "organizer":
						eventIdsToFetch = user.getManagedEventsList();
						break;
					case "attendee":
						eventIdsToFetch = user.getSignedEventsList();
						break;
					default:
						Log.w("EventMenuActivity", "Unrecognized user role: " + userRole);
						return;
				}

				if (eventIdsToFetch != null && !eventIdsToFetch.isEmpty()) {
					fetchEventsByIds(eventIdsToFetch);
				} else {
					Log.w("EventMenuActivity", "No events to fetch for user role: " + userRole);
				}
			} else {
				Log.w("EventMenuActivity", "User data could not be fetched.");
			}
		}).addOnFailureListener(e -> Log.e("EventMenuActivity", "Error fetching user data", e));
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
		fetchEvents();
	}

	/**
	 * Fetches the current user's role from Firestore and configures the UI based on the role.
	 * This method retrieves the role information from the 'Users' collection in Firestore
	 * using the current user's ID. Once the role is fetched, it calls {@link #configureUIBasedOnRole()}
	 * to update the UI elements based on the user's role.
	 */
	private void fetchUserRole() {
		String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
		db.collection("Users").document(currentUserId).get().addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists() && documentSnapshot.contains("role")) {
				userRole = documentSnapshot.getString("role");
				userEmail = documentSnapshot.getString("email");
				configureUIBasedOnRole();
			} else {
				Log.e("EventMenuActivity", "Role not found for user.");
			}
		}).addOnFailureListener(e -> Log.e("EventMenuActivity", "Error fetching user role", e));
	}

	/**
	 * Configures the user interface based on the user's role.
	 * This method checks the user's role and sets the visibility of the add event button accordingly.
	 * If the user is an organizer, the button is made visible and clickable; otherwise, it is hidden.
	 * This method should be called after the user's role has been determined by {@link #fetchUserRole()}.
	 */
	private void configureUIBasedOnRole() {
		FloatingActionButton addEventButton = findViewById(R.id.fab_add_event);
		if ("Organizer".equalsIgnoreCase(userRole)) {
			addEventButton.setVisibility(View.VISIBLE);
			addEventButton.setOnClickListener(view -> {
				Intent intent = new Intent(EventMenuActivity.this, AddEventActivity.class);
				startActivity(intent);
			});
		} else {
			addEventButton.setVisibility(View.GONE);
		}
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