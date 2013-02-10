/*
 * Copyright 2013 Daniel Grech
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

