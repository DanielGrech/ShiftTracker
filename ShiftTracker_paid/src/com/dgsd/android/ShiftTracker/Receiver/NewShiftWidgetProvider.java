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

public class NewShiftWidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] ids) {
        for (int i = 0, len = ids.length; i < len; i++) {
            //Base widget layout
            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.new_shift_widget_layout);

            Intent openIntent = new Intent(context, EditShiftActivity.class);
            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            openIntent.putExtra(EditShiftActivity.EXTRA_JULIAN_DAY, TimeUtils.getCurrentJulianDay());

            final PendingIntent openPendingIntent = PendingIntent.getActivity(context, ids[i], openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setOnClickPendingIntent(R.id.new_shift_container, openPendingIntent);

            appWidgetManager.updateAppWidget(ids[i], widget);
        }
    }

}
