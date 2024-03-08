package com.example.droiddesign.view;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.droiddesign.R;
import com.example.droiddesign.model.Attendee;
import com.example.droiddesign.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailsActivity extends AppCompatActivity {

	private String eventId;
	private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

		Button signUpButton = findViewById(R.id.sign_up_button);
		signUpButton.setOnClickListener(v -> signUpForEvent());
	}

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

	private void signUpForEvent() {
		// Assuming you have a method to get the current Attendee (user)
		String currentUserId = getCurrentUserId(); // Implement this according to your auth logic
		if (currentUserId == null || currentUserId.isEmpty()) {
			Toast.makeText(this, "User not logged in.", Toast.LENGTH_LONG).show();
			return;
		}

		db.collection("Users").document(currentUserId)
				.get()
				.addOnSuccessListener(documentSnapshot -> {
					Attendee user = documentSnapshot.toObject(Attendee.class);
					if (user != null) {
						user.getEventsList().add(eventId);
						db.collection("Users").document(currentUserId).set(user.toMap())
								.addOnSuccessListener(aVoid -> Toast.makeText(EventDetailsActivity.this, "Signed up successfully.", Toast.LENGTH_SHORT).show())
								.addOnFailureListener(e -> Toast.makeText(EventDetailsActivity.this, "Sign up failed.", Toast.LENGTH_SHORT).show());
					}
				})
				.addOnFailureListener(e -> Toast.makeText(EventDetailsActivity.this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show());
	}

	private String getCurrentUserId() {
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		return (user != null) ? user.getUid() : null;
	}
}
