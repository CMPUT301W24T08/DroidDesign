package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;
import com.google.android.material.button.MaterialButton;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class AddEventSecondActivity extends AppCompatActivity {

    private static final int UPLOAD_IMAGE_REQUEST = 1;
    private static final int GENERATE_QR_REQUEST = 2;

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_second);

        event = new Event();
        generateUniqueEventId();  // Generate and set unique event ID immediately.
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

        finishAddButton.setOnClickListener(view -> saveEvent());
    }

    private void generateUniqueEventId() {
        String uniqueID = UUID.randomUUID().toString();
        event.setEventId(uniqueID);
    }

    private void populateEventFromIntent(Intent intent) {
        event.setEventName(intent.getStringExtra("eventName"));
        event.setEventLocation(intent.getStringExtra("eventLocation"));
        event.setStartTime(intent.getStringExtra("startTime"));
        event.setEndTime(intent.getStringExtra("endTime"));
        event.setEventDate(intent.getStringExtra("startDate"));
        // Assuming geolocation is derived or identical to eventLocation.
        event.setGeolocation(intent.getStringExtra("eventLocation"));
    }

    private void setupDropdownMenu(AutoCompleteTextView dropdownMenu) {
        String[] listItems = new String[]{"Generate New QR", "Use Existing QR"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, listItems);
        dropdownMenu.setAdapter(adapter);

        dropdownMenu.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            if ("Generate New QR".equals(selectedItem)) {
                Intent qrGeneratorIntent = new Intent(AddEventSecondActivity.this, QrCodeGeneratorActivity.class);
                qrGeneratorIntent.putExtra("eventId", event.getEventId());  // Pass the unique event ID.
                startActivityForResult(qrGeneratorIntent, GENERATE_QR_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == UPLOAD_IMAGE_REQUEST) {
                event.setImagePosterId(data.getStringExtra("imagePosterUrl"));
            } else if (requestCode == GENERATE_QR_REQUEST) {
                String qrCodeUrl = data.getStringExtra("qrCodeUrl");
                event.setQrCode(qrCodeUrl);
            }
        }
    }

    private void saveEvent() {
        TextView eventDescriptionTextView = findViewById(R.id.text_input_event_description);
        String eventDescription = eventDescriptionTextView.getText().toString();
        event.setDescription(eventDescription);

        TextView maxAttendeesTextView = findViewById(R.id.input_number_max_attendees);
        String maxAttendeesString = maxAttendeesTextView.getText().toString().trim();

        try {
            int maxAttendees = Integer.parseInt(maxAttendeesString);
            event.setSignupLimit(maxAttendees);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number for maximum attendees", Toast.LENGTH_SHORT).show();
            return;
        }

        event.saveToFirestore();

        Toast.makeText(this, "Event added successfully!", Toast.LENGTH_SHORT).show();
        finish();

        Intent detailsIntent = new Intent(this, EventDetailsActivity.class);
        detailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(detailsIntent);
    }
}
