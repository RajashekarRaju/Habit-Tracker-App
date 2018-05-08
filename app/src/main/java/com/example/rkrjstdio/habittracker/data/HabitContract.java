package com.example.rkrjstdio.habittracker.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class HabitContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private HabitContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.rkrjstdio.habittracker";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.habits/habits/ is a valid path for
     * looking at habit data. content://com.example.android.habits/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_HABITS = "habits";

    /**
     * Inner class that defines constant values for the habits database table.
     * Each entry in the table represents a single habit tracker.
     */
    public static final class HabitEntry implements BaseColumns {

        /**
         * The content URI to access the habit data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_HABITS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of habits.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HABITS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single habit.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HABITS;

        /**
         * Name of database table for habit
         */
        public final static String TABLE_NAME = "habits";

        /**
         * Unique ID number for the habits (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Category Wake Up Time of the habits.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_HABIT_WAKE_UP_TIME = "wake";

        /**
         * Category image of the habits.
         * <p>
         * Type: IMAGE
         */
        public final static String COLUMN_HABIT_IMAGE = "image";

        /**
         * Category Sleep Time of the habits.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_HABIT_SLEEP_TIME = "sleep";

        /**
         * Category date of the habits.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_HABIT_DATE = "date";

        /**
         * Category Assignment of the new habit.
         * <p>
         * The only possible values are {@link #ASSIGNMENT_COMPLETE},
         * or {@link #ASSIGNMENT_INCOMPLETE}.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_HABIT_ASSIGNMENT = "assignment";

        /**
         * Category Calories Burnt of the new habit.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_HABIT_CALORIES_BURNT = "calories";

        /**
         * Category steps covered in the habit.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_HABIT_STEPS_COVERED = "steps";

        /**
         * Category for water consumed for the habit.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_HABIT_WATER_CONSUMED = "water";

        /**
         * Category Alcohol Consumption for the hait
         * <p>
         * The only possible values are {@link #ALCOHOL_CONSUMPTION}
         * or {@link #ALCOHOL_NOCONSUMPTION}.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_HABIT_ALCOHOL_CONSUMPTION = "alcohol";

        /**
         * Possible values for the Alcohol Consumption of the habit tracker.
         */
        public static final int ALCOHOL_CONSUMPTION = 0;
        public static final int ALCOHOL_NOCONSUMPTION = 1;

        /**
         * Possible values for the Assignment of the habit tracker.
         */
        public static final int ASSIGNMENT_COMPLETE = 0;
        public static final int ASSIGNMENT_INCOMPLETE = 1;

        /**
         * Returns whether or not the given gender is {@link #ASSIGNMENT_COMPLETE},
         * or {@link #ASSIGNMENT_INCOMPLETE}.
         */
        static boolean isValidAssignment(int assignment) {
            return assignment == ASSIGNMENT_COMPLETE || assignment == ASSIGNMENT_INCOMPLETE;
        }

        /**
         * Returns whether or not the given gender is {@link #ALCOHOL_CONSUMPTION}
         * or {@link #ALCOHOL_NOCONSUMPTION}.
         */
        static boolean isValidAlcoholConsumption(int alcohol) {
            return alcohol == ALCOHOL_CONSUMPTION || alcohol == ALCOHOL_NOCONSUMPTION;
        }
    }

}
