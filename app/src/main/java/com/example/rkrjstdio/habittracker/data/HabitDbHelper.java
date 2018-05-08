package com.example.rkrjstdio.habittracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rkrjstdio.habittracker.data.HabitContract.HabitEntry;

public class HabitDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "habit.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link HabitDbHelper}.
     *
     * @param context of the app
     */
    public HabitDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the habits table
        String SQL_CREATE_HABITS_TABLE = "CREATE TABLE " + HabitContract.HabitEntry.TABLE_NAME + " ("
                + HabitEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + HabitEntry.COLUMN_HABIT_WAKE_UP_TIME + " TEXT NOT NULL, "
                + HabitEntry.COLUMN_HABIT_SLEEP_TIME + " TEXT NOT NULL, "
                + HabitEntry.COLUMN_HABIT_DATE + " TEXT NOT NULL, "
                + HabitEntry.COLUMN_HABIT_IMAGE + " TEXT, "
                + HabitEntry.COLUMN_HABIT_CALORIES_BURNT + " INTEGER NOT NULL DEFAULT 0, "
                + HabitEntry.COLUMN_HABIT_STEPS_COVERED + " INTEGER NOT NULL DEFAULT 0, "
                + HabitEntry.COLUMN_HABIT_WATER_CONSUMED + " INTEGER NOT NULL DEFAULT 0, "
                + HabitEntry.COLUMN_HABIT_ALCOHOL_CONSUMPTION + " TEXT NOT NULL, "
                + HabitEntry.COLUMN_HABIT_ASSIGNMENT + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_HABITS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

}
