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
