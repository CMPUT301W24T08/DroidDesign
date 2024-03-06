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

public class AddEventActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener {
    private Button btnEndDate;
    private Button btnStartDate;
    private Button btnStartTime;
    private Button btnEndTime;
    private Boolean isStartDate;
    Calendar startTimeCalendar = Calendar.getInstance();
    Calendar endTimeCalendar = Calendar.getInstance();


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
            btnEndDate.setEnabled(isChecked);
            btnEndDate.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            endDateText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
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

    private void showDatePickerDialog() {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

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