package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.droiddesign.R;
import com.example.droiddesign.model.User;
import com.example.droiddesign.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
/**
 * Activity class that presents the details of an event.
 * It retrieves the event data from Firestore based on the passed event ID and allows the user to sign up for the event.
 */
public class EventDetailsActivity extends AppCompatActivity {

	/**
	 * The ID of the event whose details are to be displayed.
	 */
	private String eventId;

	/**
	 * Instance of FirebaseFirestore to interact with Firestore database.
	 */
	private FirebaseFirestore db = FirebaseFirestore.getInstance();



	/**
	 * Initializes the activity, sets the content view, and initiates the process to fetch and display event details.
	 * Sets up the interaction logic for UI elements like back button and sign up button.
	 * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_details);

		eventId = getIntent().getStringExtra("EVENT_ID");
		if (eventId == null || eventId.isEmpty()) {
			Toast.makeText(this, "Event ID is missing.", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		Event.loadFromFirestore(eventId, new Event.FirestoreCallback() {
			@Override
			public void onCallback(Event event) {
				if (event != null) {
					populateEventDetails(event);
				} else {
					Toast.makeText(EventDetailsActivity.this, "Unable to retrieve event details.", Toast.LENGTH_LONG).show();
				}
			}
		});

		ImageButton backButton = findViewById(R.id.back_button);
		backButton.setOnClickListener(v -> finish());

		Button goToMenuButton = findViewById(R.id.edit_event_details_button);
		goToMenuButton.setOnClickListener(v -> {
			Intent intent = new Intent(EventDetailsActivity.this, EventMenuActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish(); // If you don't want to return to this activity from EventMenuActivity
		});


		Button signUpButton = findViewById(R.id.sign_up_button);
		signUpButton.setOnClickListener(v -> signUpForEvent());
	}

	private void navigateBackToEventMenu() {
		Intent intent = new Intent(EventDetailsActivity.this, EventMenuActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	/**
	 * Populates the event details in the activity's UI components.
	 * @param event Event object containing the details to be displayed.
	 */

	private void populateEventDetails(Event event) {
		TextView eventName = findViewById(R.id.event_name);
		TextView eventDateAndTime = findViewById(R.id.date_and_time);
		TextView eventDescription = findViewById(R.id.event_description);
		ImageView eventPoster = findViewById(R.id.image_event_poster);

		eventName.setText(event.getEventName());
		String dateTime = event.getEventDate() + " " + event.getStartTime();
		eventDateAndTime.setText(dateTime);
		eventDescription.setText(event.getDescription());

		Glide.with(this)
				.load(event.getImagePosterId())
				.placeholder(R.drawable.image_placeholder)
				.into(eventPoster);
	}

	/**
	 * Signs up the current logged-in user for the event and updates the user's event list in the Firestore database.
	 * Shows a toast message based on the success or failure of the operation.
	 */

	private void signUpForEvent() {
		// Assuming you have a method to get the current User (user)
		String currentUserId = getCurrentUserId(); // Implement this according to your auth logic
		if (currentUserId == null || currentUserId.isEmpty()) {
			Toast.makeText(this, "User not logged in.", Toast.LENGTH_LONG).show();
			return;
		}

		Toast.makeText(this, "SignUpEvent called"+currentUserId, Toast.LENGTH_LONG).show();
		db.collection("Users").document(currentUserId)
				.get()
				.addOnSuccessListener(documentSnapshot -> {
					User user = documentSnapshot.toObject(User.class);
					if (user != null) {
						user.getSignedEventsList().add(eventId);
						db.collection("Users").document(currentUserId).set(user.toMap())
								.addOnSuccessListener(aVoid -> Toast.makeText(EventDetailsActivity.this, "Signed up successfully.", Toast.LENGTH_SHORT).show())
								.addOnFailureListener(e -> Toast.makeText(EventDetailsActivity.this, "Sign up failed.", Toast.LENGTH_SHORT).show());
					}
				})
				.addOnFailureListener(e -> Toast.makeText(EventDetailsActivity.this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show());
	}

	/**
	 * Retrieves the ID of the currently logged-in user from FirebaseAuth.
	 * @return The current user's ID or null if no user is logged in.
	 */

	private String getCurrentUserId() {
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		return (user != null) ? user.getUid() : null;
	}
}
