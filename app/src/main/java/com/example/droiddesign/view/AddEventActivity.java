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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener {

    private Button btnEndDate, btnStartDate, btnStartTime, btnEndTime;
    private Boolean isStartDate;
    Calendar startTimeCalendar = Calendar.getInstance();
    Calendar endTimeCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        try {
            TextInputEditText eventNameInput = findViewById(R.id.text_input_event_name);
            TextInputEditText eventLocationInput = findViewById(R.id.text_input_location);
            btnStartDate = findViewById(R.id.button_start_date);
            TextView endDateText = findViewById(R.id.textView3);
            btnEndDate = findViewById(R.id.button_end_date);
            btnStartTime = findViewById(R.id.button_start_time);
            btnEndTime = findViewById(R.id.button_end_time);
            Button btnCancelAdd = findViewById(R.id.button_cancel);
            SwitchMaterial switchMultiDay = findViewById(R.id.switch_is_multi_day);
            FloatingActionButton fabNextPage = findViewById(R.id.fab_next_page);

            initializeDateAndTimePickers();
            setupSwitchMultiDay(switchMultiDay, endDateText);
            setupCancelButton(btnCancelAdd);
            setupFabNextPage(fabNextPage, eventNameInput, eventLocationInput);
        } catch (Exception e) {
            Toast.makeText(this, "Initialization error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeDateAndTimePickers() {
        Calendar currentDate = Calendar.getInstance();
        setupDatePickers(currentDate);
        setupTimePickers();
    }

    private void setupDatePickers(Calendar currentDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
        Calendar startDate = (Calendar) currentDate.clone();
        startDate.add(Calendar.DATE, 1);
        btnStartDate.setText(dateFormat.format(startDate.getTime()));

        Calendar endDate = (Calendar) currentDate.clone();
        endDate.add(Calendar.DATE, 2);
        btnEndDate.setText(dateFormat.format(endDate.getTime()));
        btnEndDate.setVisibility(View.GONE);

        btnStartDate.setOnClickListener(v -> showDatePickerDialog(true));
        btnEndDate.setOnClickListener(v -> showDatePickerDialog(false));
    }

    private void setupTimePickers() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        startTimeCalendar.add(Calendar.HOUR_OF_DAY, 1);
        btnStartTime.setText(timeFormat.format(startTimeCalendar.getTime()));

        endTimeCalendar.add(Calendar.HOUR_OF_DAY, 2);
        btnEndTime.setText(timeFormat.format(endTimeCalendar.getTime()));

        btnStartTime.setOnClickListener(v -> showTimePickerDialog("startTimePicker"));
        btnEndTime.setOnClickListener(v -> showTimePickerDialog("endTimePicker"));
    }

    private void setupSwitchMultiDay(SwitchMaterial switchMultiDay, TextView endDateText) {
        switchMultiDay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnEndDate.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            endDateText.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });
    }

    private void setupCancelButton(Button btnCancel) {
        btnCancel.setOnClickListener(v -> finish());
    }

    private void setupFabNextPage(FloatingActionButton fab, TextInputEditText eventNameInput, TextInputEditText eventLocationInput) {
        fab.setOnClickListener(view -> {
            try {
                String eventName = eventNameInput.getText().toString();
                String eventLocation = eventLocationInput.getText().toString();
                Intent intent = new Intent(AddEventActivity.this, AddEventSecondActivity.class);
                intent.putExtra("eventName", eventName);
                intent.putExtra("eventLocation", eventLocation);
                // Additional intent data...
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(AddEventActivity.this, "Error starting next activity: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDatePickerDialog(boolean isStart) {
        try {
            isStartDate = isStart;
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "datePicker");
        } catch (Exception e) {
            Toast.makeText(this, "Error showing date picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showTimePickerDialog(String tag) {
        try {
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), tag);
        } catch (Exception e) {
            Toast.makeText(this, "Error showing time picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());
            String dateString = dateFormat.format(calendar.getTime());

            if (isStartDate) {
                btnStartDate.setText(dateString);
            } else {
                btnEndDate.setText(dateString);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error setting date: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onTimeSet(String tag, int hourOfDay, int minute) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String timeString = timeFormat.format(calendar.getTime());

            if ("startTimePicker".equals(tag)) {
                btnStartTime.setText(timeString);
                startTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startTimeCalendar.set(Calendar.MINUTE, minute);
            } else if ("endTimePicker".equals(tag)) {
                endTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endTimeCalendar.set(Calendar.MINUTE, minute);
                if (endTimeCalendar.after(startTimeCalendar)) {
                    btnEndTime.setText(timeString);
                } else {
                    throw new IllegalArgumentException("End time must be after start time.");
                }
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error setting time: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
