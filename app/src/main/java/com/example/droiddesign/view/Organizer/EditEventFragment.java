package com.example.droiddesign.view.Organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * EditEventFragment is a Fragment class that allows users to edit the details of an existing event.
 * It provides UI components for users to update the event name, description, maximum number of attendees,
 * start date, start time, and end time. The user can save the edited event details by clicking the "Save" button.
 */
public class EditEventFragment extends Fragment implements DatePickerFragment.DatePickerListener {
    private Event event;
    private Button backButton, saveButton;
    private Button startDateButton;
    private Button startTimeButton;
    private Button endTimeButton;
    private ImageButton eventPoster, locationPreview;
    private final Calendar startTimeCalendar = Calendar.getInstance();
    private final Calendar endTimeCalendar = Calendar.getInstance();
    private EditText eventName, maxAttendees, eventDescription;

    /**
     * Called when the fragment is first attached to its context.
     * Confirms that the host context implements the required DatePickerListener interface.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);

        // Set up buttons and view
        ImageButton backButton = view.findViewById(R.id.back_button);
        Button saveButton = view.findViewById(R.id.button_save);
        eventName = view.findViewById(R.id.event_name_input);
        startDateButton = view.findViewById(R.id.button_start_date);
        Button endDateButton = view.findViewById(R.id.button_end_date);
        startTimeButton = view.findViewById(R.id.button_start_time);
        endTimeButton = view.findViewById(R.id.button_end_time);
        ImageButton eventPosterButton = view.findViewById(R.id.button_event_poster);
        ImageButton locationPreviewButton = view.findViewById(R.id.location_preview);
        eventDescription = view.findViewById(R.id.edit_text_event_description);
        maxAttendees = view.findViewById(R.id.edit_text_num_attendees);

        // Hide end date button by default
        TextView endDateText = view.findViewById(R.id.text_end_date);
        endDateButton = view.findViewById(R.id.button_end_date);
        endDateText.setVisibility(View.INVISIBLE);
        endDateButton.setVisibility(View.INVISIBLE);

        String eventId = getArguments().getString("EVENT_ID");
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(requireContext(), "Event ID is missing.", Toast.LENGTH_LONG).show();
            requireActivity().finish();
        }

        Event.loadFromFirestore(eventId, new Event.FirestoreCallback() {
            @Override
            public void onCallback(Event event) {
                if (event != null) {
                    populateEventDetails(event);
                } else {
                    Toast.makeText(requireContext(), "Unable to retrieve event details.", Toast.LENGTH_LONG).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().finish();
            }
        });

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getParentFragmentManager(), "startTimePicker");
            }
        });

        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getParentFragmentManager(), "endTimePicker");
            }
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

        return view;
    }

    /**
     * Populates the event details in the activity's UI components.
     * @param event Event object containing the details to be displayed.
     */
    private void populateEventDetails(Event event) {
        eventName.setText(event.getEventName());
        startDateButton.setText(event.getEventDate());
        startTimeButton.setText(event.getStartTime());
        endTimeButton.setText(event.getEndTime());
        eventDescription.setText(event.getDescription());
        maxAttendees.setText(String.valueOf(event.getSignupLimit()));



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
        datePicker.show(getChildFragmentManager(), "datePicker");
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
     * Saves the edited event details to Firestore. This method retrieves the updated event details from the
     * UI components and prepares a map of the fields to update. It then updates the existing Event object in Firestore.
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

        // Check if the event object is not null
        if (event != null) {
            // Update the existing Event object in Firestore
            event.updateEventInFirestore(updatedFields);
        } else {
            Toast.makeText(requireContext(), "Event data is not loaded yet or is null.", Toast.LENGTH_SHORT).show();
        }
    }
}
