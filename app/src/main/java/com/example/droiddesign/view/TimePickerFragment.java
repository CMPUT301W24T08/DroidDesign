package com.example.droiddesign.view;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
                    implements TimePickerDialog.OnTimeSetListener {
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        ((AddEventActivity) getActivity()).onTimeSet(getTag(), hourOfDay, minute);
    }
}
