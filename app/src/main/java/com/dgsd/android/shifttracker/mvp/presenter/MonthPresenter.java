package com.dgsd.android.shifttracker.mvp.presenter;

import android.support.annotation.NonNull;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.data.AppSettings;
import com.dgsd.android.shifttracker.manager.AnalyticsManager;
import com.dgsd.android.shifttracker.module.AppServicesComponent;
import com.dgsd.android.shifttracker.mvp.view.MonthMvpView;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.model.MonthYear;
import com.dgsd.shifttracker.model.Shift;
import com.dgsd.shifttracker.model.ShiftMonthMapping;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

@SuppressWarnings("deprecation")
public class MonthPresenter extends Presenter<MonthMvpView> {

    @Inject
    AppSettings appSettings;

    @Inject
    DataProvider dataProvider;

    final MonthYear monthYear;

    private Date selectedDate;

    private ShiftMonthMapping shiftsByMonthDay;

    public MonthPresenter(@NonNull MonthMvpView view, AppServicesComponent component, MonthYear monthYear) {
        super(view, component);
        component.inject(this);

        this.monthYear = monthYear;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setStartDayOfWeek(appSettings.startDayOfWeek().get(AppSettings.Defaults.startDayOfWeek()));

        final Calendar cal = Calendar.getInstance();
        if (selectedDate == null) {
            if (monthYear.isCurrent()) {
                getView().selectDay(cal.get(Calendar.DAY_OF_MONTH));
            } else {
                getView().selectDay(1);
            }
        } else {
            cal.setTime(selectedDate);
            getView().selectDay(cal.get(Calendar.DAY_OF_MONTH));
        }

        reloadShifts();
    }

    public MonthYear getMonthYear() {
        return monthYear;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void onDateSelected(Date date) {
        selectedDate = date;

        getView().setDayTitle(getContext().getString(
                R.string.month_fragment_title_template, TimeUtils.formatAsDate(date)));

        if (shiftsByMonthDay != null) {
            List<Shift> shifts = shiftsByMonthDay.getMapping().get(TimeUtils.getMonthDay(date));
            getView().showShifts(shifts);
        }
    }

    private void reloadShifts() {
        Observable<ShiftMonthMapping> shiftsObservable =
                dataProvider.getShiftsBetween(monthYear.startMillis(), monthYear.endMillis())
                        .map(new Func1<List<Shift>, ShiftMonthMapping>() {
                            @Override
                            public ShiftMonthMapping call(List<Shift> shifts) {
                                return new ShiftMonthMapping(shifts);
                            }
                        });

        bind(shiftsObservable, new SimpleSubscriber<ShiftMonthMapping>() {
            @Override
            public void onNext(ShiftMonthMapping shiftsByMonthDay) {
                getView().setDaysMarked(shiftsByMonthDay.getMonthDaysWithShifts());

                MonthPresenter.this.shiftsByMonthDay = shiftsByMonthDay;
                if (selectedDate != null) {
                    onDateSelected(selectedDate);
                }
            }
        });
    }

    public void onShiftClicked(Shift shift) {
        AnalyticsManager.trackClick("shift");
        getView().showShift(shift);
    }

    public void onDeleteShiftClicked(Shift shift) {
        AnalyticsManager.trackClick("delete_shift");
        bind(dataProvider.removeShift(shift.id()), new SimpleSubscriber<Void>() {
            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Error deleting shift");
                getView().showError(getContext().getString(R.string.error_deleting_shift));
            }
        });
    }

    public void onCloneShiftClicked(Shift shift) {
        AnalyticsManager.trackClick("clone_shift");
        getView().cloneShift(shift);
    }

    public void onExportShiftClicked(Shift shift) {
        AnalyticsManager.trackClick("export_shift");
        getView().exportToCalendar(shift);
    }
}
