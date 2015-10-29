package com.dgsd.android.shifttracker.service;

import android.text.TextUtils;
import android.text.format.DateUtils;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.STApp;
import com.dgsd.android.shifttracker.activity.HomeActivity;
import com.dgsd.android.shifttracker.activity.ViewShiftActivity;
import com.dgsd.android.shifttracker.util.ModelUtils;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.data.STContentProvider;
import com.dgsd.shifttracker.model.Shift;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

import static android.text.format.DateUtils.*;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;

public class STDashClockExtension extends DashClockExtension {

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        Timber.d("onInitialize(%s)", isReconnect);
        if (!isReconnect) {
            addWatchContentUris(new String[]{STContentProvider.SHIFTS_URI.toString()});
        }
    }

    @Override
    protected void onUpdateData(int reason) {
        Timber.d("onUpdateData(%s)", reason);

        final DataProvider dataProvider = ((STApp) getApplication())
                .getAppServicesComponent()
                .dataProvider();

        try {
            final Shift nextShift = dataProvider.getShiftsBetween(0, Long.MAX_VALUE)
                    .flatMap(new Func1<List<Shift>, Observable<Shift>>() {
                        @Override
                        public Observable<Shift> call(List<Shift> shifts) {
                            return Observable.from(shifts);
                        }
                    })
                    .takeFirst(new Func1<Shift, Boolean>() {
                        @Override
                        public Boolean call(Shift shift) {
                            return shift.timePeriod().startMillis() > System.currentTimeMillis();
                        }
                    })
                    .toBlocking()
                    .firstOrDefault(null);

            if (nextShift != null) {
                String title = getString(R.string.dashclock_extension_title);
                if (!TextUtils.isEmpty(nextShift.title())) {
                    title += " - " + nextShift.title();
                }

                String dateText = formatDateTime(this, nextShift.timePeriod().startMillis(),
                        FORMAT_ABBREV_ALL | FORMAT_SHOW_DATE | FORMAT_SHOW_TIME);
                String body = dateText + " - "
                        + TimeUtils.formatDuration(nextShift.totalPaidDuration());
                final float pay = nextShift.totalPay();
                if (Float.compare(pay, 0) > 0) {
                    body += " - " + ModelUtils.formatCurrency(pay);
                }

                final ExtensionData data = new ExtensionData()
                        .icon(R.drawable.ic_dashclock_icon)
                        .visible(true)
                        .clickIntent(ViewShiftActivity.createIntentFromReminder(
                                getApplicationContext(), nextShift.id()))
                        .contentDescription(title + " is at " + dateText)
                        .expandedTitle(title)
                        .expandedBody(body)
                        .status(dateText);

                data.clean();
                publishUpdate(data);

                return;
            }
        } catch (Exception ex) {
            Timber.e(ex, "Error getting shifts for dashclock extension");
        }

        publishUpdate(new ExtensionData().visible(false));
    }
}
