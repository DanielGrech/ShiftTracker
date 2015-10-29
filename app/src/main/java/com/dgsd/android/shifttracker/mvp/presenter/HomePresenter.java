package com.dgsd.android.shifttracker.mvp.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.dgsd.android.shifttracker.BuildConfig;
import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.data.AppSettings;
import com.dgsd.android.shifttracker.data.AppSettings.Defaults;
import com.dgsd.android.shifttracker.manager.AdManager;
import com.dgsd.android.shifttracker.manager.AnalyticsManager;
import com.dgsd.android.shifttracker.manager.AppRatingManager;
import com.dgsd.android.shifttracker.module.AppServicesComponent;
import com.dgsd.android.shifttracker.mvp.view.HomeMvpView;
import com.dgsd.android.shifttracker.util.EnumUtils;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.model.Shift;
import com.dgsd.shifttracker.model.TimePeriod;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static com.dgsd.android.shifttracker.util.TimeUtils.toDateTime;
import static com.dgsd.android.shifttracker.util.TimeUtils.toTime;

public class HomePresenter extends Presenter<HomeMvpView> {

    public enum ViewType {
        MONTH,
        WEEK
    }

    @Inject
    AppSettings appSettings;

    @Inject
    AppRatingManager appRatingManager;

    @Inject
    DataProvider dataProvider;

    @Inject
    AdManager adManager;

    private List<Shift> templateShifts = new LinkedList<>();

    private int originalStartDay;

    public HomePresenter(@NonNull HomeMvpView view, AppServicesComponent component) {
        super(view, component);
        component.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appRatingManager.logAppStart();
        if (appRatingManager.shouldShowRatingDialog()) {
            getView().showRateAppPrompt(
                    getContext().getString(R.string.app_rating_prompt_title),
                    getContext().getString(R.string.app_rating_prompt_message)
            );
            appRatingManager.setHasShownRatingDialog(true);
        }

        originalStartDay = appSettings.startDayOfWeek().get(Defaults.startDayOfWeek());
    }

    @Override
    public void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        setupSelectedView();

        if (adManager.shouldShowAd()) {
            getView().showAd();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bind(dataProvider.getTemplateShifts(), new SimpleSubscriber<List<Shift>>() {
            @Override
            public void onNext(List<Shift> shifts) {
                templateShifts.clear();
                if (shifts != null) {
                    templateShifts.addAll(shifts);
                }
            }
        });

        final int currentStartDayPref = appSettings.startDayOfWeek().get(Defaults.startDayOfWeek());
        if (currentStartDayPref != originalStartDay) {
            originalStartDay = currentStartDayPref;
            setupSelectedView();
        }
    }

    public void onSettingsClicked() {
        AnalyticsManager.trackClick("settings");
        getView().closeDraw();
        getView().showSettings();
    }

    public void onSendFeedbackClicked() {
        AnalyticsManager.trackClick("send_feedback");
        getView().closeDraw();

        final String title = String.format("%s %s support", getContext().getString(R.string.app_name), BuildConfig.VERSION_NAME);
        final String supportEmail = BuildConfig.SUPPORT_EMAIL;
        getView().sendSupportEmail(title, supportEmail);
    }

    public void onRateAppClicked() {
        AnalyticsManager.trackClick("rate_app");
        getView().closeDraw();
        getView().showRateApp();
    }

    public void onAddShiftClicked() {
        AnalyticsManager.trackClick("add_shift");
        getView().closeDraw();

        if (templateShifts.isEmpty()) {
            getView().addNewShift();
        } else {
            getView().showAddNewShiftFromTemplate(templateShifts);
        }
    }

    public void onWeekViewClicked() {
        AnalyticsManager.trackClick("view_by_week");

        appSettings.lastSelectedViewType().put(ViewType.WEEK.ordinal());
        getView().closeDraw();
        getView().showWeekView(appSettings.startDayOfWeek().get(Defaults.startDayOfWeek()));

        AnalyticsManager.trackScreenView("week_view");
    }

    public void onMonthViewClicked() {
        AnalyticsManager.trackClick("view_by_month");

        appSettings.lastSelectedViewType().put(ViewType.MONTH.ordinal());
        getView().closeDraw();
        getView().showMonthView();

        AnalyticsManager.trackScreenView("month_view");
    }

    public void onStatisticsClicked() {
        AnalyticsManager.trackClick("statistics");
        getView().closeDraw();
        getView().showStatistics();
    }

    public void onAddNewShiftClicked() {
        AnalyticsManager.trackClick("add_new_shift");
        getView().addNewShift();
    }

    public void onAddShiftFromTemplateClicked(ViewType viewType, Shift shift, Date selectedDate) {
        AnalyticsManager.trackClick("add_shift_from_template");
        if (ViewType.WEEK.equals(viewType) || selectedDate == null) {
            getView().addShiftFromTemplate(shift);
        } else {
            long startMillis = toDateTime(selectedDate, toTime(shift.timePeriod().startMillis()));

            final Shift editedShift = shift.withId(-1)
                    .withIsTemplate(false)
                    .withTimePeriod(TimePeriod.builder()
                            .startMillis(startMillis)
                            .endMillis(startMillis + shift.timePeriod().durationInMillis())
                            .create());
            bind(dataProvider.addShift(editedShift), new SimpleSubscriber<Shift>() {
                @Override
                public void onError(Throwable e) {
                    Timber.e(e, "Error adding shift");
                    getView().showError(getContext().getString(R.string.error_saving_shift));
                }
            });
        }
    }

    private void setupSelectedView() {
        ViewType viewType
                = EnumUtils.from(ViewType.class, appSettings.lastSelectedViewType().get(-1));
        viewType = viewType == null ? ViewType.MONTH : viewType;
        switch (viewType) {
            case MONTH:
                onMonthViewClicked();
                break;
            case WEEK:
                onWeekViewClicked();
                break;
        }
    }
}
