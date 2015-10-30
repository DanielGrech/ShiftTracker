package com.dgsd.android.shifttracker.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.ShiftUtils;
import com.dgsd.android.shifttracker.activity.AddShiftActivity;
import com.dgsd.android.shifttracker.activity.HomeActivity;
import com.dgsd.android.shifttracker.activity.ViewShiftActivity;
import com.dgsd.android.shifttracker.util.ModelUtils;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.model.Shift;

import static android.app.PendingIntent.*;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.app.PendingIntent.getActivity;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.text.format.DateUtils.FORMAT_ABBREV_ALL;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;
import static android.text.format.DateUtils.formatDateTime;

public class ShiftListWidgetProvider extends AppWidgetProvider {

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context, ShiftListWidgetProvider.class);
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
                        = new RemoteViews(context.getPackageName(), R.layout.widget_shift_list);

                //Launch the 'EditShiftActivity' when clicking on the add button
                final Intent addIntent = AddShiftActivity.createIntent(context)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                final PendingIntent addPendingIntent
                        = getActivity(context, id, addIntent, FLAG_UPDATE_CURRENT);
                widget.setOnClickPendingIntent(R.id.add_button, addPendingIntent);

                // Launch app when the user taps on the header
                final Intent headerIntent = HomeActivity.createIntent(context)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                final PendingIntent launchAppPendingIntent
                        = getActivity(context, id, headerIntent, FLAG_UPDATE_CURRENT);
                widget.setOnClickPendingIntent(R.id.header, launchAppPendingIntent);

                //Set up listview adapter
                final Intent svcIntent = new Intent(context, ShiftListWidgetService.class);
                svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
                svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
                widget.setRemoteAdapter(R.id.list, svcIntent);
                appWidgetManager.notifyAppWidgetViewDataChanged(id, R.id.list);

                //Empty view for list
                widget.setEmptyView(R.id.list, android.R.id.empty);

                //Intent for each list item
                final Intent intentTemplate = new Intent(context, ViewShiftActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                final PendingIntent pendingIntentTemplate
                        = getActivity(context, id, intentTemplate, FLAG_UPDATE_CURRENT);
                widget.setPendingIntentTemplate(R.id.list, pendingIntentTemplate);

                appWidgetManager.updateAppWidget(id, widget);
            }
        }
    }
}
