package com.dgsd.android.ShiftTracker.Receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.RemoteViews;
import com.dgsd.android.ShiftTracker.Data.DbField;
import com.dgsd.android.ShiftTracker.Data.Provider;
import com.dgsd.android.ShiftTracker.EditShiftActivity;
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.R;
import com.dgsd.android.ShiftTracker.Util.Prefs;
import com.dgsd.android.ShiftTracker.Util.ProviderUtils;
import com.dgsd.android.ShiftTracker.Util.TimeUtils;
import com.dgsd.android.ShiftTracker.Util.UIUtils;

import java.text.NumberFormat;
import java.util.Formatter;
import java.util.Random;

public class NextShiftWidgetProvider extends AppWidgetProvider {

    private Formatter mFormatter;
    private StringBuilder mStringBuilder;
    private boolean mIs24Hour = false;
    private NumberFormat mCurrencyFormatter;

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context, NextShiftWidgetProvider.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mIs24Hour = DateFormat.is24HourFormat(context);
        mStringBuilder = new StringBuilder();
        mFormatter = new Formatter(mStringBuilder);
        mCurrencyFormatter = NumberFormat.getCurrencyInstance();

        final String a = intent.getAction();

        if(a.equals(context.getString(R.string.action_update_next_shift_widget)) ||
                a.equals(Intent.ACTION_DATE_CHANGED) ||
                a.equals(Intent.ACTION_TIME_CHANGED) ||
                a.equals(Intent.ACTION_TIMEZONE_CHANGED) ||
                a.equals(Intent.ACTION_LOCALE_CHANGED)) {
            final AppWidgetManager awm = AppWidgetManager.getInstance(context);
            int[] ids = awm.getAppWidgetIds(getComponentName(context));
            if(ids != null && ids.length > 0)
                performUpdate(context, awm, ids);
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        performUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void performUpdate(Context context, AppWidgetManager appWidgetManager, int[] ids) {
        final Shift shift = getNextShift(context);
        final boolean showIncomePref = Prefs.getInstance(context).get(context.getString(R.string.settings_key_show_income), true);
        for (int i = 0, len = ids.length; i < len; i++) {
            //Base widget layout
            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.next_shift_widget_layout);

            widget.setTextViewText(R.id.name, shift == null ? "No upcoming shifts" :  shift.name);
            widget.setTextViewText(R.id.time, shift == null ? "Tap to add a shift" : getTimeText(context, shift));

            String payText = shift == null ? null : getPayText(shift);
            if(showIncomePref && !TextUtils.isEmpty(payText)) {
                widget.setTextViewText(R.id.pay, payText);
                widget.setViewVisibility(R.id.pay, View.VISIBLE);
            } else {
                widget.setTextViewText(R.id.pay, null);
                widget.setViewVisibility(R.id.pay, View.GONE);
            }

            Intent openIntent = new Intent(context, EditShiftActivity.class);
            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if(shift == null)
                openIntent.putExtra(EditShiftActivity.EXTRA_JULIAN_DAY, TimeUtils.getCurrentJulianDay());
            else
                openIntent.putExtra(EditShiftActivity.EXTRA_SHIFT, shift);

            final PendingIntent openPendingIntent = PendingIntent.getActivity(context, ids[i], openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setOnClickPendingIntent(R.id.next_shift_container, openPendingIntent);

            appWidgetManager.updateAppWidget(ids[i], widget);
        }
    }

    private String getTimeText(Context context, Shift shift) {
        int flags = DateUtils.FORMAT_SHOW_TIME;
        if(mIs24Hour)
            flags |= DateUtils.FORMAT_24HOUR;

        mStringBuilder.setLength(0);
        String time = DateUtils.formatDateRange(context, mFormatter, shift.startTime, shift.endTime, flags).toString();
        time += " (" + UIUtils.getDurationAsHours(shift.getDurationInMinutes()) + " Hrs)";
        return time;
    }

    private String getPayText(Shift shift) {
        if(shift.payRate <= 0.01)
            return null;

        return mCurrencyFormatter.format(shift.getIncome());
    }

    private Shift getNextShift(Context context) {
        Cursor cursor = null;
        try {
            Uri uri = Provider.SHIFTS_URI.buildUpon().appendQueryParameter(Provider.QUERY_PARAMETER_LIMIT, "1").build();

            final int currentJd = TimeUtils.getCurrentJulianDay();

            final String sel = DbField.JULIAN_DAY + ">=?";
            final String[] args = {String.valueOf(currentJd)};

            cursor = ProviderUtils.doQuery(context, uri, null, sel, args, DbField.JULIAN_DAY + " ASC, " + DbField.START_TIME + " ASC");
            if(cursor != null && cursor.moveToFirst())
                return Shift.fromCursor(cursor);
            else
                return null;
        } catch (Exception e) {
            return null;
        } finally {
            if(cursor != null && !cursor.isClosed())
                cursor.close();
        }
    }

    private static String mUpdateAction = null;
    public static void triggerUpdate(Context context)  {
        if(TextUtils.isEmpty(mUpdateAction))
            mUpdateAction = context.getString(R.string.action_update_next_shift_widget);

        context.sendBroadcast(new Intent(mUpdateAction));
    }
}
