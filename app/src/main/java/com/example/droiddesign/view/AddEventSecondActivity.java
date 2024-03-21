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

/**
 * The AddEventSecondActivity class is responsible for collecting additional details for a new event creation process.
 * It allows the organizer to upload an image for the event, choose to generate a new QR code or use an existing one,
 * and finalize event details. After completion, the event data is saved to Firestore and the user is directed
 * to the EventDetailsActivity with the newly created event.
 */
public class AddEventSecondActivity extends AppCompatActivity {

    /**
     * Request code for triggering an image upload intent.
     */
    private static final int UPLOAD_IMAGE_REQUEST = 1;

    /**
     * Request code for triggering a QR code generation intent.
     */
    private static final int GENERATE_QR_REQUEST = 2;

    /**
     * Event object to store and manage the event details being added.
     */

    private Event event;
    /**
     * Instance of FirebaseFirestore to interact with Firestore database.
     */
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Initializes the activity for the second step of adding a new event.
     * Sets up UI components and retrieves event details passed from the previous activity.
     * Initializes a new Event object and assigns a unique ID.
     *
     * @param savedInstanceState Contains data supplied in onSaveInstanceState(Bundle) or null if no data was supplied.
     */
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
        cancelButton.setOnClickListener(view -> {
            finish();
        });
    }

    /**
     * Populates the Event object with data received from the Intent extras.
     * Sets the event name, location, start time, end time, and date.
     *
     * @param intent The intent carrying the event details from the previous activity.
     */
    private void populateEventFromIntent(Intent intent) {
        event.setEventName(intent.getStringExtra("eventName"));
        event.setEventLocation(intent.getStringExtra("eventLocation"));
        event.setStartTime(intent.getStringExtra("startTime"));
        event.setEndTime(intent.getStringExtra("endTime"));
        event.setEventDate(intent.getStringExtra("startDate"));
        event.setGeolocation(intent.getStringExtra("eventLocation"));
    }

    /**
     * Sets up the dropdown menu for QR code generation options.
     * Configures the dropdown with "Generate New QR" and "Use Existing QR" options
     * and sets an item click listener to handle the selection.
     *
     * @param dropdownMenu The AutoCompleteTextView that acts as the dropdown menu.
     */
    private void setupDropdownMenu(AutoCompleteTextView dropdownMenu) {
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
    }
    /**
     * Processes the result returned by any activities launched for result.
     * Handles results for both image poster upload and QR code generation.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached as Extras).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == UPLOAD_IMAGE_REQUEST) {
                event.setImagePosterId(data.getStringExtra("imagePosterUrl"));
            } else if (requestCode == GENERATE_QR_REQUEST) {
                String qrCodeUrl = data.getStringExtra("qrCodeUrl");
                Log.d("AddEventSecondActivity", "QR Code URL: " + qrCodeUrl);
                event.setQrCode(qrCodeUrl);
            }
        }
    }

    /**
     * Saves the event to Firestore. Gathers additional event details from the user input,
     * validates them, and calls the Event object's saveToFirestore method.
     * If successful, transitions to the EventDetailsActivity displaying the newly added event.
     */
    private void saveEvent() {
        TextView eventDescriptionTextView = findViewById(R.id.text_input_event_description);
        String eventDescription = eventDescriptionTextView.getText().toString();
        Log.d("AddEvent", "Max Attendees String: '" + eventDescription + "'");
        event.setDescription(eventDescription);

        TextView maxAttendeesTextView = findViewById(R.id.input_number_max_attendees);
        String maxAttendeesString = maxAttendeesTextView.getText().toString().trim();
        Log.d("AddEvent", "Max Attendees String: '" + maxAttendeesString + "'");

        try {
            int maxAttendees = Integer.parseInt(maxAttendeesString);
            event.setSignupLimit(maxAttendees);
        } catch (NumberFormatException e) {
            Toast.makeText(AddEventSecondActivity.this, "Invalid number for maximum attendees", Toast.LENGTH_SHORT).show();
            return;
        }

        event.saveToFirestore();

        Toast.makeText(AddEventSecondActivity.this, "Event added successfully!", Toast.LENGTH_SHORT).show();
        finish();

        Intent detailsIntent = new Intent(AddEventSecondActivity.this, EventDetailsActivity.class);
        detailsIntent.putExtra("EVENT_ID", event.getEventId());
        detailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        // Add user event to their list
        SharedPreferenceHelper prefsHelper = new SharedPreferenceHelper(this);
        String currentUserId = prefsHelper.getUserId();
        setAttributeValue(currentUserId, "managedEventsList",event.getEventId());
        startActivity(detailsIntent);
    }

    private void setAttributeValue(String userId, String attribute, Object value) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(userId);

        // Implement the logic to set attribute value based on attribute name
        if (attribute.equals("userName")) {
            userRef.update("userName", value);
        } else if (attribute.equals("role")) {
            userRef.update("role", value);
        } else if (attribute.equals("managedEventsList")) {
            if (value instanceof String) {
                userRef.update("managedEventsList", FieldValue.arrayUnion(value));
            }
        } else if (attribute.equals("signedEventsList")) {
            if (value instanceof String) {
                userRef.update("signedEventsList", FieldValue.arrayUnion(value));
            }
        }
        // TODO remove later, but they are currently to remember to add other attr
//		map.put("userId", userId);
//		map.put("userName", userName);
//		map.put("role", role);
//		map.put("registered", registered);
//		map.put("email", email);
//		map.put("company", company);
//		map.put("phone", phone);
//		map.put("profileName", profileName);
//		map.put("profilePic", profilePic);
//		map.put("signedEventsList", signedEventsList);
//		map.put("managedEventsList", managedEventsList);
    }
}
