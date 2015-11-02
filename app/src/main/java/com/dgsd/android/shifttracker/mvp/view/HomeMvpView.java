package com.dgsd.android.shifttracker.mvp.view;

import com.dgsd.shifttracker.model.Shift;

import java.util.List;

public interface HomeMvpView extends MvpView {

    void closeDraw();

    void sendSupportEmail(String emailSubject, String supportEmail);

    void showRateApp();

    void addNewShift();

    void showSettings();

    void showWeekView(int startDayOfWeek);

    void showMonthView();

    void showStatistics();

    void showRateAppPrompt(String title, String message);

    void showAddNewShiftFromTemplate(List<Shift> templateShifts);

    void addShiftFromTemplate(Shift shift);

    void editTemplateShift(Shift shift);

    void showError(String message);

    void showAd();
}
