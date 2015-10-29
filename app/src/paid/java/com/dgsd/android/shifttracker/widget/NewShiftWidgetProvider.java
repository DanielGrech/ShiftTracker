package com.dgsd.android.shifttracker.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.activity.AddShiftActivity;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.app.PendingIntent.getActivity;

public class NewShiftWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] ids) {
        for (int id : ids) {
            final RemoteViews widget =
                    new RemoteViews(context.getPackageName(), R.layout.widget_new_shift);

            final Intent intent = AddShiftActivity.createIntent(context)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            widget.setOnClickPendingIntent(R.id.add_icon,
                    getActivity(context, id, intent, FLAG_UPDATE_CURRENT));

            appWidgetManager.updateAppWidget(id, widget);
        }
    }
}
