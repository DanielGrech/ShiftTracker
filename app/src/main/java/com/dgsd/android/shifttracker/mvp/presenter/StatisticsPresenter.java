package com.dgsd.android.shifttracker.mvp.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.Time;

import com.dgsd.android.shifttracker.manager.AdManager;
import com.dgsd.android.shifttracker.manager.AnalyticsManager;
import com.dgsd.android.shifttracker.manager.StatisticsEngine;
import com.dgsd.android.shifttracker.module.AppServicesComponent;
import com.dgsd.android.shifttracker.mvp.view.StatisticsMvpView;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.model.Shift;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

@SuppressWarnings("deprecation")
public class StatisticsPresenter extends Presenter<StatisticsMvpView> {

    private static final int DEFAULT_DAY_RANGE = 7; // 1 week

    private final Calendar calendar = Calendar.getInstance();

    @Inject
    DataProvider dataProvider;

    @Inject
    AdManager adManager;

    public StatisticsPresenter(@NonNull StatisticsMvpView view, AppServicesComponent component) {
        super(view, component);
        component.inject(this);
    }

    @Override
    public void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

        if (savedInstanceState == null) {
            final Calendar cal = Calendar.getInstance();

            cal.setTimeInMillis(System.currentTimeMillis());
            getView().setEndDate(cal.getTime());

            cal.add(Calendar.DAY_OF_MONTH, -DEFAULT_DAY_RANGE);
            getView().setStartDate(cal.getTime());
        }

        if (adManager.shouldShowAd()) {
            getView().showAd();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsManager.trackScreenView("statistics");
        reloadStatistics();
    }

    public void onStartDateChanged(Date date) {
        AnalyticsManager.trackClick("start_date");
        getView().setStartDate(date);
        if (date.after(getView().getEndDate())) {
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, DEFAULT_DAY_RANGE);
            getView().setEndDate(calendar.getTime());
        }

        reloadStatistics();
    }

    public void onEndDateChanged(Date date) {
        AnalyticsManager.trackClick("end_date");
        getView().setEndDate(date);
        if (date.before(getView().getStartDate())) {
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, -DEFAULT_DAY_RANGE);
            getView().setStartDate(calendar.getTime());
        }

        reloadStatistics();
    }

    private void reloadStatistics() {
        final long from = getStartTime();
        final long to = getEndTime();

        final Observable<StatisticsEngine.Results> observable =
                dataProvider.getShiftsBetween(from, to)
                        .take(1)
                        .map(new Func1<List<Shift>, StatisticsEngine>() {
                            @Override
                            public StatisticsEngine call(List<Shift> shifts) {
                                return new StatisticsEngine(shifts, from, to);
                            }
                        })
                        .map(new Func1<StatisticsEngine, StatisticsEngine.Results>() {
                            @Override
                            public StatisticsEngine.Results call(StatisticsEngine engine) {
                                return engine.calculate();
                            }
                        });

        bind(observable, new SimpleSubscriber<StatisticsEngine.Results>() {
            @Override
            public void onNext(StatisticsEngine.Results results) {
                getView().showShiftCount(results.getShiftCount());
                getView().showTotalEarnings(results.getTotalEarnings());
                getView().showTotalTimeWorked(results.getTotalTimeWorked());
                getView().showAverageEarningsPerDay(results.getAverageEarningsByDay());
                getView().showAverageTimeWorkedPerDay(results.getAverageTimeWorkedByDay());
                getView().showMostValuableShift(results.getMaxEarningShiftAmount());
                getView().showLeastValuableShift(results.getMinEarningShiftAmount());
                getView().showShortestShift(results.getShortestShiftDuration());
                getView().showLongestShift(results.getLongestShiftDuration());
                getView().showAverageEarningsPerShift(results.getAverageEarningsPerItem());
                getView().showAverageTimeWorkedPerShift(results.getAverageTimeWorkedPerItem());
                getView().showEarningsPerDay(results.mapDaysToEarnings());
                getView().showTimeWorkedPerDay(results.mapDaysToTimeWorked());
            }
        });
    }

    private long getStartTime() {
        final Time time = new Time();

        time.hour = 0;
        time.minute = 0;
        time.second = 0;
        time.normalize(false);

        return TimeUtils.toDateTime(getView().getStartDate(), time);
    }

    private long getEndTime() {
        final Time time = new Time();

        time.hour = 23;
        time.minute = 59;
        time.second = 59;
        time.normalize(false);

        return TimeUtils.toDateTime(getView().getEndDate(), time);
    }
}
