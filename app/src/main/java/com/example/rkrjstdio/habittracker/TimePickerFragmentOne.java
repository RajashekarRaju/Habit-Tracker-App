package com.example.rkrjstdio.habittracker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragmentOne extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    // EditText for user to pick time from layout.
    EditText timePickerWakeUp;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @SuppressLint("DefaultLocale")
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        //Get reference of host activity (XML Layout File) TextView widget
        timePickerWakeUp = getActivity().findViewById(R.id.time_picker_wake_up);

        // Display the user changed time on TextView
        // Set the time for 12 hour format
        boolean isPM = (hourOfDay >= 12);
        timePickerWakeUp.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 :
                hourOfDay % 12, minute, isPM ? "PM" : "AM"));
    }
}
