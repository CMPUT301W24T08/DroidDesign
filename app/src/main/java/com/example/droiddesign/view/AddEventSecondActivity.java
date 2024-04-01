package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class AddEventSecondActivity extends AppCompatActivity {
    private static final int UPLOAD_IMAGE_REQUEST = 1;
    private static final int GENERATE_QR_REQUEST = 2;

    private Event event;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_second);

        event = new Event();
        String uniqueID = UUID.randomUUID().toString();
        event.setEventId(uniqueID);

        Intent intent = getIntent();
        populateEventFromIntent(intent);

        MaterialButton buttonUploadPoster = findViewById(R.id.button_upload_poster);
        AutoCompleteTextView dropdownMenu = findViewById(R.id.QR_menu);
        Button finishAddButton = findViewById(R.id.finish_add_button);

        setupDropdownMenu(dropdownMenu);

        buttonUploadPoster.setOnClickListener(view -> {
            Intent imageUploadIntent = new Intent(AddEventSecondActivity.this, ImageUploadActivity.class);
            startActivityForResult(imageUploadIntent, UPLOAD_IMAGE_REQUEST);
        });

        finishAddButton.setOnClickListener(view -> {
            saveEvent();
        });

        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(view -> finish());
    }

    private void populateEventFromIntent(Intent intent) {
        try {
            event.setEventName(intent.getStringExtra("eventName"));
            event.setEventLocation(intent.getStringExtra("eventLocation"));
            event.setStartTime(intent.getStringExtra("startTime"));
            event.setEndTime(intent.getStringExtra("endTime"));
            event.setEventDate(intent.getStringExtra("startDate"));
            event.setGeolocation(intent.getStringExtra("eventLocation"));
        } catch (Exception e) {
            Log.e("AddEventSecondActivity", "Error populating event from intent", e);
            Toast.makeText(this, "Error loading event details", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDropdownMenu(AutoCompleteTextView dropdownMenu) {
        try {
            String[] listItems = new String[]{"Generate New QR", "Use Existing QR"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, listItems);
            dropdownMenu.setAdapter(adapter);

            dropdownMenu.setOnItemClickListener((parent, view, position, id) -> {
                String selectedItem = (String) parent.getItemAtPosition(position);
                if ("Generate New QR".equals(selectedItem)) {
                    Intent qrGeneratorIntent = new Intent(AddEventSecondActivity.this, QrCodeGeneratorActivity.class);
                    qrGeneratorIntent.putExtra("eventID", event.getEventId());
                    startActivityForResult(qrGeneratorIntent, GENERATE_QR_REQUEST);
                }
            });
        } catch (Exception e) {
            Log.e("AddEventSecondActivity", "Error setting up dropdown menu", e);
            Toast.makeText(this, "Error setting up QR options", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            try {
                if (requestCode == UPLOAD_IMAGE_REQUEST) {
                    event.setImagePosterId(data.getStringExtra("imagePosterUrl"));
                } else if (requestCode == GENERATE_QR_REQUEST) {
                    String shareQrUrl = data.getStringExtra("shareQrUrl");
                    String shareQrId = data.getStringExtra("shareQrId");
                    String checkInQrUrl = data.getStringExtra("checkInQrUrl");
                    String checkInQrId = data.getStringExtra("checkInId");
                    Log.d("AddEventSecondActivity", "QR Code URL: " + shareQrUrl);
                    event.setShareQrCode(shareQrUrl, shareQrId);
                    event.setCheckInQrCode(checkInQrUrl, checkInQrId);
                }
            } catch (Exception e) {
                Log.e("AddEventSecondActivity", "Error processing activity result", e);
                Toast.makeText(this, "Error processing result", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveEvent() {
        TextView eventDescriptionTextView = findViewById(R.id.text_input_event_description);
        String eventDescription = eventDescriptionTextView.getText().toString();
        Log.d("AddEvent", "Max Attendees String: '" + eventDescription + "'");
        event.setDescription(eventDescription);

        TextView maxAttendeesTextView = findViewById(R.id.input_number_max_attendees);
        TextView milestoneTextView = findViewById(R.id.input_number_milestone);
        String maxAttendeesString = maxAttendeesTextView.getText().toString().trim();
        String milestoneString = milestoneTextView.getText().toString().trim();

        try {
            int maxAttendees = Integer.parseInt(maxAttendeesString);
            int milestone = Integer.parseInt(milestoneString);
            event.setSignupLimit(maxAttendees);
            event.setSignupLimit(milestone);
        } catch (NumberFormatException e) {
            event.setSignupLimit(null);
            event.setSignupLimit(null);}
        if (!maxAttendeesString.isEmpty()) {
            try {
                int maxAttendees = Integer.parseInt(maxAttendeesString);
                event.setSignupLimit(maxAttendees);
            } catch (NumberFormatException e) {
                Toast.makeText(AddEventSecondActivity.this, "Invalid number for maximum attendees", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add user event to their managedEventsList
            SharedPreferenceHelper prefsHelper = new SharedPreferenceHelper(this);
            String currentUserId = prefsHelper.getUserId();
            event.setOrganizerOwnerId(currentUserId);
            event.saveToFirestore();

            DocumentReference userRef = db.collection("Users").document(currentUserId);
            userRef.update("managedEventsList", FieldValue.arrayUnion(event.getEventId()))
                    .addOnSuccessListener(aVoid -> Toast.makeText(AddEventSecondActivity.this, "Event added successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.e("AddEventSecondActivity", "Error adding event to user's managedEventsList", e));

            Intent detailsIntent = new Intent(AddEventSecondActivity.this, EventDetailsActivity.class);
            detailsIntent.putExtra("EVENT_ID", event.getEventId());
            detailsIntent.putExtra("ORIGIN", "AddEventSecondActivity");
            detailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(detailsIntent);

            finish();
        } catch (Exception e) {
            Log.e("AddEventSecondActivity", "Error saving event", e);
            Toast.makeText(AddEventSecondActivity.this, "Error saving event", Toast.LENGTH_SHORT).show();
        }
    }
}

