package com.dgsd.android.ShiftTracker.Service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.dgsd.android.ShiftTracker.Adapter.WeekAdapter;
import com.dgsd.android.ShiftTracker.BuildConfig;
import com.dgsd.android.ShiftTracker.EditShiftActivity;
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.R;
import com.dgsd.android.ShiftTracker.Util.Prefs;
import com.dgsd.android.ShiftTracker.Util.TimeUtils;
import com.dgsd.android.ShiftTracker.Util.UIUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class ShiftListWidgetService extends RemoteViewsService {
    public static final String TAG = ShiftListWidgetService.class.getSimpleName();

    // Minimum delay between queries on the database for widget updates in ms
    private static final int WIDGET_UPDATE_THROTTLE = 500;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return(new ShiftListUpdateReceiver(this.getApplicationContext(), intent));
    }


    public static class ShiftListUpdateReceiver implements RemoteViewsFactory,
            Loader.OnLoadCompleteListener<Cursor> {

        private Context mContext = null;
        private AppWidgetManager mAppWidgetManager;
        private int mAppWidgetId = -1;
        private CursorLoader mLoader;
        private List<Object> mShiftsAndIds; //Holds either Shifts or Integers representing Julian Days

        private Formatter mFormatter;
        private StringBuilder mStringBuilder = new StringBuilder();
        private boolean mIs24Hour = false;
        private boolean mShowIncomePref;

        private NumberFormat mCurrencyFormatter;

        public ShiftListUpdateReceiver(Context context, Intent intent) {
            this.mContext = context;
            mAppWidgetManager = AppWidgetManager.getInstance(mContext);
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            mIs24Hour = DateFormat.is24HourFormat(mContext);
            mFormatter = new Formatter(mStringBuilder);

            mCurrencyFormatter = NumberFormat.getCurrencyInstance();

            mShowIncomePref = Prefs.getInstance(context).get(context.getString(R.string.settings_key_show_income), true);
        }

        @Override
        public void onCreate() {
            initLoader();
        }

        public void initLoader() {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Querying for widget data...");

            if(mLoader != null)
                mLoader.forceLoad();
            else {
                final int startDay = Integer.valueOf(Prefs.getInstance(mContext).get(mContext.getString(R.string.settings_key_start_day), "1"));
                int currentJd = TimeUtils.getCurrentJulianDay();

                mLoader = new WeekAdapter(mContext, null, adjustJulianDay(startDay, currentJd)).getWeeklyLoader(mContext);
                mLoader.setUpdateThrottle(WIDGET_UPDATE_THROTTLE);
                mLoader.registerListener(mAppWidgetId, this);
                mLoader.startLoading();
            }
        }

        @Override
        public void onDestroy() {
            if (mLoader != null)
                mLoader.reset();
        }

        @Override
        public int getCount() {
            return mShiftsAndIds == null ? 0 : mShiftsAndIds.size();
        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(mContext.getPackageName(), R.layout.widget_loading);
        }

        @Override
        public RemoteViews getViewAt(int pos) {
            if (pos < 0 || pos > getCount() || mShiftsAndIds == null || mShiftsAndIds.isEmpty())
                return null;

            if(pos < mShiftsAndIds.size() && mShiftsAndIds.get(pos) instanceof Integer) {
                //We've got a header!
                RemoteViews header = new RemoteViews(mContext.getPackageName(), R.layout.list_widget_heading);
                header.setTextViewText(R.id.text, getDayOfWeekString((Integer) mShiftsAndIds.get(pos), TimeUtils.getCurrentJulianDay()));
                return header;
            }

            RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.list_widget_shift);

            final Shift shift = (Shift) mShiftsAndIds.get(pos);
            row.setTextViewText(R.id.name, shift.name);
            row.setTextViewText(R.id.time, getTimeText(shift));

            String payText = getPayText(shift);
            if(mShowIncomePref && !TextUtils.isEmpty(payText)) {
                row.setTextViewText(R.id.pay, payText);
                row.setViewVisibility(R.id.pay, View.VISIBLE);
            } else {
                row.setTextViewText(R.id.pay, null);
                row.setViewVisibility(R.id.pay, View.GONE);
            }

            if(TextUtils.isEmpty(shift.note)) {
                row.setViewVisibility(R.id.note, View.GONE);
            } else {
                row.setTextViewText(R.id.note, shift.note);
                row.setViewVisibility(R.id.note, View.VISIBLE);
            }


            Intent intent = new Intent();
            intent.putExtra(EditShiftActivity.EXTRA_SHIFT, shift);
            row.setOnClickFillInIntent(R.id.container, intent);
            return row;
        }

        @Override
        public int getViewTypeCount() {
            return 2; // shifts + header
        }

        @Override
        public long getItemId(int pos) {
            if(mShiftsAndIds == null || pos >= mShiftsAndIds.size())
                return -1;

            Object o = mShiftsAndIds.get(pos);
            if(o instanceof Integer)
                return (Integer) o;
            else
                return ((Shift) o).id;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void onDataSetChanged() {
            initLoader();
        }

        @Override
        public void onLoadComplete(Loader<Cursor> cursorLoader, Cursor cursor) {
            List<Object> newShifts = new ArrayList<Object>();
            int prevJd = -1;

            if(cursor != null && cursor.moveToFirst()) {
                do {
                    Shift shift = Shift.fromCursor(cursor);
                    final int jdToCompare = shift.julianDay;

                    if(prevJd != jdToCompare)
                        newShifts.add(jdToCompare);

                    newShifts.add(shift);
                    prevJd = shift.julianDay;
                } while(cursor.moveToNext());
            }

            if(cursor != null && !cursor.isClosed())
                cursor.close();

            if(mShiftsAndIds == null || !newShifts.equals(mShiftsAndIds)) {
                mShiftsAndIds = newShifts;
                mAppWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetId, R.id.list);
            }
        }

        private String getDayOfWeekString(int julianDay, int todayJulianDay) {
            Time time = new Time();
            time.setJulianDay(julianDay);

            time.setJulianDay(julianDay);
            return DateUtils.formatDateTime(mContext, time.toMillis(true),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY |
                            DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_NO_YEAR);
        }

        private String getTimeText(Shift shift) {
            int flags = DateUtils.FORMAT_SHOW_TIME;
            if(mIs24Hour)
                flags |= DateUtils.FORMAT_24HOUR;

            mStringBuilder.setLength(0);
            String time = DateUtils.formatDateRange(mContext, mFormatter, shift.getStartTime(), shift.getEndTime(), flags).toString();
            time += " (" + UIUtils.getDurationAsHours(shift.getDurationInMinutes()) + ")";
            return time;
        }

        private String getPayText(Shift shift) {
            if(shift.payRate <= 0.01)
                return null;

            return mCurrencyFormatter.format(shift.getIncome());
        }
    }

    private static int adjustJulianDay(int startWeekday, int jd) {
        Time time = new Time();
        time.setJulianDay(jd);
        if(time.weekDay == startWeekday) {
            //Great, no adjustment needed
            return jd;
        } else {
            while(time.weekDay != startWeekday)
                time.setJulianDay(jd--);
            return TimeUtils.getJulianDay(time);
        }
    }
}
