package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

        Button btnCancelAdd = findViewById(R.id.button_cancel);

        try {
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

            btnCancelAdd = findViewById(R.id.button_cancel);


            // Initialize starting time to current time + 1hr
            Calendar startTimeCalendar = Calendar.getInstance();
            startTimeCalendar.add(Calendar.HOUR_OF_DAY, 1);
            startTimeCalendar.set(Calendar.MINUTE, 0);
            String startTimeFormatted = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(startTimeCalendar.getTime());
            btnStartTime.setText(startTimeFormatted);

            // Initialize ending time to current time + 2hr
            Calendar endTimeCalendar = Calendar.getInstance();
            endTimeCalendar.add(Calendar.HOUR_OF_DAY, 2);
            endTimeCalendar.set(Calendar.MINUTE, 0);
            String endTimeFormatted = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(endTimeCalendar.getTime());
            btnEndTime.setText(endTimeFormatted);


        } catch (Exception e) {
            // Handle the exception or log it
            Log.e("ActivityAddEvent", "Error in onCreate", e);
            Toast.makeText(this, "An error occurred setting up the event details.", Toast.LENGTH_LONG).show();
        }

        btnCancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnStartTime.setOnClickListener(v -> {
            try {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "startTimePicker");
            } catch (Exception e) {
                Toast.makeText(AddEventActivity.this, "Error showing time picker", Toast.LENGTH_SHORT).show();
            }
        });

        btnEndTime.setOnClickListener(v -> {
            try {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "endTimePicker");
            } catch (Exception e) {
                Toast.makeText(AddEventActivity.this, "Error showing time picker", Toast.LENGTH_SHORT).show();
            }
        });

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartDate = true;
                try {
                    showDatePickerDialog();
                } catch (Exception e) {
                    Toast.makeText(AddEventActivity.this, "Error showing date picker", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnEndDate.setOnClickListener(v -> {
            if (btnEndDate.getVisibility() == View.VISIBLE) {
                isStartDate = false;
                try {
                    showDatePickerDialog();
                } catch (Exception e) {
                    Toast.makeText(AddEventActivity.this, "Error showing date picker", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton fabNextPage = findViewById(R.id.fab_next_page);
        fabNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    TextInputEditText eventNameInput = findViewById(R.id.text_input_event_name);
                    TextInputEditText eventLocationInput = findViewById(R.id.text_input_location);

                    String eventName = eventNameInput.getText().toString();
                    String eventLocation = eventLocationInput.getText().toString();
                    String startTime = btnStartTime.getText().toString();
                    String endTime = btnEndTime.getText().toString();
                    String startDate = btnStartDate.getText().toString();
                    String endDate = btnEndDate.getText().toString();

                    Intent intent = new Intent(AddEventActivity.this, AddEventSecondActivity.class);
                    intent.putExtra("eventName", eventName);
                    intent.putExtra("eventLocation", eventLocation);
                    intent.putExtra("startTime", startTime);
                    intent.putExtra("endTime", endTime);
                    intent.putExtra("startDate", startDate);
                    intent.putExtra("endDate", endDate);

                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(AddEventActivity.this, "Error starting activity", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Shows the date picker dialog to allow the user to select a date.
     */
    private void showDatePickerDialog() {
        try {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "datePicker");
        } catch (Exception e) {
            Toast.makeText(this, "Error showing date picker", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback method to receive the selected date from the date picker dialog.
     *
     * @param year  The selected year.
     * @param month The selected month.
     * @param day   The selected day.
     */
    public void onDateSet(int year, int month, int day) {
        try {
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
        } catch (Exception e) {
            Toast.makeText(this, "Error setting date", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback method to receive the selected time from the time picker dialog.
     *
     * @param tag       The tag to identify the time picker dialog.
     * @param hourOfDay The selected hour of the day.
     * @param minute    The selected minute.
     */
    public void onTimeSet(String tag, int hourOfDay, int minute) {
        try {
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);

            if ("startTimePicker".equals(tag)) {
                startTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startTimeCalendar.set(Calendar.MINUTE, minute);
                btnStartTime.setText(formattedTime);
            } else if ("endTimePicker".equals(tag)) {
                endTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endTimeCalendar.set(Calendar.MINUTE, minute);

                if (endTimeCalendar.after(startTimeCalendar)) {
                    btnEndTime.setText(formattedTime);
                } else {
                    Toast.makeText(this, "End Time must be after start time.", Toast.LENGTH_SHORT).show();
                    btnEndTime.setText("ERR");
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error setting time", Toast.LENGTH_SHORT).show();
        }
    }
}