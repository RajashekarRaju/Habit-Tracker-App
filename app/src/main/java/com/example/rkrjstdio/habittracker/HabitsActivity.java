package com.example.rkrjstdio.habittracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rkrjstdio.habittracker.data.HabitContract.HabitEntry;
import com.example.rkrjstdio.habittracker.data.HabitDbHelper;

public class HabitsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // private variable to for HabitDbHelper to initialize mDbHelper;
    private HabitDbHelper mDbHelper;

    private static final int Habit_LOADER = 0;

    HabitCursorAdapter mCursorAdapter;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_habits);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HabitsActivity.this, TrackerActivity.class);
                startActivity(intent);
            }
        });

        // Writing to database using HabitDbHelper class.
        mDbHelper = new HabitDbHelper(this);

        mCursorAdapter = new HabitCursorAdapter(this, null);
        // Find the ListView which will be populated with the habits data
        ListView habitListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);

        habitListView.setEmptyView(emptyView);

        habitListView.setAdapter(mCursorAdapter);

        habitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(HabitsActivity.this, TrackerActivity.class);
                Uri currentHabitUri = ContentUris.withAppendedId(HabitEntry.CONTENT_URI, id);
                intent.setData(currentHabitUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(Habit_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                showDeleteAllConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAllConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg_all);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the habit.
                deleteAllHabits();
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
     * Helper method to delete all habits in the database.
     */
    private void deleteAllHabits() {
        // Only perform the delete if this is an existing habit.
        if (HabitEntry.CONTENT_URI != null) {
            // Call the ContentResolver to delete the habit at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentHabitUri
            // content URI already identifies the habit that we want.
            int rowsDeleted = getContentResolver().delete(HabitEntry.CONTENT_URI, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.delete_all_habits),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.delete_all_habits_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                HabitEntry._ID,
                HabitEntry.COLUMN_HABIT_WAKE_UP_TIME,
                HabitEntry.COLUMN_HABIT_SLEEP_TIME,
                HabitEntry.COLUMN_HABIT_DATE,
                HabitEntry.COLUMN_HABIT_WATER_CONSUMED,
                HabitEntry.COLUMN_HABIT_CALORIES_BURNT,
                HabitEntry.COLUMN_HABIT_STEPS_COVERED,
                HabitEntry.COLUMN_HABIT_ASSIGNMENT,
                HabitEntry.COLUMN_HABIT_ALCOHOL_CONSUMPTION,
                HabitEntry.COLUMN_HABIT_IMAGE};

        return new CursorLoader(this, HabitEntry.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}
