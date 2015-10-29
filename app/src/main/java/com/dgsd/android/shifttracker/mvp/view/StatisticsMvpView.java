package com.dgsd.android.shifttracker.mvp.view;

import android.util.SparseArray;

import java.util.Date;

public interface StatisticsMvpView extends MvpView {

    void setStartDate(Date date);

    void setEndDate(Date date);

    Date getStartDate();

    Date getEndDate();

    void showShiftCount(int shiftCount);

    void showMostValuableShift(float amount);

    void showLeastValuableShift(float amount);

    void showLongestShift(long durationMillis);

    void showShortestShift(long durationMillis);

    void showTotalEarnings(float totalEarnings);

    void showTotalTimeWorked(long totalMillisWorked);

    void showAverageEarningsPerDay(float averageEarningsByDay);

    void showAverageEarningsPerShift(float earnings);

    void showAverageTimeWorkedPerDay(long averageTimeWorkedByDay);

    void showAverageTimeWorkedPerShift(long averageTimeWorkedByShift);

    void showEarningsPerDay(SparseArray<Float> julianDayToEarningMap);

    void showTimeWorkedPerDay(SparseArray<Float> julianDayToTimeWorkedMap);

    void showAd();
}
