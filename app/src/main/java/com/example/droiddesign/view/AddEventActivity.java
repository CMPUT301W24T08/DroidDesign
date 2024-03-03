package com.example.droiddesign.view;

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
    String eventName;
    Date eventDate;
    Time timeStart, timeEnd;
    TextInputEditText eventNameInput;
    Button btnStartTime, btnEndTime, btnEndDate, btnStartDate, btnCancelAdd;
    FloatingActionButton nextPage;
    SwitchMaterial switchMultiDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventNameInput = findViewById(R.id.text_input_event_name);
        btnStartDate = findViewById(R.id.button_start_date);
        btnEndDate = findViewById(R.id.button_end_date);
        btnStartTime = findViewById(R.id.button_start_time);
        btnEndTime = findViewById(R.id.button_end_time);
        switchMultiDay = findViewById(R.id.switch_is_multi_day);

        switchMultiDay.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            btnEndDate.setEnabled(isChecked);
            if(isChecked) {
                btnEndDate.setVisibility(View.VISIBLE);
            } else {
                btnEndDate.setVisibility(View.GONE);
            }
        }));

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(true);
            }
        });

        btnEndDate.setOnClickListener(v -> {
            if (btnEndDate.getVisibility() == View.VISIBLE) {
                showDatePickerDialog(false);
            }
        });
    }

    private void showDatePickerDialog(boolean isStartDate) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    public void onDateSet(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        eventDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        String dateString = dateFormat.format(eventDate);

        btnStartDate.setText(dateString);
    }
}