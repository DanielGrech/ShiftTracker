package com.dgsd.android.shifttracker.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.STApp;
import com.dgsd.android.shifttracker.activity.ViewShiftActivity;
import com.dgsd.android.shifttracker.data.AppSettings;
import com.dgsd.android.shifttracker.data.AppSettings.Defaults;
import com.dgsd.android.shifttracker.util.ModelUtils;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.model.Shift;
import com.dgsd.shifttracker.model.ShiftWeekMapping;
import com.dgsd.shifttracker.model.TimePeriod;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static android.text.format.DateUtils.FORMAT_ABBREV_ALL;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;
import static android.text.format.DateUtils.formatDateTime;

public class ShiftListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ShiftListViewFactory(getApplicationContext(), intent);
    }

    private static class ShiftListViewFactory implements RemoteViewsFactory {

        private final Context context;
        private final DataProvider dataProvider;
        private final AppSettings appSettings;
        private final AppWidgetManager appWidgetManager;
        private final int appWidgetId;
        private final Calendar calendar;
        private long weekStartMillis;
        private Subscription currentSubscription;

        /**
         * Holds either Shifts or Integers representing Julian Days
         */
        private final List<Object> shiftsAndDayHeaders;

        public ShiftListViewFactory(Context context, Intent intent) {
            this.context = context;
            this.appWidgetManager = AppWidgetManager.getInstance(context);
            this.shiftsAndDayHeaders = new ArrayList<>();
            this.calendar = Calendar.getInstance();
            this.appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            this.dataProvider = ((STApp) context.getApplicationContext())
                    .getAppServicesComponent()
                    .dataProvider();
            this.appSettings = ((STApp) context.getApplicationContext())
                    .getAppServicesComponent()
                    .appSettings();
        }

        @Override
        public void onCreate() {
            reloadData();
        }

        @Override
        public void onDataSetChanged() {
            reloadData();
        }

        @Override
        public void onDestroy() {
            unsubscribeFromUpdates();
        }

        @Override
        public int getCount() {
            return shiftsAndDayHeaders.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (position < 0 || position >= shiftsAndDayHeaders.size()) {
                return null;
            }

            final Object item = shiftsAndDayHeaders.get(position);
            if (item instanceof Shift) {
                return getViewForShift((Shift) shiftsAndDayHeaders.get(position));
            } else if (item instanceof Integer) {
                return getViewForDayHeader((Integer) shiftsAndDayHeaders.get(position));
            } else {
                throw new IllegalStateException("Unknown object: " + item);
            }
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 2; // Shift + Header
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        private RemoteViews getViewForDayHeader(Integer offset) {
            calendar.setTimeInMillis(weekStartMillis + TimeUnit.DAYS.toMillis(offset));

            final RemoteViews header
                    = new RemoteViews(context.getPackageName(), R.layout.widget_shift_list_day_header);
            header.setTextViewText(R.id.text, TimeUtils.formatAsDate(calendar.getTime()));
            return header;
        }

        private RemoteViews getViewForShift(Shift shift) {
            final RemoteViews widget
                    = new RemoteViews(context.getPackageName(), R.layout.widget_shift_list_item);

            final String title;
            if (TextUtils.isEmpty(shift.title())) {
                title = context.getString(R.string.default_shift_title);
            } else {
                title = shift.title();
            }

            final String dateText = formatDateTime(context, shift.timePeriod().startMillis(),
                    FORMAT_ABBREV_ALL | FORMAT_SHOW_DATE | FORMAT_SHOW_TIME);
            String body = dateText + " - "
                    + TimeUtils.formatDuration(shift.totalPaidDuration());
            final float totalPay = shift.totalPay();
            if (Float.compare(totalPay, 0) > 0) {
                body += " - " + ModelUtils.formatCurrency(totalPay);
            }

            widget.setTextViewText(R.id.title, title);
            widget.setTextViewText(R.id.summary, body);

            final String payText = Float.compare(totalPay, 0) > 0 ?
                    ModelUtils.formatCurrency(totalPay) : null;
            if (TextUtils.isEmpty(payText)) {
                widget.setTextViewText(R.id.pay, null);
                widget.setViewVisibility(R.id.pay, View.GONE);
            } else {
                widget.setTextViewText(R.id.pay, payText);
                widget.setViewVisibility(R.id.pay, View.VISIBLE);
            }

            widget.setOnClickFillInIntent(R.id.container,
                    ViewShiftActivity.createIntentFromReminder(context, shift.id()));

            return widget;
        }

        private void reloadData() {
            unsubscribeFromUpdates();

            final int startDay = appSettings.startDayOfWeek().get(Defaults.startDayOfWeek());
            final TimePeriod week = TimeUtils.getWeekTimePeriod(
                    TimeUtils.getJulianDay(System.currentTimeMillis()),
                    startDay
            );

            weekStartMillis = week.startMillis();
            currentSubscription = dataProvider.getShiftsBetween(week.startMillis(), week.endMillis())
                    .map(new Func1<List<Shift>, Map<Integer, List<Shift>>>() {
                        @Override
                        public Map<Integer, List<Shift>> call(List<Shift> shifts) {
                            return new ShiftWeekMapping(startDay, shifts).getMapping();
                        }
                    })
                    .map(new Func1<Map<Integer, List<Shift>>, List<Object>>() {
                        @Override
                        public List<Object> call(Map<Integer, List<Shift>> dayToShiftListMapping) {
                            return convertDayToShiftListMapping(dayToShiftListMapping);
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.immediate())
                    .subscribe(new Action1<List<Object>>() {
                        @Override
                        public void call(List<Object> shiftAndDays) {
                            if (!shiftsAndDayHeaders.equals(shiftAndDays)) {
                                shiftsAndDayHeaders.clear();
                                shiftsAndDayHeaders.addAll(shiftAndDays);
                                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list);
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable error) {
                            Timber.e(error, "Error getting shifts");
                            // TODO: Show error!
                        }
                    });
        }

        private List<Object> convertDayToShiftListMapping(Map<Integer, List<Shift>> dayToShiftListMapping) {
            final List<Object> retval = new LinkedList<>();

            int offset = 0;
            for (final Map.Entry<Integer, List<Shift>> entry : dayToShiftListMapping.entrySet()) {
                final List<Shift> shifts = entry.getValue();

                if (shifts != null && !shifts.isEmpty()) {
                    retval.add(offset);
                    for (Shift shift : shifts) {
                        retval.add(shift);
                    }
                }

                offset++;
            }
            return retval;
        }

        private void unsubscribeFromUpdates() {
            if (currentSubscription != null && !currentSubscription.isUnsubscribed()) {
                currentSubscription.unsubscribe();
            }

            currentSubscription = null;
        }
    }
}
