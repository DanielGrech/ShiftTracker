package com.dgsd.android.ShiftTracker.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Db extends SQLiteOpenHelper {
    private static final String TAG = Db.class.getSimpleName();

    private static final int VERSION = 1;
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
        db.execSQL(DbTable.SHIFTS.dropSql());
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
}

