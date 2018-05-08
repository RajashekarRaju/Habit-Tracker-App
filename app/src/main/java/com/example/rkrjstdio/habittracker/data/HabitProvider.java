package com.example.rkrjstdio.habittracker.data;


import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.rkrjstdio.habittracker.data.HabitContract.HabitEntry;

public class HabitProvider extends ContentProvider {

    // HabitDbHelper object to match a mDbHelper to a corresponding code.
    private HabitDbHelper mDbHelper;

    /** Tag for the log messages */
    public static final String LOG_TAG = HabitProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the habits table */
    private static final int HABITS = 100;

    /** URI matcher code for the content URI for a single habit in the habits table */
    private static final int HABITS_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(HabitContract.CONTENT_AUTHORITY , HabitContract.PATH_HABITS , HABITS);

        sUriMatcher.addURI(HabitContract.CONTENT_AUTHORITY , HabitContract.PATH_HABITS + "/#" , HABITS_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new HabitDbHelper(getContext());

        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @SuppressLint("NewApi")
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {

            case HABITS:
                // For the habits code, query the habits table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the habits table.

                cursor = database.query( HabitContract.HabitEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;

            case HABITS_ID:
                // For the HABIT_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.habits/habits/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = HabitEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the habits table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(HabitEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HABITS:
                return insertHabit(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a habit into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertHabit(Uri uri, ContentValues values) {

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new habit with the given values
        long id = database.insert(HabitEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Check the name if is not null
        String wakeUpTime = values.getAsString(HabitEntry.COLUMN_HABIT_WAKE_UP_TIME);
        if (wakeUpTime == null) {
            throw new IllegalArgumentException("Habit requires valid wakeup time");
        }

        // Check the image if is not null
        String image = values.getAsString(HabitEntry.COLUMN_HABIT_IMAGE);
        if (image == null) {
            throw new IllegalArgumentException("Habit requires valid image");
        }

        // Check that the sleep time is not null
        String sleepTime = values.getAsString(HabitEntry.COLUMN_HABIT_SLEEP_TIME);
        if (sleepTime == null) {
            throw new IllegalArgumentException("Habit requires valid sleep time");
        }

        // Check that the date is not null
        String date = values.getAsString(HabitEntry.COLUMN_HABIT_DATE);
        if (date == null) {
            throw new IllegalArgumentException("Habit requires valid date");
        }

        // Check that the assignment is valid
        Integer assignment = values.getAsInteger(HabitEntry.COLUMN_HABIT_ASSIGNMENT);
        if (assignment == null || !HabitEntry.isValidAssignment(assignment)) {
            throw new IllegalArgumentException("Habit requires valid assignment");
        }

        // Check that the alcohol Consumption is valid
        Integer alcoholConsumption = values.getAsInteger(HabitEntry.COLUMN_HABIT_ALCOHOL_CONSUMPTION);
        if (alcoholConsumption == null || !HabitEntry.isValidAlcoholConsumption(alcoholConsumption)) {
            throw new IllegalArgumentException("Habit requires valid alcohol consumption");
        }

        // Check that the steps Covered is valid
        Integer stepsCovered = values.getAsInteger(HabitEntry.COLUMN_HABIT_STEPS_COVERED);
        if (stepsCovered != null && stepsCovered < 0) {
            throw new IllegalArgumentException("Habit requires valid steps completed");
        }

        // Check that the calories Burnt is valid
        Integer caloriesBurnt = values.getAsInteger(HabitEntry.COLUMN_HABIT_CALORIES_BURNT);
        if (caloriesBurnt != null && caloriesBurnt < 0) {
            throw new IllegalArgumentException("Habit requires valid calories burnt");
        }

        // Check that the water Consumed is valid
        Integer waterConsumed = values.getAsInteger(HabitEntry.COLUMN_HABIT_WATER_CONSUMED);
        if (waterConsumed != null && waterConsumed < 0) {
            throw new IllegalArgumentException("Habit requires valid water consumed");
        }

        // Notify all listeners that the data has changed for the habit content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in th`e table,
        // return the new URI with the ID appended `to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HABITS:
                return updateHabit(uri, contentValues, selection, selectionArgs);
            case HABITS_ID:
                // For the HABIT_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = HabitEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateHabit(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update habits in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more habits).
     * Return the number of rows that were successfully updated.
     */
    private int updateHabit(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link HABITEntry#COLUMN_HABIT_NAME} key is present,
        // check that the wakeUpTime value is not null.
        if (values.containsKey(HabitEntry.COLUMN_HABIT_WAKE_UP_TIME)) {
            String wakeUpTime = values.getAsString(HabitEntry.COLUMN_HABIT_WAKE_UP_TIME);
            if (wakeUpTime == null) {
                throw new IllegalArgumentException("Habit requires wake up time");
            }
        }

        // If the {@link HABITEntry#COLUMN_HABIT_IMAGE} key is present,
        // check that the imageView value is not null.
        if (values.containsKey(HabitEntry.COLUMN_HABIT_IMAGE)) {
            String imageView = values.getAsString(HabitEntry.COLUMN_HABIT_IMAGE);
            if (imageView == null) {
                throw new IllegalArgumentException("Habit requires valid image");
            }
        }

        // If the {@link HabitEntry#COLUMN_HABIT_SLEEP_TIME} key is present,
        // check that the sleepTime value is not null.
        if (values.containsKey(HabitEntry.COLUMN_HABIT_SLEEP_TIME)) {
            String sleepTime = values.getAsString(HabitEntry.COLUMN_HABIT_SLEEP_TIME);
            if (sleepTime == null) {
                throw new IllegalArgumentException("Habit requires sleep time");
            }
        }

        // If the {@link HabitEntry#COLUMN_HABIT_DATE} key is present,
        // check that the date value is not null.
        if (values.containsKey(HabitEntry.COLUMN_HABIT_DATE)) {
            String date = values.getAsString(HabitEntry.COLUMN_HABIT_DATE);
            if (date == null) {
                throw new IllegalArgumentException("Habit requires date");
            }
        }

        // If the {@link HabitEntry#COLUMN_HABIT_ASSIGNMENT} key is present,
        // check that the assignment value is valid.
        if (values.containsKey(HabitEntry.COLUMN_HABIT_ASSIGNMENT)) {
            Integer assignment = values.getAsInteger(HabitEntry.COLUMN_HABIT_ASSIGNMENT);
            if (assignment == null || !HabitEntry.isValidAssignment(assignment)) {
                throw new IllegalArgumentException("Habit requires valid assignment");
            }
        }

        // If the {@link HabitEntry#COLUMN_HABIT_ALCOHOL_CONSUMPTION} key is present,
        // check that the alcohol value is valid.
        if (values.containsKey(HabitEntry.COLUMN_HABIT_ALCOHOL_CONSUMPTION)) {
            Integer alcohol = values.getAsInteger(HabitEntry.COLUMN_HABIT_ALCOHOL_CONSUMPTION);
            if (alcohol == null || !HabitEntry.isValidAlcoholConsumption(alcohol)) {
                throw new IllegalArgumentException("Habit requires valid assignment");
            }
        }

        // If the {@link HabitEntry#COLUMN_HABIT_CALORIES_BURNT} key is present,
        // check that the caloriesBurnt value is valid.
        if (values.containsKey(HabitEntry.COLUMN_HABIT_CALORIES_BURNT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer caloriesBurnt = values.getAsInteger(HabitEntry.COLUMN_HABIT_CALORIES_BURNT);
            if (caloriesBurnt != null && caloriesBurnt < 0) {
                throw new IllegalArgumentException("Habit requires valid calories burnt");
            }
        }

        // If the {@link HabitEntry#COLUMN_HABIT_STEPS_COVERED} key is present,
        // check that the stepsCovered value is valid.
        if (values.containsKey(HabitEntry.COLUMN_HABIT_STEPS_COVERED)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer stepsCovered = values.getAsInteger(HabitEntry.COLUMN_HABIT_STEPS_COVERED);
            if (stepsCovered != null && stepsCovered < 0) {
                throw new IllegalArgumentException("Habit requires valid steps completed");
            }
        }

        // If the {@link HabitEntry#COLUMN_HABIT_WATER_CONSUMED} key is present,
        // check that the waterConsumed value is valid.
        if (values.containsKey(HabitEntry.COLUMN_HABIT_WATER_CONSUMED)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer waterConsumed = values.getAsInteger(HabitEntry.COLUMN_HABIT_WATER_CONSUMED);
            if (waterConsumed != null && waterConsumed < 0) {
                throw new IllegalArgumentException("Habit requires valid water consumed");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(HabitEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of database rows affected by the update statement
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HABITS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(HabitEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case HABITS_ID:
                // Delete a single row given by the ID in the URI
                selection = HabitEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(HabitEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HABITS:
                return HabitEntry.CONTENT_LIST_TYPE;
            case HABITS_ID:
                return HabitEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
