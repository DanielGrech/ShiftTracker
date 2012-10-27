package com.dgsd.android.ShiftTracker.Receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.RemoteViews;
import com.dgsd.android.ShiftTracker.EditShiftActivity;
import com.dgsd.android.ShiftTracker.R;
import com.dgsd.android.ShiftTracker.Service.ShiftListWidgetService;
import com.dgsd.android.ShiftTracker.StApp;

public class ShiftListWidgetProvider extends AppWidgetProvider {

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context, ShiftListWidgetProvider.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String a = intent.getAction();

        if(a.equals(context.getString(R.string.action_update_list_widget)) ||
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
        for (int i = 0, len = ids.length; i < len; i++) {
            //Base widget layout
            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.shift_list_widget_layout);

            //Launch the 'EditShiftActivity' when clicking on the add button
            final Intent addIntent = new Intent(context, EditShiftActivity.class);
            addIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            final PendingIntent addPendingIntent = PendingIntent.getActivity(context, ids[i], addIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setOnClickPendingIntent(R.id.add_button, addPendingIntent);

            // Launch calendar app when the user taps on the header
            final Intent headerIntent = new Intent(context, StApp.getHomeClass(context));
            headerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            final PendingIntent launchCalendarPendingIntent = PendingIntent.getActivity(context, ids[i], headerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setOnClickPendingIntent(R.id.header, launchCalendarPendingIntent);

            //Set up listview adapter
            Intent svcIntent = new Intent(context, ShiftListWidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, ids[i]);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
            widget.setRemoteAdapter(R.id.list, svcIntent);
            appWidgetManager.notifyAppWidgetViewDataChanged(ids[i], R.id.list);

            //Empty view for list
            widget.setEmptyView(R.id.list, android.R.id.empty);

            //Intent for each list item
            Intent intentTemplate = new Intent(context, EditShiftActivity.class);
            intentTemplate.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntentTemplate = PendingIntent.getActivity(context, ids[i], intentTemplate, PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setPendingIntentTemplate(R.id.list, pendingIntentTemplate);

            //..aaaaand BOOM! We're good to go!
            appWidgetManager.updateAppWidget(ids[i], widget);
        }
    }

    private static String mUpdateAction = null;
    public static void triggerUpdate(Context context)  {
        if(TextUtils.isEmpty(mUpdateAction))
            mUpdateAction = context.getString(R.string.action_update_list_widget);

        context.sendBroadcast(new Intent(mUpdateAction));
    }
}
