package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
    private String eventName, eventLocation;
    private Time timeStart, timeEnd;
    private Button btnEndDate;
    private Button btnStartDate;
    private Button btnStartTime;
    private Button btnEndTime;
    private FloatingActionButton nextPage;
    private Boolean isStartDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        TextInputEditText eventNameInput = findViewById(R.id.text_input_event_name);
        TextInputEditText eventLocationInput = findViewById(R.id.text_input_location);
        btnStartDate = findViewById(R.id.button_start_date);
        btnEndDate = findViewById(R.id.button_end_date);
        btnStartTime = findViewById(R.id.button_start_time);
        btnEndTime = findViewById(R.id.button_end_time);
        Button btnCancelAdd = findViewById(R.id.button_cancel);
        SwitchMaterial switchMultiDay = findViewById(R.id.switch_is_multi_day);

        switchMultiDay.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            btnEndDate.setEnabled(isChecked);
            btnEndDate.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        }));

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
                String endDate = btnEndDate.getText().toString(); // Handle visibility & logic for single vs. multi-day events

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
        if (".StartTimePicker".equals(tag)) {
            btnStartTime.setText(formattedTime);
        } else {
            btnEndTime.setText(formattedTime);
        }
    }
}