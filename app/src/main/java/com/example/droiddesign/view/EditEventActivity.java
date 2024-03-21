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
    private final Calendar startTimeCalendar = Calendar.getInstance();
    private final Calendar endTimeCalendar = Calendar.getInstance();
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

    /**
     * Saves the edited event details by retrieving the updated values from the UI components and updating the existing
     * Event object in Firestore. The updated event details include the event name, description, maximum number of attendees,
     * start date, start time, and end time. These details are extracted from the respective UI components, compiled into a map,
     * and then used to update the Event object in Firestore.
     * <p>
     * If the Event object is not yet loaded or is null (indicating the event data is not available), a toast message is displayed
     * to inform the user that the event data is not loaded or is null.
     * <p>
     * Note: This method is private and intended to be called within the class it is defined in, typically in response to a UI
     * action such as pressing a "Save" button after editing event details.
     * <p>
     * Preconditions:
     * - UI components for event name, description, max attendees, start date, start time, and end time must be initialized and accessible.
     * <p>
     * Postconditions:
     * - If the Event object is not null, the event is updated in Firestore with the new details.
     * - If the Event object is null, a toast message is shown to the user indicating the data is not available.
     * <p>
     * TODO: Implement milestones properly as part of the event update process.
     */
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
        // TODO: Properly implement milestones

        // Check if the event object is not null
        if (event != null) {
            // Update the existing Event object in Firestore
            event.updateEventInFirestore(updatedFields);
        } else {
            Toast.makeText(this, "Event data is not loaded yet or is null.", Toast.LENGTH_SHORT).show();
        }
    }
}
