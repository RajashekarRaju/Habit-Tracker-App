package com.example.rkrjstdio.habittracker;


import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.rkrjstdio.habittracker.data.HabitContract;
import com.example.rkrjstdio.habittracker.data.HabitContract.HabitEntry;

public class TrackerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the habit data loader
     */
    private static final int EXISTING_HABIT_LOADER = 0;

    /**
     * Spinner field to select the option from array displayed
     */
    private Spinner mAssignmentSpinner;

    /**
     * EditText field to enter the habits's Wake Up Time for user
     */
    private EditText mWakeUpEditText;

    /**
     * EditText field to enter the habits's Sleep Time for user
     */
    private EditText mSleepTimeEditText;

    /**
     * EditText field to enter the habits's date for user
     */
    private EditText mDateEditText;

    /**
     * EditText field to enter the habits's calories burnt per day for user
     */
    private EditText mCaloriesEditText;

    /**
     * EditText field to enter the habits's steps completed per day by the user
     */
    private EditText mStepsCompletedEditText;

    /**
     * EditText field to enter the habits's water consumed by the user per day
     */
    private EditText mWaterConsumedEditText;

    /**
     * Spinner field to select the option from array displayed
     */
    private Spinner mAlcoholConsumptionSpinner;

    /**
     * Assignment completed options in habits's to be chosen by the user
     */
    private int mAssignment = HabitEntry.ASSIGNMENT_COMPLETE;

    /**
     * Alcohol Consumption options in habits's to be chosen by the user
     */
    private int mAlcohol = HabitEntry.ALCOHOL_NOCONSUMPTION;

    /**
     * Boolean flag that keeps track of whether the habit has been edited (true) or not (false)
     */
    private boolean mHabitHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mHabitHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mHabitHasChanged = true;
            return false;
        }
    };

    /**
     * Content URI for the existing habit (null if it's a new habit)
     */
    private Uri mCurrentHabitUri;

    private ImageView mImageView;

    private Uri mImageURI;

    private static final int RESULT_LOAD_IMAGE = 1;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new habit or editing an existing one.
        Intent intent = getIntent();
        mCurrentHabitUri = intent.getData();

        // If the intent DOES NOT contain a habit content URI, then we know that we are
        // creating a new habit.
        if (mCurrentHabitUri == null) {
            // This is a new habit, so change the app bar to say "Add a habit"
            setTitle(getString(R.string.editor_activity_title_new_habit));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a habit that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing habit, so change app bar to say "Edit a habit"
            setTitle(getString(R.string.editor_activity_title_edit_habit));

            // Initialize a loader to read the habit data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_HABIT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mWakeUpEditText = findViewById(R.id.time_picker_wake_up);
        mSleepTimeEditText = findViewById(R.id.time_picker_sleep_time);
        mDateEditText = findViewById(R.id.date_picker);
        mCaloriesEditText = findViewById(R.id.calories_burnt);
        mStepsCompletedEditText = findViewById(R.id.steps_covered);
        mWaterConsumedEditText = findViewById(R.id.water_consumed);
        mAssignmentSpinner = findViewById(R.id.spinner_assignment);
        mAlcoholConsumptionSpinner = findViewById(R.id.spinner_alcohol);
        mImageView = findViewById(R.id.Edit_image_view);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mWakeUpEditText.setOnTouchListener(mTouchListener);
        mSleepTimeEditText.setOnTouchListener(mTouchListener);
        mDateEditText.setOnTouchListener(mTouchListener);
        mCaloriesEditText.setOnTouchListener(mTouchListener);
        mStepsCompletedEditText.setOnTouchListener(mTouchListener);
        mWaterConsumedEditText.setOnTouchListener(mTouchListener);
        mAssignmentSpinner.setOnTouchListener(mTouchListener);
        mAlcoholConsumptionSpinner.setOnTouchListener(mTouchListener);
        mImageView.setOnTouchListener(mTouchListener);

        // setting up Spinner for Assignment options in habits.
        AssignmentSpinner();

        // setting up Spinner for Alcohol options in habits.
        AlcoholSpinner();

        // Method to make sure the keyboard only pops up when a user clicks into an EditText
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    // Method calling wakeup from view for support fragment manager
    public void wakeUp(View v) {

        // Dialog Fragment to pick time from the TimePicker
        DialogFragment newFragment = new TimePickerFragmentOne();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    // Method calling sleep from view for support fragment manager
    public void sleep(View view) {

        // Dialog Fragment to pick time from the TimePicker
        DialogFragment newFragment = new TimePickerFragmentTwo();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    // Method calling datePicker from view for support fragment manager
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_tracker, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new habit, hide the "Delete" menu item.
        if (mCurrentHabitUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Inserting habits into fields displaying data for user
                saveHabit();
                finish();

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the habit hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mHabitHasChanged) {
                    NavUtils.navigateUpFromSameTask(TrackerActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(TrackerActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the habit hasn't changed, continue with handling back button press
        if (!mHabitHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all habit attributes, define a projection that contains
        // all columns from the habit table
        String[] projection = {
                HabitEntry.COLUMN_HABIT_IMAGE,
                HabitEntry.COLUMN_HABIT_WAKE_UP_TIME,
                HabitEntry.COLUMN_HABIT_SLEEP_TIME,
                HabitEntry.COLUMN_HABIT_DATE,
                HabitEntry.COLUMN_HABIT_CALORIES_BURNT,
                HabitEntry.COLUMN_HABIT_STEPS_COVERED,
                HabitEntry.COLUMN_HABIT_ASSIGNMENT,
                HabitEntry.COLUMN_HABIT_ALCOHOL_CONSUMPTION,
                HabitEntry.COLUMN_HABIT_WATER_CONSUMED};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentHabitUri,         // Query the content URI for the current habit
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of habit attributes that we're interested in
            int wakeUpTimeColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_WAKE_UP_TIME);
            int sleepTimeColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_SLEEP_TIME);
            int dateColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_DATE);
            int caloriesBurntColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_CALORIES_BURNT);
            int stepsCompletedColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_STEPS_COVERED);
            int assignmentColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_ASSIGNMENT);
            int waterConsumedColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_WATER_CONSUMED);
            int alcoholConsumptionColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_ALCOHOL_CONSUMPTION);
            int imageViewColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_IMAGE);

            // Extract out the value from the Cursor for the given column index
            String wakeUp = cursor.getString(wakeUpTimeColumnIndex);
            String sleepTime = cursor.getString(sleepTimeColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            int caloriesBurnt = cursor.getInt(caloriesBurntColumnIndex);
            int stepsCompleted = cursor.getInt(stepsCompletedColumnIndex);
            int assignment = cursor.getInt(assignmentColumnIndex);
            int waterConsumed = cursor.getInt(waterConsumedColumnIndex);
            int alcoholConsumption = cursor.getInt(alcoholConsumptionColumnIndex);
            String imageView = cursor.getString(imageViewColumnIndex);

            // Update the views on the screen with the values from the database
            mWakeUpEditText.setText(wakeUp);
            mSleepTimeEditText.setText(sleepTime);
            mDateEditText.setText(date);
            mCaloriesEditText.setText(Integer.toString(caloriesBurnt));
            mStepsCompletedEditText.setText(Integer.toString(stepsCompleted));
            mWaterConsumedEditText.setText(Integer.toString(waterConsumed));
            mImageView.setImageURI(Uri.parse(imageView));
            mImageURI = Uri.parse(imageView);

            // Assignment is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is ASSIGNMENT_COMPLETE, 1 is ASSIGNMENT_INCOMPLETE).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (assignment) {
                case HabitEntry.ASSIGNMENT_COMPLETE:
                    mAssignmentSpinner.setSelection(0);
                    break;
                default:
                    mAssignmentSpinner.setSelection(1);
                    break;
            }

            // Alcohol Consumption is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is ALCOHOL_CONSUMPTION, 1 is ALCOHOL_NO_CONSUMPTION).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (alcoholConsumption) {
                case HabitEntry.ALCOHOL_CONSUMPTION:
                    mAlcoholConsumptionSpinner.setSelection(0);
                    break;
                default:
                    mAlcoholConsumptionSpinner.setSelection(1);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mWakeUpEditText.setText("");
        mSleepTimeEditText.setText("");
        mDateEditText.setText("");
        mCaloriesEditText.setText("");
        mStepsCompletedEditText.setText("");
        mWaterConsumedEditText.setText("");
        mAssignmentSpinner.setSelection(0); // Select "ASSIGNMENT_COMPLETE"
        mAlcoholConsumptionSpinner.setSelection(0);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the habit.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this habit.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the habit.
                deleteHabit();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the habit.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the habit in the database.
     */
    private void deleteHabit() {
        // Only perform the delete if this is an existing habit.
        if (mCurrentHabitUri != null) {
            // Call the ContentResolver to delete the habit at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentHabitUri
            // content URI already identifies the habit that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentHabitUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_habit_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_habit_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    // Inserting data into EditText fields and storing into fields
    private void saveHabit() {
        String wakeUpString = mWakeUpEditText.getText().toString().trim();
        String sleepTimeString = mSleepTimeEditText.getText().toString().trim();
        String dateString = mDateEditText.getText().toString().trim();
        String caloriesBurntString = mCaloriesEditText.getText().toString().trim();
        String stepsCoveredString = mStepsCompletedEditText.getText().toString().trim();
        String waterConsumedString = mWaterConsumedEditText.getText().toString().trim();

        // Check if this is supposed to be a new habit
        // and check if all the fields in the editor are blank
        if (mCurrentHabitUri == null &&
                TextUtils.isEmpty(wakeUpString) &&
                TextUtils.isEmpty(sleepTimeString) &&
                TextUtils.isEmpty(dateString) &&
                TextUtils.isEmpty(caloriesBurntString) &&
                TextUtils.isEmpty(stepsCoveredString) &&
                TextUtils.isEmpty(waterConsumedString) &&
                mAssignment == HabitEntry.ASSIGNMENT_COMPLETE &&
                mAlcohol == HabitEntry.ALCOHOL_NOCONSUMPTION) {
            // Since no fields were modified, we can return early without creating a new habit.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // storing data into fields and displaying to layout using values
        ContentValues values = new ContentValues();
        values.put(HabitEntry.COLUMN_HABIT_IMAGE, String.valueOf(mImageURI));
        values.put(HabitEntry.COLUMN_HABIT_WAKE_UP_TIME, wakeUpString);
        values.put(HabitEntry.COLUMN_HABIT_SLEEP_TIME, sleepTimeString);
        values.put(HabitEntry.COLUMN_HABIT_DATE, dateString);
        values.put(HabitEntry.COLUMN_HABIT_CALORIES_BURNT, caloriesBurntString + " cal");
        values.put(HabitEntry.COLUMN_HABIT_STEPS_COVERED, stepsCoveredString + " steps");
        values.put(HabitEntry.COLUMN_HABIT_WATER_CONSUMED, waterConsumedString + " Lt");
        values.put(HabitEntry.COLUMN_HABIT_ALCOHOL_CONSUMPTION, mAlcohol);
        values.put(HabitEntry.COLUMN_HABIT_ASSIGNMENT, mAssignment);

        // If the caloriesBurnt is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int caloriesBurnt = 0;
        if (!TextUtils.isEmpty(caloriesBurntString)) {
            caloriesBurnt = Integer.parseInt(caloriesBurntString);
        }
        values.put(HabitEntry.COLUMN_HABIT_CALORIES_BURNT, caloriesBurnt);

        // If the stepsCovered is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int stepsCovered = 0;
        if (!TextUtils.isEmpty(stepsCoveredString)) {
            stepsCovered = Integer.parseInt(stepsCoveredString);
        }
        values.put(HabitEntry.COLUMN_HABIT_STEPS_COVERED, stepsCovered);

        // If the waterConsumed is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int waterConsumed = 0;
        if (!TextUtils.isEmpty(waterConsumedString)) {
            waterConsumed = Integer.parseInt(waterConsumedString);
        }
        values.put(HabitEntry.COLUMN_HABIT_WATER_CONSUMED, waterConsumed);

        // Determine if this is a new or existing habit by checking if mCurrentHabitUri is null or not
        if (mCurrentHabitUri == null) {
            // This is a NEW habit, so insert a new habit into the provider,
            // returning the content URI for the new habit.
            Uri newUri = getContentResolver().insert(HabitEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_habit_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_habit_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING habit, so update the habit with content URI: mCurrentHabitUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentHabitUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentHabitUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_habit_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_habit_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Setup the dropdown spinner that allows the user to select the Assignment options in the habits
     */
    private void AssignmentSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_assignment_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mAssignmentSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mAssignmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.completed))) {
                        mAssignment = HabitEntry.ASSIGNMENT_COMPLETE;
                    } else if (selection.equals(getString(R.string.incomplete))) {
                        mAssignment = HabitContract.HabitEntry.ASSIGNMENT_INCOMPLETE;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mAssignment = HabitEntry.ASSIGNMENT_COMPLETE;
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the Alcohol options in the habits
     */
    private void AlcoholSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter alcoholSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_alcohol_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        alcoholSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mAlcoholConsumptionSpinner.setAdapter(alcoholSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mAlcoholConsumptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.consumed_array))) {
                        mAlcohol = HabitEntry.ALCOHOL_CONSUMPTION;
                    } else {
                        mAlcohol = HabitEntry.ALCOHOL_NOCONSUMPTION;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mAlcohol = HabitEntry.ALCOHOL_NOCONSUMPTION;
            }
        });
    }

    public void onClickImageChoose(View view) {
        tryToOpenImageSelector();
    }

    public void tryToOpenImageSelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();
    }

    private void openImageSelector() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            mImageView.setImageURI(selectedImage);
            mImageURI = Uri.parse(selectedImage.toString());
        }
    }

}
