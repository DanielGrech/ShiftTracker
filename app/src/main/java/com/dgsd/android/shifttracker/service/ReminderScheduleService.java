package com.dgsd.android.shifttracker.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.dgsd.android.shifttracker.STApp;
import com.dgsd.android.shifttracker.util.AlarmUtils;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.model.Shift;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

import static java.lang.System.currentTimeMillis;

public class ReminderScheduleService extends IntentService {

    private static final String EXTRA_SHIFT_ID = "_shift_id";
    private static final String EXTRA_AT_MILLIS = "_at_millis";

    private static final String ACTION_SCHEDULE_ALL = "schedule_all";
    private static final String ACTION_SCHEDULE_SINGLE = "schedule_single";

    @Inject
    DataProvider dataProvider;

    public static void schedule(Context context, Shift shift) {
        schedule(context, shift.id(), shift.reminderTime());
    }

    public static void schedule(Context context, long shiftId, long atMillis) {
        context.startService(new Intent(context, ReminderScheduleService.class)
                .setAction(ACTION_SCHEDULE_SINGLE)
                .putExtra(EXTRA_SHIFT_ID, shiftId)
                .putExtra(EXTRA_AT_MILLIS, atMillis));
    }

    public static void scheduleAll(Context context) {
        context.startService(new Intent(context, ReminderScheduleService.class)
                .setAction(ACTION_SCHEDULE_ALL));
    }

    public ReminderScheduleService() {
        super(ReminderScheduleService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final STApp app = (STApp) getApplication();
        app.getAppServicesComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();
        try {
            if (ACTION_SCHEDULE_ALL.equals(action)) {
                scheduleAll();
            } else {
                final long shiftId = intent.getLongExtra(EXTRA_SHIFT_ID, -1);
                final long atMillis = intent.getLongExtra(EXTRA_AT_MILLIS, -1);

                scheduleAlarm(shiftId, atMillis);
            }
        } catch (Exception ex) {
            Timber.e(ex, "Error schedule alarm(s)");
        }
    }

    private void scheduleAll() {
        final List<Shift> shifts = dataProvider.getShiftsBetween(currentTimeMillis(), Long.MAX_VALUE)
                .flatMap(new Func1<List<Shift>, Observable<Shift>>() {
                    @Override
                    public Observable<Shift> call(List<Shift> shifts) {
                        return Observable.from(shifts);
                    }
                }).filter(new Func1<Shift, Boolean>() {
                    @Override
                    public Boolean call(Shift shift) {
                        return shift.hasReminder();
                    }
                }).filter(new Func1<Shift, Boolean>() {
                    @Override
                    public Boolean call(Shift shift) {
                        return shift.reminderTime() > currentTimeMillis();
                    }
                }).toList().toBlocking().firstOrDefault(Collections.<Shift>emptyList());

        if (shifts != null) {
            for (Shift shift : shifts) {
                scheduleAlarm(shift.id(), shift.reminderTime());
            }
        }
    }

    private void scheduleAlarm(long shiftId, long atMillis) {
        Timber.d("Scheduling alarm for %s at %s", shiftId, atMillis);
        AlarmUtils.create(this, shiftId, atMillis);
    }
}
