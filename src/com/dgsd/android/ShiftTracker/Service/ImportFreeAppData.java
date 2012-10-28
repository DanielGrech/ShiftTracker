package com.dgsd.android.ShiftTracker.Service;

import android.app.IntentService;
import android.app.backup.BackupManager;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.dgsd.android.ShiftTracker.BuildConfig;
import com.dgsd.android.ShiftTracker.Data.Provider;
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.Util.ProviderUtils;

import java.util.ArrayList;

/**
 * @author Daniel Grech
 */
public class ImportFreeAppData extends IntentService {
	private static final String TAG = ImportFreeAppData.class.getSimpleName();

    public static final Uri FREE_APP_URI = Uri.parse("content://com.dgsd.android.ShiftTrackerFree.Data.Provider/shifts");

	public ImportFreeAppData() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent inIntent) {
        Cursor cursor = null;
		try {
            cursor = getContentResolver().query(FREE_APP_URI, null, null, null, null);
            if(cursor != null && cursor.moveToFirst()) {
                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>(cursor.getCount());
                do {
                    Shift shift = Shift.fromCursor(cursor);
                    shift.id = -1;
                    ops.add(ContentProviderOperation.newInsert(Provider.SHIFTS_URI)
                                                    .withYieldAllowed(true)
                                                    .withValues(shift.toContentValues())
                                                    .build());
                } while(cursor.moveToNext());

                if(!ops.isEmpty())
                    getContentResolver().applyBatch(Provider.getAuthority(), ops);
            }
		} catch (Exception e) {
			if(BuildConfig.DEBUG)
				Log.e(TAG, "Error importing app data", e);
		} finally {
            if(cursor != null && !cursor.isClosed())
                cursor.close();
        }
    }
}
