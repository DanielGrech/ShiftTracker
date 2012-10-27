package com.dgsd.android.ShiftTracker.Service;

import android.app.IntentService;
import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.dgsd.android.ShiftTracker.BuildConfig;
import com.dgsd.android.ShiftTracker.Util.ProviderUtils;

/**
 * @author Daniel Grech
 */
public class DbService extends IntentService {
	private static final String TAG = DbService.class.getSimpleName();

	public static final String EXTRA_CONTENT_VALUES = "com.dgsd.android.ShiftTracker.extra._content_values";
	public static final String EXTRA_URI = "com.dgsd.android.ShiftTracker.extra._uri";
    public static final String EXTRA_SELECTION = "com.dgsd.android.ShiftTracker.extra._selection";

	public static final String EXTRA_REQUEST_TYPE = "com.dgsd.android.ShiftTracker.extra._request_type_id";

    private static OnDbEventListener mOnDbEventListener;

	public DbService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent inIntent) {
		final int type = inIntent.getIntExtra(EXTRA_REQUEST_TYPE, -1);

		try {
			switch(type) {
				case RequestType.DELETE: {
					final Uri uri = inIntent.getParcelableExtra(EXTRA_URI);
                    final String sel = inIntent.getStringExtra(EXTRA_SELECTION);
                    ProviderUtils.doDelete(this, uri, sel);

                    if(mOnDbEventListener != null)
                        mOnDbEventListener.onDelete();
					break;
				}

				case RequestType.UPDATE: {
					final Uri uri = inIntent.getParcelableExtra(EXTRA_URI);
					final ContentValues values = inIntent.getParcelableExtra(EXTRA_CONTENT_VALUES);
                    final String sel = inIntent.getStringExtra(EXTRA_SELECTION);
                    ProviderUtils.doUpdate(this, uri, values, sel, null);

                    if(mOnDbEventListener != null)
                        mOnDbEventListener.onUpdate();

					break;
				}

				case RequestType.INSERT: {
					final Uri uri = inIntent.getParcelableExtra(EXTRA_URI);
					final ContentValues values = inIntent.getParcelableExtra(EXTRA_CONTENT_VALUES);
                    ProviderUtils.doInsert(this, uri, values);

                    if(mOnDbEventListener != null)
                        mOnDbEventListener.onInsert();

					break;
				}
			}

            new BackupManager(this).dataChanged();
		} catch (Exception e) {
			if(BuildConfig.DEBUG)
				Log.e(TAG, "Error in DbService", e);
		}
	}

    public static void async_delete(Context c, Uri uri, String sel) {
        final Intent intent = new Intent(c, DbService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, RequestType.DELETE);
        intent.putExtra(EXTRA_URI, uri);
        intent.putExtra(EXTRA_SELECTION, sel);

        c.startService(intent);
    }

    public static void async_update(Context c, Uri uri, String sel, ContentValues values) {
        final Intent intent = new Intent(c, DbService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, RequestType.UPDATE);
        intent.putExtra(EXTRA_URI, uri);
        intent.putExtra(EXTRA_SELECTION, sel);
        intent.putExtra(EXTRA_CONTENT_VALUES, values);

        c.startService(intent);
    }

    public static void async_insert(Context c, Uri uri, ContentValues values) {
        final Intent intent = new Intent(c, DbService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, RequestType.INSERT);
        intent.putExtra(EXTRA_URI, uri);
        intent.putExtra(EXTRA_CONTENT_VALUES, values);

        c.startService(intent);
    }

    public static void setOnDbEventListener(OnDbEventListener listener) {
        mOnDbEventListener = listener;
    }

	public static final class RequestType {
		public static final int INSERT = 0x101;
		public static final int DELETE = 0x202;
		public static final int UPDATE = 0x303;
	}

    public static interface OnDbEventListener {
        public void onInsert();
        public void onDelete();
        public void onUpdate();
    }

}
