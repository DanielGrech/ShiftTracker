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

package com.dgsd.android.ShiftTracker.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import com.dgsd.android.ShiftTracker.BuildConfig;
import com.dgsd.android.ShiftTracker.Data.DbField;
import com.dgsd.android.ShiftTracker.Data.Provider;
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.Receiver.AlarmReceiver;
import com.dgsd.android.ShiftTracker.Util.AlarmUtils;
import com.dgsd.android.ShiftTracker.Util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Grech
 */
public class AlarmScheduleService extends IntentService {
	public static final String TAG = AlarmScheduleService.class.getSimpleName();

	public AlarmScheduleService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(final Intent inIntent) {
		Cursor cursor = null;
		final AlarmUtils alarmManager = AlarmUtils.get(this);
		try {
            final String sel = DbField.REMINDER + ">=0 AND " + DbField.JULIAN_DAY + " >= " + TimeUtils.getCurrentJulianDay();
            cursor = getContentResolver().query(Provider.SHIFTS_URI, null, sel, null, null);
			if(cursor != null && cursor.moveToFirst()) {
				List<Shift> alarms = new ArrayList<Shift>(cursor.getCount());
                final long currentMillis = TimeUtils.getCurrentMillis();
				do {
                    final Shift shift = Shift.fromCursor(cursor);

                    if(shift.getStartTime() > currentMillis)
					    alarms.add(shift);
				} while(cursor.moveToNext());

				if(!alarms.isEmpty()) {
                    for(Shift shift : alarms) {
                        final Intent intent = AlarmUtils.newIntent(this, shift);
                        alarmManager.createAt(shift.getReminderTime(), intent);
                    }
				}
			}
		} catch(Exception e) {
			if(BuildConfig.DEBUG) {
				Log.e(TAG, "Error updating alarms for AlarmManager", e);
			}
		} finally {
			if(cursor != null && !cursor.isClosed()) {
                cursor.close();
			}
		}
	}

    public static void requestSchedule(Context context) {
        final Intent intent = new Intent(context, AlarmScheduleService.class);
        intent.setAction(AlarmReceiver.ACTION_UPDATE_FROM_DATABASE);
        context.startService(intent);
    }
}
