package com.dgsd.android.shifttracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dgsd.android.shifttracker.manager.AnalyticsManager;
import com.dgsd.android.shifttracker.service.ShowReminderService;

public class ShowReminderReceiver extends BroadcastReceiver {

    private static final String EXTRA_SHIFT_ID = "_shift_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        final long shiftId = intent.getLongExtra(EXTRA_SHIFT_ID, -1);
        if (shiftId >= 0) {
            AnalyticsManager.trackEvent("showing_reminder");
            ShowReminderService.showReminder(context, shiftId);
        }
    }

    public static Intent newIntent(Context context, long shiftId) {
        return new Intent(context, ShowReminderReceiver.class)
                .putExtra(EXTRA_SHIFT_ID, shiftId);
    }
}
