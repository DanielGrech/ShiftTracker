package com.dgsd.android.shifttracker.service;

import android.text.TextUtils;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.ShiftUtils;
import com.dgsd.android.shifttracker.activity.ViewShiftActivity;
import com.dgsd.android.shifttracker.util.ModelUtils;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.shifttracker.data.STContentProvider;
import com.dgsd.shifttracker.model.Shift;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static android.text.format.DateUtils.FORMAT_ABBREV_ALL;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;
import static android.text.format.DateUtils.formatDateTime;

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

        try {
            final Shift nextShift = ShiftUtils.getNextShift(this)
                    .timeout(2, TimeUnit.SECONDS)
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
            } else {
                Timber.d("No next shift found");
            }
        } catch (Exception ex) {
            Timber.e(ex, "Error getting shifts for dashclock extension");
        }

        publishUpdate(new ExtensionData().visible(false));
    }
}
