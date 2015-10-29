package com.dgsd.android.shifttracker.mvp.view;

import android.text.format.Time;

import com.dgsd.shifttracker.model.ColorItem;
import com.dgsd.shifttracker.model.ReminderItem;
import com.dgsd.shifttracker.model.Shift;

import java.util.Date;

@SuppressWarnings("deprecation")
public interface AddShiftMvpView extends MvpView {

    void showStartDate(Date date);

    void showStartTime(Time time);

    void showEndDate(Date date);

    void showEndTime(Time time);

    void showUnpaidBreakDuration(long durationInMins);

    void showReminders(ReminderItem[] reminders);

    void showReminder(ReminderItem reminder);

    void showColors(ColorItem[] colors);

    void showColor(ColorItem color);

    void showPayRate(float payRate);

    void hideOvertime();

    void showOvertime();

    void showTitle(String title);

    void showNotes(String notes);

    void showOvertimePayRate(float payRate);

    void showOvertimeStartDate(Date date);

    void showOvertimeStartTime(Time time);

    void showOvertimeEndDate(Date date);

    void showOvertimeEndTime(Time time);

    void showSaveAsTemplate(boolean showAsSave);

    Date getCurrentStartDate();

    Date getCurrentEndDate();

    Date getCurrentOvertimeStartDate();

    Date getCurrentOvertimeEndDate();

    Time getCurrentStartTime();

    Time getCurrentEndTime();

    Time getCurrentOvertimeStartTime();

    Time getCurrentOvertimeEndTime();

    Shift getShift();

    boolean isOvertimeShowing();

    void showError(String message);

    void showShiftList();

    void showAd();
}
