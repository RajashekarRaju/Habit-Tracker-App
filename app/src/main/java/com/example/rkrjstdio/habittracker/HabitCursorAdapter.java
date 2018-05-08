package com.example.rkrjstdio.habittracker;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rkrjstdio.habittracker.data.HabitContract;

/**
 * {@link HabitCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class HabitCursorAdapter extends CursorAdapter {

    public HabitCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView wakeUpTextView = view.findViewById(R.id.wake_up_time);
        TextView sleepTextView = view.findViewById(R.id.sleep_time);
        TextView dateTextView = view.findViewById(R.id.date);
        TextView caloriesTextView = view.findViewById(R.id.calories_burnt);
        TextView waterTextView = view.findViewById(R.id.water_consumption);
        TextView stepsCoveredTextView = view.findViewById(R.id.steps_covered);
        TextView assignmentTextView = view.findViewById(R.id.assignment_test);
        TextView alcoholTextView = view.findViewById(R.id.alcohol_consumption);
        ImageView itemImageView = view.findViewById(R.id.image_view);

        // Find the columns of habits attributes that we're interested in
        int wakeUpColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_WAKE_UP_TIME);
        int sleepColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_SLEEP_TIME);
        int dateColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_DATE);
        int caloriesBurntColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_CALORIES_BURNT);
        int waterColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_WATER_CONSUMED);
        int stepsCoveredColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_STEPS_COVERED);
        int assignmentColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_ASSIGNMENT);
        int alcoholColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_ALCOHOL_CONSUMPTION);
        int imageColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_IMAGE);

        // Read the habits attributes from the Cursor for the current habit
        String habitWakeUpTime = cursor.getString(wakeUpColumnIndex);
        String habitSleepTime = cursor.getString(sleepColumnIndex);
        String habitDate = cursor.getString(dateColumnIndex);
        String habitCaloriesBurnt = cursor.getString(caloriesBurntColumnIndex);
        String habitWater = cursor.getString(waterColumnIndex);
        String habitStepsCovered = cursor.getString(stepsCoveredColumnIndex);
        String habitImageView = cursor.getString(imageColumnIndex);

        // If the habit caloriesBurnt is empty string or null, then use some default text
        // that says "Unknown value", so the TextView isn't blank.
        if (TextUtils.isEmpty(habitCaloriesBurnt)) {
            habitCaloriesBurnt = context.getString(R.string.unknown_value);
        }

        if (TextUtils.isEmpty(habitStepsCovered)) {
            habitStepsCovered = context.getString(R.string.unknown_value);
        }

        /*
         * Get selected spinner value as TextView from tracker class to habits activity for selected
         * spinner value , following are the values from column index into habits information
         * ASSIGNMENT_COMPLETE sets value Completed
         * in default case value is Incomplete
         */
        int assignmentInformation = cursor.getInt(assignmentColumnIndex);
        String assignmentInformationString;
        switch (assignmentInformation) {
            case HabitContract.HabitEntry.ASSIGNMENT_COMPLETE:
                assignmentInformationString = context.getString(R.string.completed);
                break;
            default:
                assignmentInformationString = context.getString(R.string.incomplete);
        }

        /*
         * Get selected spinner value as TextView from tracker class to habits activity for selected
         * spinner value , following are the values from column index into habits information
         * ALCOHOL_CONSUMPTION sets value Alcohol Consumed
         * in default case value is NO Consumption
         */
        int alcoholInformation = cursor.getInt(alcoholColumnIndex);
        String alcoholInformationString;
        switch (alcoholInformation) {
            case HabitContract.HabitEntry.ALCOHOL_CONSUMPTION:
                alcoholInformationString = context.getString(R.string.consumed_array);
                break;
            default:
                alcoholInformationString = context.getString(R.string.noConsumption_array);
        }

        // Update the TextViews with the attributes for the current habit
        wakeUpTextView.setText(habitWakeUpTime);
        sleepTextView.setText(habitSleepTime);
        dateTextView.setText(habitDate);
        caloriesTextView.setText(habitCaloriesBurnt);
        waterTextView.setText(habitWater);
        stepsCoveredTextView.setText(habitStepsCovered);
        assignmentTextView.setText(assignmentInformationString);
        alcoholTextView.setText(alcoholInformationString);
        itemImageView.setImageURI(Uri.parse(habitImageView));
    }

}