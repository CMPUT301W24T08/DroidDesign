package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.example.droiddesign.model.User;
import com.example.droiddesign.model.UsersDB;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Activity to allow the user to select a role (Admin, Organizer, Attendee).
 * This class is responsible for handling user interactions with role selection buttons,
 * performing user registration for the selected role, and navigating to the next activity.
 */

public class RoleSelectionActivity extends AppCompatActivity {

	/**
	 * Buttons used for selecting the role of the new or existing user.
	 * Admin image button allows the user to select the 'Admin' role.
	 * Organizer image button allows the user to select the 'Organizer' role.
	 * Attendee image button allows the user to select the 'Attendee' role.
	 */

	private MaterialButton adminImage, organizerImage, attendeeImage;

	/**
	 * An instance of FirebaseFirestore, providing access to the Firebase Firestore database.
	 * Used here to interact with the 'users' collection in Firestore.
	 */
	private final FirebaseFirestore db = FirebaseFirestore.getInstance();

	/**
	 * Reference to the 'users' collection in the Firestore database.
	 * This collection contains documents where each document represents a user and their associated data.
	 */

	CollectionReference usersCollection = db.collection("UsersDB");

	/**
	 * Constant used as a key in SharedPreferences to store and retrieve the user's ID.
	 * This ID is used to identify the user across different sessions and activities.
	 */
	SharedPreferenceHelper prefsHelper;

	/**
	 * Called when the activity is starting.
	 * This is where most initialization should go: calling setContentView(int) to inflate
	 * the activity's UI, using findViewById(int) to programmatically interact with widgets in the UI.
	 * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
	 *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
	 *                           Note: Otherwise it is null.
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_role_selection_unregistered);

		// Initialize SharedPreferences
		prefsHelper = new SharedPreferenceHelper(this);
		// Not a new user scenario
		if (!prefsHelper.isFirstTimeUser()) {
			// Returning user scenario
			navigateToEventMenu();
		}
		// Initialize views and set click listeners
		adminImage = findViewById(R.id.admin_button);
		organizerImage = findViewById(R.id.organizer_button);
		attendeeImage = findViewById(R.id.attendee_button);

		setRoleSelectionListeners();
	}


	/**
	 * Sets click listeners for each role selection button.
	 * Defines the actions to be taken when each role button is clicked.
	 */
	private void setRoleSelectionListeners() {
		attendeeImage.setOnClickListener(v -> handleRoleSelection("attendee"));
		organizerImage.setOnClickListener(v -> handleRoleSelection("organizer"));
		adminImage.setOnClickListener(v -> handleRoleSelection("admin"));
	}

	/**
	 * Handles the role selection process.
	 * Depending on the role selected, it performs actions such as creating a new user or navigating to the EventMenuActivity.
	 * @param role The role selected by the user.
	 */

	private void handleRoleSelection(String role) {
		Toast.makeText(RoleSelectionActivity.this, "Quick start!", Toast.LENGTH_SHORT).show();
		// New user scenario

		// Save user profile information
		String userId = UUID.randomUUID().toString(); // Generate a new user ID
		prefsHelper.saveUserProfile(userId, role);
		// Now that the user profile information is saved locally, proceed to save the user to Firestore
		FirebaseFirestore firestore = FirebaseFirestore.getInstance();
		UsersDB userdb = new UsersDB(firestore);
		// Create user in Firestore
		User newUser = new UnregisteredUser(userId, role, false);
		userdb.addUser(newUser);

		navigateToEventMenu();

	}

	/**
	 * Navigates to the EventMenuActivity.
	 * Passes the role and user ID as intent extras for use in the next activity.
	 */
		private void navigateToEventMenu() {
		Intent intent = new Intent(RoleSelectionActivity.this, EventMenuActivity.class);
		startActivity(intent);
		finish();
	}

	public class UnregisteredUser extends User {
		/**
		 * The profile name of the attendee.
		 */
		private String profileName;

		/**
		 * The email address of the attendee.
		 */
		private String email;

		/**
		 * The phone number of the attendee.
		 */
		private String phone;

		/**
		 * The URL or path to the profile picture of the attendee.
		 */
		private String profilePic;

		/**
		 * A flag indicating whether geolocation features are enabled for the attendee.
		 */
		private boolean geolocation;

		/**
		 * A list of event IDs that this attendee is associated with.
		 */
		private ArrayList<String> eventsList;
		private boolean registered;

		public UnregisteredUser(String userId, String role, boolean b) {
			super(userId, role);
			this.registered = false;
			this.profileName = "";
			this.email = "";
			this.phone = "";
			this.profilePic = "";
			this.geolocation = false;
			this.eventsList = new ArrayList<>();
		}

		// Getter for registered status
		public boolean isRegistered() {
			return registered;
		}

		@Override
		public HashMap<String, Object> toMap() {
			HashMap<String, Object> map = new HashMap<>();
			map.put("userId", getUserId());
			map.put("role", getRole());
			map.put("registered", registered);
			map.put("profilePic", profilePic);
			map.put("geolocation", geolocation);
			map.put("eventsList", eventsList);
			return map;
		}
	}
}

