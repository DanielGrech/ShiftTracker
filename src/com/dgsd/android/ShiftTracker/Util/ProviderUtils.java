package com.dgsd.android.ShiftTracker.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * @author Daniel Grech
 */
public class ProviderUtils {
	private static final String TAG = ProviderUtils.class.getSimpleName();

	public static Uri doInsert(Context c, Uri provider, ContentValues values) {
		return c.getContentResolver().insert(provider, values);
	}

	public static Cursor doQuery(Context c, Uri provider, String[] projection, String selection, String[] args,
								 String sortOrder) {
		return c.getContentResolver().query(provider, projection, selection, args, sortOrder);
	}

	public static boolean doUpdate(Context c, Uri provider, ContentValues values) {
		return doUpdate(c, provider, values, null, null);
	}

	public static boolean doUpdate(Context c, Uri provider, ContentValues values, String sel, String[] selArgs) {
		return c.getContentResolver().update(provider, values, sel, selArgs) > 0;
	}

	public static int doDelete(Context c, Uri provider, String where) {
		return c.getContentResolver().delete(provider, where, null);
	}

	public static ContentValues getSingleRecord(Context c, Uri provider, String[] fields, String selection,
												String[] args) {
		Cursor cursor = c.getContentResolver().query(provider, fields, selection, args, null);

		try {
			if (cursor == null || !cursor.moveToFirst()) {
				return null;
			} else {
				ContentValues values = new ContentValues();

				for (String field : fields) {
					values.put(field, cursor.getString(cursor.getColumnIndex(field)));
				}
				return values;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

}
