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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.Receiver.AlarmReceiver;

/**
 * @author Daniel Grech
 */
public class AlarmUtils {
	private static final String TAG = AlarmUtils.class.getSimpleName();

	public static final String CONTENT_AUTHORITY = "com.dgsd.android.ShiftTracker.AlarmUtils._authority";

	private AlarmManager mAlarmManager;
	private Context mContext;

    private static AlarmUtils mInstance;

    public static AlarmUtils get(Context context) {
        if(mInstance == null)
            mInstance = new AlarmUtils(context);

        return mInstance;
    }

    public static Intent newIntent(Context context, Shift shift) {
        String id = shift == null ? "" : (shift.id + "." + shift.getStartTime());
        Intent intent = new Intent();
        intent.setClass(context, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.ACTION_SHOW_ALARM);
        intent.putExtra(AlarmReceiver.EXTRA_SHIFT, shift);
        intent.setData(new Uri.Builder().authority(CONTENT_AUTHORITY).path(id).build());

        return intent;
    }

	private AlarmUtils(Context context) {
		mContext = context;
		mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}

	public void createAt(long millis, Intent intent) {
		PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		mAlarmManager.set(AlarmManager.RTC_WAKEUP, millis, pi);
	}

    public void cancel(Shift shift) {
        mAlarmManager.cancel(PendingIntent.getBroadcast(mContext, 0, newIntent(mContext, shift), PendingIntent.FLAG_CANCEL_CURRENT));
    }
}
