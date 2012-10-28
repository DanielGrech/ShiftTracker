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