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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.dgsd.android.ShiftTracker.BuildConfig;

import java.sql.SQLException;


public class Provider extends ContentProvider {
    private static final String TAG = Provider.class.getSimpleName();

    public static final String QUERY_PARAMETER_LIMIT = "limit";
    public static final String QUERY_PARAMETER_DISTINCT = "distinct";

    private static String AUTHORITY = "com.dgsd.android.ShiftTracker.Data.Provider";
    private static Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private static UriMatcher mURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int SHIFTS = 0x1;
    public static final int SHIFTS_WITH_DAYS = 0x2;

    public static Uri SHIFTS_URI = Uri.withAppendedPath(BASE_URI, "shifts");
    public static Uri DAYS_WITH_SHIFTS = Uri.withAppendedPath(BASE_URI, "days_with_shifts");

    private Db mDb;

    static {
        mURIMatcher.addURI(AUTHORITY, "shifts", SHIFTS);
    }

    public static String getAuthority() {
        return AUTHORITY;
    }

    public static void setAuthority(String authority) {
        AUTHORITY = authority;
        BASE_URI = Uri.parse("content://" + AUTHORITY);
        SHIFTS_URI = Uri.withAppendedPath(BASE_URI, "shifts");
        DAYS_WITH_SHIFTS = Uri.withAppendedPath(BASE_URI, "days_with_shifts");

        mURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mURIMatcher.addURI(AUTHORITY, "shifts", SHIFTS);
        mURIMatcher.addURI(AUTHORITY, "days_with_shifts", SHIFTS_WITH_DAYS);
    }

    @Override
    public boolean onCreate() {
        mDb = Db.getInstance(getContext());
        return true;
    }

    @Override
    public String getType(final Uri uri) {
        if (mURIMatcher.match(uri) != UriMatcher.NO_MATCH) {
            return uri.toString();
        } else {
            return null;
        }
    }

    @Override
    public Cursor query(final Uri uri, String[] proj, final String sel, final String[] selArgs, final String sort) {
        final int type = mURIMatcher.match(uri);
        if (type == UriMatcher.NO_MATCH) {
            if (BuildConfig.DEBUG)
                Log.w(TAG, "No match for URI: " + uri);

            return null;
        }

        try {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

            Cursor cursor = null;
            switch (type) {
                case SHIFTS:
                    qb.setTables(DbTable.SHIFTS.name);

                    if (!TextUtils.isEmpty(uri.getQueryParameter(QUERY_PARAMETER_DISTINCT)))
                        qb.setDistinct(true);

                    cursor = qb.query(mDb.getReadableDatabase(),
                            proj,
                            sel,
                            selArgs,
                            null,
                            null,
                            sort,
                            uri.getQueryParameter(QUERY_PARAMETER_LIMIT));

                    break;
                case SHIFTS_WITH_DAYS:
                    qb.setTables(DbTable.SHIFTS.name);

                    cursor = qb.query(mDb.getReadableDatabase(),
                            new String[]{
                                DbField.JULIAN_DAY.name
                            },
                            null,
                            null,
                            DbField.JULIAN_DAY.name,
                            sel,
                            sort,
                            null);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }

            cursor.setNotificationUri(getContext().getContentResolver(), uri);

            return cursor;
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "Error querying data", e);

            return null;
        }
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        final int type = mURIMatcher.match(uri);
        try {
            final SQLiteDatabase db = mDb.getWritableDatabase();

            String table = null;
            switch (type) {
                case SHIFTS:
                    table = DbTable.SHIFTS.name;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }

            long id = db.replaceOrThrow(table, null, values);
            if (id > 0) {
                Uri newUri = ContentUris.withAppendedId(uri, id);

                getContext().getContentResolver().notifyChange(uri, null);
                getContext().getContentResolver().notifyChange(DAYS_WITH_SHIFTS, null);
                return newUri;
            } else {
                throw new SQLException("Failed to insert row into " + uri);
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "Error inserting data", e);

            return null;
        }
    }

    @Override
    public int delete(final Uri uri, final String sel, final String[] selArgs) {
        final int type = mURIMatcher.match(uri);

        try {
            String table = null;
            switch (type) {
                case SHIFTS:
                    table = DbTable.SHIFTS.name;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }

            int rowsAffected = mDb.getWritableDatabase().delete(table, sel, selArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(DAYS_WITH_SHIFTS, null);
            return rowsAffected;
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Error deleting data", e);
            }
            return 0;
        }
    }

    @Override
    public int update(final Uri uri, final ContentValues values, final String sel, final String[] selArgs) {
        final int type = mURIMatcher.match(uri);

        try {
            String id = null;
            String table = null;
            switch (type) {
                case SHIFTS:
                    table = DbTable.SHIFTS.name;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }

            final SQLiteDatabase db = mDb.getWritableDatabase();

            final int rowsAffected = db.update(table, values, sel, selArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(DAYS_WITH_SHIFTS, null);
            return rowsAffected;
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Error deleting data", e);
            }
            return 0;
        }
    }
}