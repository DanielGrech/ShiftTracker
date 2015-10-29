package com.dgsd.shifttracker.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

public class STContentProvider extends ContentProvider {

    private static final String AUTHORITY = BuildConfig.APPLICATION_ID;

    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private static final int TYPE_SHIFTS = 0x01;

    public static final Uri SHIFTS_URI;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        SHIFTS_URI = addUri(TYPE_SHIFTS);
    }

    private DbOpenHelper dbOpenHelper;

    private ContentResolver contentResolver;

    @Override
    public boolean onCreate() {
        dbOpenHelper = DbOpenHelper.getInstance(getContext().getApplicationContext());
        contentResolver = getContext().getContentResolver();
        return true;
    }

    @Override
    public String getType(final Uri uri) {
        return URI_MATCHER.match(uri) == UriMatcher.NO_MATCH ? null : uri.toString();
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String sel, String[] args, String sort) {
        try {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(getTableFromType(URI_MATCHER.match(uri)).name);

            Cursor cursor = qb.query(dbOpenHelper.getReadableDatabase(), projection,
                    sel, args, null, null, sort,
                    uri.getQueryParameter("limit"));

            if (cursor != null) {
                cursor.setNotificationUri(contentResolver, uri);
            }
            return cursor;
        } catch (Exception e) {
            // Blast!
        }

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        try {
            final int type = URI_MATCHER.match(uri);
            final long id = db.replaceOrThrow(getTableFromType(type).name, null, values);

            if (id >= 0) {
                Uri newUri = ContentUris.withAppendedId(uri, id);
                contentResolver.notifyChange(uri, null);
                return newUri;
            } else {
                throw new SQLException("Failed to insert row into " + uri);
            }

        } catch (Exception e) {
            // Blast!
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String sel, String[] selArgs) {
        try {
            final int type = URI_MATCHER.match(uri);
            final SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            int rowsAffected = db.delete(getTableFromType(type).name,
                    sel, selArgs);

            contentResolver.notifyChange(uri, null);
            return rowsAffected;
        } catch (Exception e) {
            // Blast!
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String sel,
                      String[] selArgs) {
        try {
            final int type = URI_MATCHER.match(uri);
            final SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            final int rowsAffected = db.update(getTableFromType(type).name,
                    values, sel, selArgs);

            contentResolver.notifyChange(uri, null);
            return rowsAffected;
        } catch (Exception e) {
            // Blast!
        }

        return 0;
    }

    private static DbContract.Table getTableFromType(int type) {
        switch (type) {
            case TYPE_SHIFTS:
                return DbContract.TABLE_SHIFTS;
            default:
                throw new IllegalArgumentException("Unrecognised uri type: " + type);
        }
    }

    private static Uri addUri(int type) {
        final String tableName = getTableFromType(type).name;
        URI_MATCHER.addURI(AUTHORITY, tableName, type);
        return Uri.withAppendedPath(BASE_URI, tableName);
    }
}
