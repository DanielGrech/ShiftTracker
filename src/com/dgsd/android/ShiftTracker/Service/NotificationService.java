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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import com.dgsd.android.ShiftTracker.BuildConfig;
import com.dgsd.android.ShiftTracker.EditShiftActivity;
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.R;
import com.dgsd.android.ShiftTracker.Util.TimeUtils;

import java.util.*;

/**
 * @author Daniel Grech
 */
public class NotificationService extends IntentService {
	public static final String TAG = NotificationService.class.getSimpleName();

    public static final String ACTION_SHOW_ALARM = "com.dgsd.android.ShiftTracker.Service.NotificationService._action_show_shift_alarm";
    public static final String EXTRA_SHIFT = "com.dgsd.android.ShiftTracker.Service.NotificationService._extra_shift";

    private static Map<String, Set<Integer>> mNotificationTagToIds = new HashMap<String, Set<Integer>>();

	public NotificationService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(final Intent inIntent) {
        final String action = inIntent.getAction();
        if(TextUtils.isEmpty(action)) {
            return;
        }

        if(TextUtils.equals(action, ACTION_SHOW_ALARM)) {
            final Shift shift = inIntent.getParcelableExtra(EXTRA_SHIFT);
            if(shift != null && shift.id != -1) {
                showAlarmNotification(shift);
            }
        }
	}

    private void showAlarmNotification(Shift shift) {
        if(shift == null)
            return;

        try {
            NotificationCompat.Builder b = new NotificationCompat.Builder(this);
            b.setAutoCancel(true);
            b.setPriority(NotificationCompat.PRIORITY_HIGH);
            b.setDefaults(Notification.DEFAULT_ALL);
            b.setTicker("Reminder: " + shift.name);
            b.setSmallIcon(R.drawable.stat_notify_calendar);
            b.setContentTitle(shift.name);
            b.setContentText(getContentText(shift));
            b.setWhen(shift.getStartTime());

            final Intent contentIntent = new Intent(this, EditShiftActivity.class);
            contentIntent.putExtra(EditShiftActivity.EXTRA_SHIFT, shift);
            contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            b.setContentIntent(PendingIntent.getActivity(this, shift.hashCode(), contentIntent, PendingIntent.FLAG_UPDATE_CURRENT));

            notify(Tag.REMINDER, (int) shift.id, b.build());
        } catch(Exception e) {
            if(BuildConfig.DEBUG) {
                Log.e(TAG, "Error creating event reminder notification", e);
            }
        }

    }

    private CharSequence getContentText(Shift shift) {
        return "Tap to view details";
    }

    /**
     * Shows a notification in the status bar. Also keeps a reference to the shown notifications so that we can
     * clear them on demand
     * @param tag
     * @param id
     * @param notification
     */
    private void notify(String tag, int id, Notification notification) {
        //Save the ids of the any notifications we show so that we can clear them on demand
        Set<Integer> ids = mNotificationTagToIds.get(tag);
        if(ids == null) {
            ids = new HashSet<Integer>();
        }

        ids.add(id);
        mNotificationTagToIds.put(tag, ids);


        final NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mgr.notify(tag, id, notification);
    }

    public static void requestShowAlarm(Context context, Shift shift) {
        final Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_SHOW_ALARM);
        intent.putExtra(EXTRA_SHIFT, shift);
        context.startService(intent);
    }

    /**
     *  Tags for different kinds of notifications
     */
    public static final class Tag {
        public static final String REMINDER = "com.skedgo.android.planner.Service.NotificationService._reminder_notification_tag";
    }
}
