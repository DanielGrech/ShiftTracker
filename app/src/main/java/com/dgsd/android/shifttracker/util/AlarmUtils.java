package com.dgsd.android.shifttracker.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.dgsd.android.shifttracker.receiver.ShowReminderReceiver;

import static android.app.PendingIntent.getBroadcast;

public class AlarmUtils {

    AlarmUtils() {
        // No instances..
    }

    public static void create(Context context, long shiftId, long millis) {
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = ShowReminderReceiver.newIntent(context, shiftId);
        am.set(AlarmManager.RTC_WAKEUP, millis,
                getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT));
    }

    public static void cancel(Context context, long shiftId) {
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = ShowReminderReceiver.newIntent(context, shiftId);
        am.cancel(getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT));
    }
}
