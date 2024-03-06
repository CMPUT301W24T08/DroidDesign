package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.example.droiddesign.model.GenerateQRCode;
import com.google.android.material.button.MaterialButton;

import com.bumptech.glide.Glide;

import com.example.droiddesign.model.Event;

public class AddEventSecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_second);

        // Retrieve data from Intent
        Intent intent = getIntent();
        String eventName = intent.getStringExtra("eventName");
        String eventLocation = intent.getStringExtra("eventLocation");
        String startTime = intent.getStringExtra("startTime");
        String endTime = intent.getStringExtra("endTime");
        String startDate = intent.getStringExtra("startDate");
        String endDate = intent.getStringExtra("endDate");
        MaterialButton buttonUploadPoster = findViewById(R.id.button_upload_poster);

        AutoCompleteTextView dropdownMenu = findViewById(R.id.QR_menu);
        String[] listItems = new String[]{"Generate New QR", "Use Existing QR"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.list_item,
                listItems);

        dropdownMenu.setAdapter(adapter);

        buttonUploadPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddEventSecondActivity.this, ImageUploadActivity.class);
                startActivity(intent);
            }
        });

        dropdownMenu.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            // Check if "Generate New QR" is selected
            if ("Generate New QR".equals(selectedItem)) {
                // Navigate to QrCodeGeneratorActivity
                Intent qrGeneratorIntent = new Intent(AddEventSecondActivity.this, QrCodeGeneratorActivity.class);
                startActivity(qrGeneratorIntent);
            }
        });

        Button finishAddButton = findViewById(R.id.finish_add_button);

        finishAddButton.setOnClickListener(view -> {
            Event event = new Event();
            event.setEventName(eventName);
            event.setGeolocation(eventLocation);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setEventDate(startDate);

            TextView eventDescriptionTextView = findViewById(R.id.text_input_event_description);
            String eventDescription = eventDescriptionTextView.getText().toString();

            TextView maxAttendeesTextView = findViewById(R.id.text_input_max_attendees);
            String maxAttendeesString = maxAttendeesTextView.getText().toString();
            int maxAttendees = Integer.parseInt(maxAttendeesString);

            event.setDescription(eventDescription);
            event.setSignupLimit(maxAttendees);

            // Save the event to Firestore
            event.saveToFirestore();

            Toast.makeText(AddEventSecondActivity.this, "Event added successfully!", Toast.LENGTH_SHORT).show();
            finish();

            Intent detailsIntent = new Intent(AddEventSecondActivity.this, EventDetailsActivity.class);
            detailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(detailsIntent);
        });
    }
}
