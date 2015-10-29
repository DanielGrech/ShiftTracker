package com.dgsd.android.shifttracker.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.ShiftUtils;
import com.dgsd.android.shifttracker.activity.ViewShiftActivity;
import com.dgsd.android.shifttracker.util.ModelUtils;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.model.Shift;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.app.PendingIntent.getActivity;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.text.format.DateUtils.FORMAT_ABBREV_ALL;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;
import static android.text.format.DateUtils.formatDateTime;

public class NextShiftWidgetProvider extends AppWidgetProvider {

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context, NextShiftWidgetProvider.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(DataProvider.UPDATE_ACTION) ||
                action.equals(Intent.ACTION_DATE_CHANGED) ||
                action.equals(Intent.ACTION_TIME_CHANGED) ||
                action.equals(Intent.ACTION_TIMEZONE_CHANGED) ||
                action.equals(Intent.ACTION_LOCALE_CHANGED)) {
            final AppWidgetManager awm = AppWidgetManager.getInstance(context);
            int[] ids = awm.getAppWidgetIds(getComponentName(context));
            if (ids != null && ids.length > 0) {
                performUpdate(context, awm, ids);
            }
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        performUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void performUpdate(Context context, AppWidgetManager appWidgetManager, int[] ids) {
        final Shift nextShift = ShiftUtils.getNextShift(context);
        if (nextShift != null) {
            for (int id : ids) {
                final RemoteViews widget
                        = new RemoteViews(context.getPackageName(), R.layout.widget_next_shift);

                String title = context.getString(R.string.dashclock_extension_title);
                if (!TextUtils.isEmpty(nextShift.title())) {
                    title += " - " + nextShift.title();
                }

                final String dateText = formatDateTime(context, nextShift.timePeriod().startMillis(),
                        FORMAT_ABBREV_ALL | FORMAT_SHOW_DATE | FORMAT_SHOW_TIME);
                String body = dateText + " - "
                        + TimeUtils.formatDuration(nextShift.totalPaidDuration());
                final float totalPay = nextShift.totalPay();
                if (Float.compare(totalPay, 0) > 0) {
                    body += " - " + ModelUtils.formatCurrency(totalPay);
                }

                widget.setTextViewText(R.id.title, title);
                widget.setTextViewText(R.id.summary, body);

                String payText = Float.compare(totalPay, 0) > 0 ?
                        ModelUtils.formatCurrency(totalPay) : null;
                if (TextUtils.isEmpty(payText)) {
                    widget.setTextViewText(R.id.pay, null);
                    widget.setViewVisibility(R.id.pay, View.GONE);
                } else {
                    widget.setTextViewText(R.id.pay, payText);
                    widget.setViewVisibility(R.id.pay, View.VISIBLE);
                }

                final Intent intent = ViewShiftActivity.createIntentFromReminder(context,
                        nextShift.id()).setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);

                widget.setOnClickPendingIntent(R.id.next_shift_container,
                        getActivity(context, id, intent, FLAG_UPDATE_CURRENT));

                appWidgetManager.updateAppWidget(id, widget);
            }
        }
    }
}
