package com.dgsd.android.shifttracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dgsd.android.shifttracker.service.ReminderScheduleService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ReminderScheduleService.scheduleAll(context);
    }
}
