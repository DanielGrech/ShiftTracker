package com.dgsd.android.ShiftTracker.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import com.dgsd.android.ShiftTracker.Const;

/**
 * @author Daniel Grech
 */
public class Prefs {
	private SharedPreferences mPrefs;

	private static Prefs mInstance;
	public static Prefs getInstance(Context context) {
		if(mInstance == null) {
			mInstance = new Prefs(context);
		}

		return mInstance;
	}

	private Prefs(Context context) {
		mPrefs = context.getSharedPreferences(Const.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
	}

	public void remove(String key) {
        applyOrCommit(mPrefs.edit().remove(key));
	}

	public String get(String key, String defVal) {
		return mPrefs.getString(key, defVal);
	}

	public boolean get(String key, boolean defVal) {
		return mPrefs.getBoolean(key, defVal);
	}

	public int get(String key, int defVal) {
		return mPrefs.getInt(key, defVal);
	}

	public long get(String key, long defVal) {
		return  mPrefs.getLong(key, defVal);
	}

	public float get(String key, float defVal) {
		return  mPrefs.getFloat(key, defVal);
	}

	public void set(String key, boolean val) {
        applyOrCommit(mPrefs.edit().putBoolean(key, val));
	}

	public void set(String key, int val) {
        applyOrCommit(mPrefs.edit().putInt(key, val));
	}

	public void set(String key, float val) {
        applyOrCommit(mPrefs.edit().putFloat(key, val));
	}

	public void set(String key, long val) {
        applyOrCommit(mPrefs.edit().putLong(key, val));
	}

	public void set(String key, String val) {
        applyOrCommit(mPrefs.edit().putString(key, val));
	}

    private void applyOrCommit(SharedPreferences.Editor editor) {
        if(Api.isMin(Api.HONEYCOMB)) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
}
