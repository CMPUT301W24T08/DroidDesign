package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.droiddesign.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * An activity that provides UI for adding a new event. It allows users to select start and end dates and times.
 */
public class AddEventActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener {
    /**
     * Button to trigger the end date picker.
     */
    private Button btnEndDate;

    /**
     * Button to trigger the start date picker.
     */
    private Button btnStartDate;

    /**
     * Button to trigger the start time picker.
     */
    private Button btnStartTime;

    /**
     * Button to trigger the end time picker.
     */
    private Button btnEndTime;

    /**
     * Flag to identify whether the date being picked is the start date.
     */
    private Boolean isStartDate;

    /**
     * Calendar instance to keep track of the start time.
     */
    Calendar startTimeCalendar = Calendar.getInstance();

    /**
     * Calendar instance to keep track of the end time.
     */
    Calendar endTimeCalendar = Calendar.getInstance();

    /**
     * Initializes the activity, setting up the user interface for adding a new event. This includes initializing
     * input fields for the event's name and location, setting up date and time pickers for the event's start and end
     * times, and configuring visibility and interactivity based on the event duration type (single day or multi-day).
     * The method also sets listeners for button clicks to handle various interactions like showing date/time pickers,
     * cancelling the event addition, or proceeding to the next page for further event details.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this bundle contains the most recent data provided by onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Get event name and location as Strings
        TextInputEditText eventNameInput = findViewById(R.id.text_input_event_name);
        TextInputEditText eventLocationInput = findViewById(R.id.text_input_location);

        // Initialize Start date button to have the current date + 1 day
        btnStartDate = findViewById(R.id.button_start_date);
        Calendar currentDate = Calendar.getInstance();
        Calendar startDate = (Calendar) currentDate.clone();
        startDate.add(Calendar.DATE, 1);
        String startDateFormatted = new SimpleDateFormat("dd MMM", Locale.getDefault()).format(startDate.getTime());
        btnStartDate.setText(startDateFormatted);

        // Set default button behaviour to gone and set button to display current date + 2 days
        TextView endDateText = findViewById(R.id.textView3);
        Calendar endDate = (Calendar) currentDate.clone();
        endDate.add(Calendar.DATE, 2);
        String endDateFormatted = new SimpleDateFormat("dd MMM", Locale.getDefault()).format(endDate.getTime());
        btnEndDate = findViewById(R.id.button_end_date);
        btnEndDate.setText(endDateFormatted);
        btnEndDate.setVisibility(View.GONE);
        endDateText.setVisibility(View.GONE);

        btnStartTime = findViewById(R.id.button_start_time);
        btnEndTime = findViewById(R.id.button_end_time);

        Button btnCancelAdd = findViewById(R.id.button_cancel);

        SwitchMaterial switchMultiDay = findViewById(R.id.switch_is_multi_day);

        // Initialize starting time to current time + 1hr
        startTimeCalendar.add(Calendar.HOUR_OF_DAY, 1);
        startTimeCalendar.set(Calendar.MINUTE, 0);
        String startTimeFormatted = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(startTimeCalendar.getTime());
        btnStartTime.setText(startTimeFormatted);

        // Initialize ending time to current time + 2hr
        endTimeCalendar.add(Calendar.HOUR_OF_DAY, 2);
        endTimeCalendar.set(Calendar.MINUTE, 0);
        String endTimeFormatted = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(endTimeCalendar.getTime());
        btnEndTime.setText(endTimeFormatted);

        switchMultiDay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnEndDate.setEnabled(!isChecked);
            btnEndDate.setVisibility(!isChecked ? View.VISIBLE : View.GONE);
            endDateText.setVisibility(!isChecked ? View.VISIBLE : View.GONE);
        });


        btnCancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnStartTime.setOnClickListener(v -> {
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "startTimePicker");
        });

        btnEndTime.setOnClickListener(v -> {
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "endTimePicker");
        });

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartDate = true;
                showDatePickerDialog();
            }
        });

        btnEndDate.setOnClickListener(v -> {
            if (btnEndDate.getVisibility() == View.VISIBLE) {
                isStartDate = false;
                showDatePickerDialog();
            }
        });

        btnCancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FloatingActionButton fabNextPage = findViewById(R.id.fab_next_page);
        fabNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass values for event to next activity
                Intent intent = new Intent(AddEventActivity.this, AddEventSecondActivity.class);
                // Start the AddEventSecondActivity
                TextInputEditText eventNameInput = findViewById(R.id.text_input_event_name);
                TextInputEditText eventLocationInput = findViewById(R.id.text_input_location);

                // Convert event start and end times to String
                String eventName = eventNameInput.getText().toString();
                String eventLocation = eventLocationInput.getText().toString();
                String startTime = btnStartTime.getText().toString();
                String endTime = btnEndTime.getText().toString();
                String startDate = btnStartDate.getText().toString();

                // Pack the data into an Intent
                Intent intent = new Intent(AddEventActivity.this, AddEventSecondActivity.class);
                intent.putExtra("eventName", eventName);
                intent.putExtra("eventLocation", eventLocation);
                intent.putExtra("startTime", startTime);
                intent.putExtra("endTime", endTime);
                intent.putExtra("startDate", startDate);
                intent.putExtra("endDate", endDate);

                // Start the EventDetailsActivity
                startActivity(intent);
            }
        });
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

        if (isStartDate) {
            btnStartDate.setText(dateString);
        } else {
            btnEndDate.setText(dateString);
        }
    }

    /**
     * Callback invoked when a time is picked in the TimePickerFragment.
     * It updates the corresponding start or end time based on the source of the callback and validates the time logic.
     *
     * @param tag A tag identifying which time picker (start or end) triggered this callback.
     * @param hourOfDay The hour of the day that was picked.
     * @param minute The minute within the hour that was picked.
     */
    public void onTimeSet(String tag, int hourOfDay, int minute) {
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
        // If button is startTime button
        if ("startTimePicker".equals(tag)) {
            startTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startTimeCalendar.set(Calendar.MINUTE, minute);
            btnStartTime.setText(formattedTime);
            // if button is endTime button
        } else if ("endTimePicker".equals(tag)) {
            endTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            endTimeCalendar.set(Calendar.MINUTE, minute);

            // Check that the end time is after the start time
            if (endTimeCalendar.after(startTimeCalendar)) {
                btnEndTime.setText(formattedTime);
            } else {
                Toast.makeText(this, "End Time must be after start time.", Toast.LENGTH_SHORT).show();
                btnEndTime.setText("ERR");
            }
        }
    }
}