package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;

import java.util.concurrent.atomic.AtomicReference;

public class AddEventSecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_second);

        // Retrieve data from Intent
        AtomicReference<Intent> intent = new AtomicReference<>(getIntent());
        String eventName = intent.get().getStringExtra("eventName");
        String eventLocation = intent.get().getStringExtra("eventLocation");
        String startTime = intent.get().getStringExtra("startTime");
        String endTime = intent.get().getStringExtra("endTime");
        String startDate = intent.get().getStringExtra("startDate");
        String endDate = intent.get().getStringExtra("endDate");

        AutoCompleteTextView dropdownMenu = findViewById(R.id.QR_menu);
        String[] listItems = new String[]{"Generate New QR", "Use Existing QR"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.list_item,
                listItems);

        dropdownMenu.setAdapter(adapter);

        Button finishAddButton = findViewById(R.id.finish_add_button);

        // Set an OnClickListener for the button
        finishAddButton.setOnClickListener(view -> {
            // Create an Event object and populate it with all details from both activities
            Event event = new Event();
            event.setEventName(eventName);
            event.setGeolocation(eventLocation); // TODO:Ensure you handle the geolocation appropriately
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setEventDate(startDate); // TODO:Adjust according to your Event class's methods
            // For endDate and other fields like QR codes and posters, we need to adjust your Event class as needed
            //TODO: for qrcode and poster

            // Set the details collected in this activity
            TextView eventDescriptionTextView = (TextView) findViewById(R.id.text_event_description);
            String eventDescription = eventDescriptionTextView.getText().toString();

            TextView maxAttendeesTextView = (TextView) findViewById(R.id.text_input_max_attendees);
            String maxAttendeesString = maxAttendeesTextView.getText().toString();
            int maxAttendees = Integer.parseInt(maxAttendeesString);

            event.setDescription(eventDescription);
            event.setSignupLimit(maxAttendees);
            // For QR code and event poster, you'll need to handle image selection/uploading

            // Finally, save the event to Firestore
            event.saveToFirestore();

            Toast.makeText(AddEventSecondActivity.this, "Event added successfully!", Toast.LENGTH_SHORT).show();

            // Optionally, navigate the user to another activity or show a success message
            // For example, finish this activity to return to a previous one
            finish();

            // Navigate to the EventDetailsActivity and clear the task stack
            intent.set(new Intent(AddEventSecondActivity.this, EventDetailsActivity.class));
            intent.get().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent.get());

    });

    }
}
