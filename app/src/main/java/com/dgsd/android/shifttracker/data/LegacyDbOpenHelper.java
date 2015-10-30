package com.dgsd.android.shifttracker.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class LegacyDbOpenHelper extends SQLiteOpenHelper {

    private static final int VERSION = 2;

    public static final String DB_NAME = "shift_tracker.db";

    private static final String TABLE_NAME_SHIFTS = "shifts";

    private static LegacyDbOpenHelper instance;

    public static LegacyDbOpenHelper getInstance(Context c) {
        if (instance == null) {
            instance = new LegacyDbOpenHelper(c);
        }

        return instance;
    }

    protected LegacyDbOpenHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<LegacyShift> getShifts() {
        List<LegacyShift> shifts = new LinkedList<>();
        final Cursor cursor =  getReadableDatabase().query(
                TABLE_NAME_SHIFTS,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                shifts.add(new LegacyShift(cursor));
            } while (cursor.moveToNext());
        }

        return shifts;
    }

    public static boolean databaseExists(Context context) {
        return context.getDatabasePath(DB_NAME).exists();
    }

    public static void removeDatabase(Context context) {
        final File file = context.getDatabasePath(DB_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    public class LegacyShift {
        public final long id;
        public final String name;
        public final String note;
        public final long startTime;
        public final long endTime;
        public final int julianDay;
        public final int endJulianDay;
        public final float payRate;
        public final int breakDuration;
        public final boolean isTemplate;
        public final int reminder;

        LegacyShift(Cursor cursor) {
            int colCount = 0;
            id = cursor.getLong(colCount++);
            julianDay = cursor.getInt(colCount++);
            endJulianDay = cursor.getInt(colCount++);
            startTime = cursor.getLong(colCount++);
            endTime = cursor.getLong(colCount++);
            payRate = cursor.getFloat(colCount++);
            name = cursor.getString(colCount++);
            note = cursor.getString(colCount++);
            breakDuration = cursor.getInt(colCount++);
            isTemplate = cursor.getInt(colCount++) > 0;
            reminder = cursor.getInt(colCount++);
        }
    }
}
