package com.example.droiddesign.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditEventActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener {
    private Event event;
    private Button backButton, saveButton;
    private String eventId;
    private Button startDateButton, endDateButton, startTimeButton, endTimeButton;
    private ImageButton eventPoster, locationPreview;
    private Calendar startTimeCalendar = Calendar.getInstance();
    private Calendar endTimeCalendar = Calendar.getInstance();
    private EditText eventName, maxAttendees, milestones, eventDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        // Set up buttons and view
        ImageButton backButton = findViewById(R.id.back_button);
        Button saveButton = findViewById(R.id.button_save);
        EditText eventNameInput = findViewById(R.id.event_name_input);
        Button startDateButton = findViewById(R.id.button_start_date);
        Button endDateButton = findViewById(R.id.button_end_date);
        Button startTimeButton = findViewById(R.id.button_start_time);
        Button endTimeButton = findViewById(R.id.button_end_time);
        ImageButton eventPosterButton = findViewById(R.id.button_event_poster);
        ImageButton locationPreviewButton = findViewById(R.id.location_preview);
        EditText eventDescription = findViewById(R.id.edit_text_event_description);
        EditText numAttendees = findViewById(R.id.edit_text_num_attendees);
        EditText milestone = findViewById(R.id.edit_text_milestone);

        // Hide end date button by default
        TextView endDateText = findViewById(R.id.text_end_date);
        endDateButton = findViewById(R.id.button_end_date);
        endDateText.setVisibility(View.INVISIBLE);
        endDateButton.setVisibility(View.INVISIBLE);

        eventId = getIntent().getStringExtra("EVENT_ID");
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Event ID is missing.", Toast.LENGTH_LONG).show();
            finish();
        }

        Event.loadFromFirestore(eventId, new Event.FirestoreCallback() {
            @Override
            public void onCallback(Event event) {
                if (event != null) {
                    populateEventDetails(event);
                } else {
                    Toast.makeText(EditEventActivity.this, "Unable to retrieve event details.", Toast.LENGTH_LONG).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        startTimeButton.setOnClickListener(v -> {
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "startTimePicker");
        });

        endTimeButton.setOnClickListener(v -> {
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "endTimePicker");
        });

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEditedEvent();
            }
        });

        backButton.setOnClickListener(v -> {
            finish();
        });
    }


    /**
     * Populates the event details in the activity's UI components.
     * @param event Event object containing the details to be displayed.
     */
    private void populateEventDetails(Event event) {
        eventName = findViewById(R.id.event_name_input);
        startDateButton = findViewById(R.id.button_start_date);
        endDateButton = findViewById(R.id.button_end_date);
        startTimeButton = findViewById(R.id.button_start_time);
        endTimeButton = findViewById(R.id.button_end_time);
        eventPoster = findViewById(R.id.button_event_poster);
        locationPreview = findViewById(R.id.location_preview);
        eventDescription = findViewById(R.id.edit_text_event_description);
        maxAttendees = findViewById(R.id.edit_text_num_attendees);
        milestones = findViewById(R.id.edit_text_milestone);

        eventName.setText(event.getEventName());
        startDateButton.setText(event.getEventDate());
        startTimeButton.setText(event.getStartTime());
        endTimeButton.setText(event.getEndTime());
        eventDescription.setText(event.getDescription());
        maxAttendees.setText(String.valueOf(event.getSignupLimit()));
        milestones.setText("1234"); // TODO properly implement milestones


        Glide.with(this)
                .load(event.getImagePosterId())
                .placeholder(R.drawable.image_placeholder)
                .into(eventPoster);
    }

    /**
     * Displays a date picker dialog to allow the user to select a date.
     * This method creates a new instance of DatePickerFragment and displays it.
     */
    private void showDatePickerDialog() {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Handles the date selected by the user in the DatePickerFragment. Depending on whether the user is
     * setting a start or end date, this method updates the appropriate button's text to reflect the
     * selected date. It formats the date according to the "dd MMMM" pattern (e.g., "15 March").
     *
     * @param year The year that was picked by the user.
     * @param month The month that was picked by the user (0-indexed, e.g., 0 for January).
     * @param day The day of the month that was picked by the user.
     */
    public void onDateSet(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date eventDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        String dateString = dateFormat.format(eventDate);

        startDateButton.setText(dateString);
    }

    private void saveEditedEvent() {
        // Retrieve the updated event details from the UI components
        String updatedEventName = eventName.getText().toString();
        String updatedEventDescription = eventDescription.getText().toString();
        int updatedMaxAttendees = Integer.parseInt(maxAttendees.getText().toString());
        String updatedStartDate = startDateButton.getText().toString();
        String updatedStartTime = startTimeButton.getText().toString();
        String updatedEndTime = endTimeButton.getText().toString();

        // Prepare a map of the fields to update
        Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("eventName", updatedEventName);
        updatedFields.put("description", updatedEventDescription);
        updatedFields.put("signupLimit", updatedMaxAttendees);
        updatedFields.put("eventDate", updatedStartDate);
        updatedFields.put("startTime", updatedStartTime);
        updatedFields.put("endTime", updatedEndTime);
        // TODO properly implement milestones

        // Update the existing Event object in Firestore using the eventId
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId)
                .update(updatedFields)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditEventActivity.this, "Event updated successfully!", Toast.LENGTH_SHORT).show();
                    // Optionally close the activity or navigate the user elsewhere
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EditEventActivity.this, "Error updating event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
