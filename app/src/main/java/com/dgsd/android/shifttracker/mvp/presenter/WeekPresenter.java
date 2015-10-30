package com.dgsd.android.shifttracker.mvp.presenter;

import android.support.annotation.NonNull;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.data.AppSettings;
import com.dgsd.android.shifttracker.data.AppSettings.Defaults;
import com.dgsd.android.shifttracker.manager.AnalyticsManager;
import com.dgsd.android.shifttracker.module.AppServicesComponent;
import com.dgsd.android.shifttracker.mvp.view.WeekMvpView;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.model.Shift;
import com.dgsd.shifttracker.model.ShiftWeekMapping;
import com.dgsd.shifttracker.model.TimePeriod;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

public class WeekPresenter extends Presenter<WeekMvpView> {

    @Inject
    DataProvider dataProvider;

    @Inject
    AppSettings appSettings;

    final TimePeriod timePeriod;

    public WeekPresenter(@NonNull WeekMvpView view, AppServicesComponent component, TimePeriod timePeriod) {
        super(view, component);
        component.inject(this);
        this.timePeriod = timePeriod;
    }

    @Override
    public void onResume() {
        super.onResume();

        reloadShifts();
    }

    private void reloadShifts() {
        Observable<ShiftWeekMapping> mappingObservable =
                dataProvider.getShiftsBetween(timePeriod.startMillis(), timePeriod.endMillis())
                        .map(new Func1<List<Shift>, ShiftWeekMapping>() {
                            @Override
                            public ShiftWeekMapping call(List<Shift> shifts) {
                                final ShiftWeekMapping mapping = new ShiftWeekMapping(
                                        appSettings.startDayOfWeek().get(Defaults.startDayOfWeek()), shifts);
                                // Make sure internal cache is populated
                                mapping.getMapping();
                                return mapping;
                            }
                        });

        bind(mappingObservable, new SimpleSubscriber<ShiftWeekMapping>() {
            @Override
            public void onNext(ShiftWeekMapping weekMapping) {
                getView().showShifts(weekMapping);
            }
        });
    }

    public long getStartOfWeek() {
        return timePeriod.startMillis();
    }

    public void onShiftClicked(Shift shift) {
        AnalyticsManager.trackClick("shift");
        getView().showShift(shift);
    }

    public void onAddShiftClicked(Date date) {
        AnalyticsManager.trackClick("add_shift");
        getView().addShiftAt(date);
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
