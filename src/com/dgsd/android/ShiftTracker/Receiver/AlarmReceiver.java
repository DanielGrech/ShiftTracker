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

package com.dgsd.android.ShiftTracker.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.dgsd.android.ShiftTracker.BuildConfig;
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.Service.AlarmScheduleService;
import com.dgsd.android.ShiftTracker.Service.NotificationService;

/**
 * Alarms upon boot
 * @author Daniel Grech
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();

    public static final String ACTION_UPDATE_FROM_DATABASE = "com.dgsd.android.ShiftTracker.Receiver.RemindersReceiver._update_from_db";
    public static final String ACTION_SHOW_ALARM = "com.dgsd.android.ShiftTracker.Receiver.RemindersReceiver._show_alarm";

    public static final String EXTRA_SHIFT = "com.dgsd.android.ShiftTracker.Receiver.AlarmReceiver._extra_shift";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action == null)
            action = ACTION_UPDATE_FROM_DATABASE;

        if(action.equals(ACTION_UPDATE_FROM_DATABASE) || action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            handleUpdateFromDatabase(context);
        } else if(action.equals(ACTION_SHOW_ALARM)) {
            final Shift rem = intent.getParcelableExtra(EXTRA_SHIFT);
            if(rem != null) {
                handleShowAlarm(context, rem);
            }
        }
    }

    private void handleShowAlarm(Context context, Shift shift) {
        if(BuildConfig.DEBUG)
            Log.i(TAG, "Alarm received for shift: " + shift.toString());

        NotificationService.requestShowAlarm(context, shift);
    }

    private void handleUpdateFromDatabase(Context context) {
        AlarmScheduleService.requestSchedule(context);
    }

}
