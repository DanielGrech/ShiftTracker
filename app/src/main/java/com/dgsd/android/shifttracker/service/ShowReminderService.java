package com.dgsd.android.shifttracker.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.STApp;
import com.dgsd.android.shifttracker.activity.ViewShiftActivity;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.model.Shift;

import javax.inject.Inject;

import timber.log.Timber;

public class ShowReminderService extends IntentService {

    private static final String EXTRA_SHIFT_ID = "_shift_id";
    private static final String NOTIFICATION_TAG_REMINDER = "_notification_reminder";

    @Inject
    DataProvider dataProvider;

    public static void showReminder(Context context, long shiftId) {
        context.startService(new Intent(context, ShowReminderService.class)
                .putExtra(EXTRA_SHIFT_ID, shiftId));
    }

    public ShowReminderService() {
        super(ShowReminderService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final STApp app = (STApp) getApplication();
        app.getAppServicesComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final long shiftId = intent.getLongExtra(EXTRA_SHIFT_ID, -1);
        final Shift shift = getShift(shiftId);
        if (shift == null) {
            Timber.d("Couldn't find shift for reminder. id = %s", shiftId);
        } else {
            Timber.d("Showing notification for shift: %s", shiftId);
            showNotification(shift);
        }
    }

    private void showNotification(@NonNull Shift shift) {
        final String title;
        if (TextUtils.isEmpty(shift.title())) {
            title = getString(R.string.reminder_notification_title);
        } else {
            title = getString(R.string.reminder_notification_title_template, shift.title());
        }

        final String startTimeText;
        final long currentTime = System.currentTimeMillis();
        if ((shift.timePeriod().startMillis() - currentTime) < DateUtils.MINUTE_IN_MILLIS) {
            startTimeText = getString(R.string.now);
        } else {
            startTimeText = DateUtils.getRelativeTimeSpanString(
                    shift.timePeriod().startMillis(),
                    currentTime,
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL
            ).toString().toLowerCase();
        }

        final Notification notification = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_time)
                .setWhen(shift.timePeriod().startMillis())
                .setShowWhen(true)
                .setTicker(title)
                .setContentTitle(title)
                .setContentText(
                        getString(R.string.reminder_notification_content_template, startTimeText))
                .setColor(shift.color() == -1 ?
                        getResources().getColor(R.color.primary) : shift.color())
                .setContentIntent(createViewShiftIntent(shift))
                .build();


        NotificationManagerCompat.from(this)
                .notify(NOTIFICATION_TAG_REMINDER, (int) shift.id(), notification);
    }

    private PendingIntent createViewShiftIntent(Shift shift) {
        final Intent intent = ViewShiftActivity.createIntentFromReminder(this, shift.id())
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(this, (int) shift.id(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Shift getShift(long shiftId) {
        try {
            return dataProvider.getShift(shiftId)
                    .toBlocking()
                    .firstOrDefault(null);
        } catch (Exception ex) {
            Timber.e(ex, "Error loading shift: %s", shiftId);
            return null;
        }
    }
}
