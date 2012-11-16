package com.dgsd.android.ShiftTracker.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Db extends SQLiteOpenHelper {
    private static final String TAG = Db.class.getSimpleName();

    private static final int VERSION = 2;
    public static final String DB_NAME ="shift_tracker.db";

    private static Db mInstance;

    public static final Object[] LOCK = new Object[0];

    public static Db getInstance(Context c) {
        if(mInstance == null)
            mInstance = new Db(c);

        return mInstance;
    }

    protected Db(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbTable.SHIFTS.createSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion == 2)
            handleVersion2Changes(db);
        else {
            db.execSQL(DbTable.SHIFTS.dropSql());
            onCreate(db);
        }
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        synchronized (LOCK) {
            return super.getReadableDatabase();
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        synchronized (LOCK) {
            return super.getWritableDatabase();
        }
    }

    private void handleVersion2Changes(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + DbTable.SHIFTS.name + " ADD COLUMN " + DbField.END_JULIAN_DAY.name + " " + DbField.END_JULIAN_DAY.type);
    }
}

